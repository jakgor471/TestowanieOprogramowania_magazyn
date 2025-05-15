package server;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;

import shared.DataValidation;
import shared.Permission;
import shared.User;
import shared.User.Gender;

/**
 * Klasa implementująca metody do obsługi komunikacji serwera z bazą danych.
 * @author jakgor471
 *
 */
public class DBServer implements Closeable {
	private Connection dbConn;
	
	/**
	 * Tworzy połączenie z bazą danych, jeśli baza nie istnieje - tworzy ją za pomocą skryptu magazyn.sql.
	 */
	public DBServer() {
		boolean createDb = !(new File("magazyn.db")).exists();
		
		try{
			dbConn = DriverManager.getConnection("jdbc:sqlite:magazyn.db");
		} catch (SQLException e) {
			e.printStackTrace();
			dbConn = null;
		}
		
		if(createDb && dbConn != null) {
			try {
				executeSqlFile(DBServer.class.getResourceAsStream("/server/magazyn.sql"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void close() {
		if(dbConn != null) {
			try {
				dbConn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Wykonuje kwerendy ze strumienia wejściowego.
	 * @param is Strumień znaków
	 * @throws IOException
	 */
	public void executeSqlFile(InputStream is) throws IOException {
		boolean begin = false;
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
		StringBuilder content = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			content.append(line).append("\n");
			
			if(line.toUpperCase().startsWith("BEGIN") && !line.toUpperCase().startsWith("BEGIN TRANSACTION"))
				begin = true;
			
			if(!begin && line.endsWith(";") || begin && line.toUpperCase().endsWith("END;")) {
				try {
					Statement stmt = dbConn.createStatement();
					System.out.println(content.toString());
					stmt.execute(content.toString());
					content.setLength(0);
					
					begin = false;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public HashSet<Permission> getUserPermissions(String login){
		try {
			PreparedStatement stmt2 = dbConn.prepareStatement("SELECT idUprawnienia FROM uzytkownicyUprawnienia WHERE uzytkownikLogin = ?;");
			
			stmt2.setString(1, login);
			ResultSet rs2 = stmt2.executeQuery();
			
			HashSet<Permission> set = new HashSet<>();
			
			while(rs2.next()) {
				int idUprawnienia = rs2.getInt(1);
				
				if(idUprawnienia < 1 || idUprawnienia > Permission.values().length)
					continue;
				
				set.add(Permission.values()[idUprawnienia - 1]);
			}
			
			return set;
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public ArrayList<User> getUsers(){
		ArrayList<User> users = new ArrayList<User>();
		
		try {
			PreparedStatement stmt = dbConn.prepareStatement("SELECT uzytkownicy.*, adresy.*\r\n"
					+ "FROM uzytkownicy\r\n"
					+ "LEFT JOIN (\r\n"
					+ "    SELECT ua.uzytkownikLogin, a.*\r\n"
					+ "    FROM uzytkownicyAdresy ua\r\n"
					+ "    JOIN adresy a ON ua.adresId = a.id\r\n"
					+ "    WHERE a.id = (\r\n"
					+ "        SELECT MIN(a2.id) \r\n"
					+ "        FROM adresy a2 \r\n"
					+ "        JOIN uzytkownicyAdresy ua2 ON a2.id = ua2.adresId\r\n"
					+ "        WHERE ua2.uzytkownikLogin = ua.uzytkownikLogin\r\n"
					+ "    )\r\n"
					+ ") adresy ON uzytkownicy.login = adresy.uzytkownikLogin;");
			
			PreparedStatement stmt2 = dbConn.prepareStatement("SELECT idUprawnienia FROM uzytkownicyUprawnienia WHERE uzytkownikLogin = ?;");
			
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()) {
				try {
					User user = new User();
					user.setLogin(rs.getString("login"));
					user.setName(rs.getString("imie"));
					user.setLastname(rs.getString("nazwisko"));
					user.setNrPesel(rs.getString("nrPesel"));
					user.setBirthDate(DataValidation.stringToDate(rs.getString("dataUrodzenia")));
					user.setGender(Gender.values()[rs.getInt("plec")]);
					user.setEmail(rs.getString("email"));
					user.setNrTel(rs.getString("numerTelefonu"));
					user.setForgotten(rs.getBoolean("zapomniany"));
					
					if(user.isForgotten()) {
						user.setForgottenBy(rs.getString("zapomnianyPrzez"));
						user.setForgottenDate(DataValidation.stringToDate(rs.getString("dataZapomnienia")));
					}
					
					user.getAdres().setKodPocztowy(rs.getString("kodPocztowy"));
					user.getAdres().setMiejscowosc(rs.getString("miejscowosc"));
					user.getAdres().setUlica(rs.getString("ulica"));
					user.getAdres().setNrPosesji(rs.getString("nrPosesji"));
					user.getAdres().setNrLokalu(rs.getString("nrLokalu"));
					
					user.setPermissions(getUserPermissions(rs.getString("login")));
					
					users.add(user);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return users;
	}
	
	/**
	 * 
	 * @param user Użytkownik do dodania do bazy danych.
	 * @throws IllegalArgumentException Jeśli login użytkownika nie jest unikalny w bazie danych.
	 */
	public void addUser(User user) throws IllegalArgumentException{
		DataValidation.validatePesel(user.getNrPesel(), user.getBirthDate(), user.getGender());
		DataValidation.validateEmail(user.getEmail());
		DataValidation.validateNrTel(user.getNrTel());
		
		try {
			PreparedStatement stmt = dbConn.prepareStatement("SELECT Count(login) FROM uzytkownicy WHERE login = ?;");
			stmt.setString(1, user.getLogin());
			
			ResultSet rs = stmt.executeQuery();
			if(rs.next() && rs.getInt(1) > 0)
				throw new IllegalArgumentException("Nieunikalny login użytkownika!");
			
			stmt = dbConn.prepareStatement("INSERT INTO uzytkownicy (login, imie, nazwisko, nrPesel, dataUrodzenia, plec, email, numerTelefonu, zapomniany) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);");
			stmt.setString(1, user.getLogin());
			stmt.setString(2, user.getName());
			stmt.setString(3, user.getLastname());
			stmt.setString(4, user.getNrPesel());
			stmt.setString(5, DataValidation.dateToSqlString(user.getBirthDate()));
			stmt.setInt(6, user.getGender().ordinal());
			stmt.setString(7, user.getEmail());
			stmt.setString(8, user.getNrTel());
			stmt.setBoolean(9, user.isForgotten());
			
			stmt.executeUpdate();
			
			stmt = dbConn.prepareStatement("INSERT INTO adresy (miejscowosc, kodPocztowy, ulica, nrPosesji, nrLokalu) VALUES (?, ?, ?, ?, ?);");
			stmt.setString(1, user.getAdres().getMiejscowosc());
			stmt.setString(2, user.getAdres().getKodPocztowy());
			stmt.setString(3, user.getAdres().getUlica());
			stmt.setString(4, user.getAdres().getNrPosesji());
			stmt.setString(5, user.getAdres().getNrLokalu());
			
			stmt.executeUpdate();

			stmt = dbConn.prepareStatement("INSERT INTO uzytkownicyAdresy (uzytkownikLogin, adresId) VALUES (?, last_insert_rowid());");
			stmt.setString(1, user.getLogin());
			
			stmt.executeUpdate();
			
			stmt = dbConn.prepareStatement("INSERT INTO uzytkownicyUprawnienia VALUES (?, 1);");
			stmt.setString(1, user.getLogin());
			stmt.executeUpdate();
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void editUser(User user, String oldLogin) throws IllegalArgumentException {
		DataValidation.validatePesel(user.getNrPesel(), user.getBirthDate(), user.getGender());
		DataValidation.validateEmail(user.getEmail());
		DataValidation.validateNrTel(user.getNrTel());
		
		try {
			dbConn.createStatement().execute("PRAGMA foreign_keys = ON;");
			PreparedStatement stmt = dbConn.prepareStatement("SELECT Count(login) FROM uzytkownicy WHERE login = ?;");
			
			ResultSet rs;
			stmt.setString(1, user.getLogin());
			rs = stmt.executeQuery();
			
			if(!rs.next())
				throw new RuntimeException("Nie znaleziono wskazanego użytkownika!");
				
			if(oldLogin != null && oldLogin.equals(user.getLogin())) {
				if(rs.getInt(1) < 1)
					throw new RuntimeException("Nie znaleziono wskazanego użytkownika!");
			} else if(rs.getInt(1) > 0)
				throw new IllegalArgumentException("Nieunikalny login użytkownika!");
			
			stmt = dbConn.prepareStatement("UPDATE uzytkownicy SET login = ?, imie = ?, nazwisko = ?, nrPesel = ?, dataUrodzenia = ?, plec = ?, email = ?, numerTelefonu = ?, zapomniany = ?, dataZapomnienia = ?, zapomnianyPrzez = ? WHERE login = ?;");
			stmt.setString(1, user.getLogin());
			stmt.setString(2, user.getName());
			stmt.setString(3, user.getLastname());
			stmt.setString(4, user.getNrPesel());
			stmt.setString(5, DataValidation.dateToSqlString(user.getBirthDate()));
			stmt.setInt(6, user.getGender().ordinal());
			stmt.setString(7, user.getEmail());
			stmt.setString(8, user.getNrTel());
			stmt.setBoolean(9, user.isForgotten());
			
			if(user.isForgotten()) {
				stmt.setString(10, DataValidation.dateToSqlString(user.getForgottenDate()));
				stmt.setString(11, user.getForgottenBy());
			}
			
			stmt.setString(12, oldLogin);
			
			stmt.executeUpdate();
			
			stmt = dbConn.prepareStatement("UPDATE adresy SET miejscowosc = ?, kodPocztowy = ?, ulica = ?, nrPosesji = ?, nrLokalu = ? WHERE adresy.id = (SELECT adresId FROM uzytkownicyAdresy WHERE uzytkownikLogin = ?);");
			stmt.setString(1, user.getAdres().getMiejscowosc());
			stmt.setString(2, user.getAdres().getKodPocztowy());
			stmt.setString(3, user.getAdres().getUlica());
			stmt.setString(4, user.getAdres().getNrPosesji());
			stmt.setString(5, user.getAdres().getNrLokalu());
			stmt.setString(6, user.getLogin());
			
			stmt.executeUpdate();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void editUserPassword(String login, String passwordHash) throws IllegalArgumentException {
		try {			
			PreparedStatement stmt = dbConn.prepareStatement("SELECT EXISTS (SELECT 1 FROM (SELECT haslo FROM uzytkownicyHasla WHERE uzytkownikLogin = ? ORDER BY id DESC LIMIT 3) WHERE haslo = ?);");
			stmt.setString(1, login);
			stmt.setString(2, passwordHash);
			
			ResultSet rs = stmt.executeQuery();
			rs.next();
			if(rs.getInt(1) != 0)
				throw new IllegalArgumentException("Nowe hasło musi być inne niż 3 poprzednie hasła");
			
			stmt = dbConn.prepareStatement("INSERT INTO uzytkownicyHasla (uzytkownikLogin, haslo) VALUES (?, ?);");
			stmt.setString(2, passwordHash);
			stmt.setString(1, login);
			stmt.executeUpdate();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void editUserPermissions(String login, HashSet<Permission> perms) {
		try {
			dbConn.createStatement().execute("PRAGMA foreign_keys = ON;");
			PreparedStatement stmt = dbConn.prepareStatement("DELETE FROM uzytkownicyUprawnienia WHERE uzytkownikLogin = ?;");
			stmt.setString(1, login);
			stmt.executeUpdate();
			
			stmt = dbConn.prepareStatement("INSERT INTO uzytkownicyUprawnienia VALUES(?, ?);");
			stmt.setString(1, login);
			
			for(Permission p : perms) {
				stmt.setInt(2, p.ordinal() + 1);
				stmt.executeUpdate();
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static enum LoginResult{
		Invalid,
		RegularPassword,
		OneTimePassword
	}
	
	public LoginResult validateUserPassword(String login, String passwordHash) {
		try {
			PreparedStatement stmt = dbConn.prepareStatement(
					"SELECT CASE WHEN uh.id IS NULL OR DATE(wh.dataWygenerowania, '+7 days') <= DATE('now') THEN 0\r\n"
					+ "WHEN wh.hasloId IS NULL THEN 1\r\n"
					+ "ELSE 2 END FROM (SELECT id, haslo FROM uzytkownicyHasla WHERE uzytkownikLogin = ? ORDER BY id DESC LIMIT 1) AS uh\r\n"
					+ "LEFT JOIN (SELECT hasloId, dataWygenerowania FROM wygenerowaneHasla WHERE czyUzyte = 0) wh ON wh.hasloId = uh.id WHERE uh.haslo = ?;"
			);
			stmt.setString(1, login);
			stmt.setString(2, passwordHash);
			
			ResultSet rs = stmt.executeQuery();
			rs.next();
			
			return LoginResult.values()[rs.getInt(1)];
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return LoginResult.Invalid;
	}
	
	public long loginBlockTime(String login, String ip, int maxTries) {
		try {
			PreparedStatement stmt = dbConn.prepareStatement(
					"SELECT czasZalogowania, liczbaProb FROM probyZalogowania WHERE login = ? AND adresIp = ?;"
			);
			stmt.setString(1, login);
			stmt.setString(2, ip);

			ResultSet rs = stmt.executeQuery();
			rs.next();
			
			if(rs.getInt("liczbaProb") >= maxTries)
				return rs.getLong("czasZalogowania");
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public void addLoginAttempt(String login, String ip) {
		try {
			PreparedStatement stmt = dbConn.prepareStatement("INSERT OR IGNORE INTO probyZalogowania (login, adresIp, czasZalogowania, liczbaProb) VALUES (?, ?, ?, 0);");
			stmt.setString(1, login);
			stmt.setString(2, ip);
			stmt.setLong(3, System.currentTimeMillis());
			
			stmt.executeUpdate();
			
			stmt = dbConn.prepareStatement("UPDATE probyZalogowania SET liczbaProb = liczbaProb + 1, czasZalogowania = ? WHERE login = ? AND adresIp = ?;");
			stmt.setLong(1, System.currentTimeMillis());
			stmt.setString(2, login);
			stmt.setString(3, ip);
			
			stmt.executeUpdate();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void resetLoginAttempts(String login, String ip) {
		try {
			PreparedStatement stmt;
			
			stmt = dbConn.prepareStatement("UPDATE probyZalogowania SET liczbaProb = 1 WHERE login = ? AND adresIp = ?;");
			stmt.setString(1, login);
			stmt.setString(2, ip);
			
			stmt.executeUpdate();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean validateUserEmail(String login, String email) {
		try {
			PreparedStatement stmt = dbConn.prepareStatement(
					"SELECT EXISTS (SELECT 1 FROM uzytkownicy WHERE login = ? AND email = ?);"
			);
			stmt.setString(1, login);
			stmt.setString(2, email);

			ResultSet rs = stmt.executeQuery();
			rs.next();
			
			return rs.getInt(1) == 1;
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void addRecoveryPassword(String login, String passwordHash) {
		try {
			dbConn.createStatement().execute("PRAGMA foreign_keys = ON;");
			PreparedStatement stmt = dbConn.prepareStatement(
				"INSERT INTO uzytkownicyHasla (uzytkownikLogin, haslo) VALUES (?, ?);"
			);
			stmt.setString(1, login);
			stmt.setString(2, passwordHash);
			
			stmt.executeUpdate();
			
			stmt = dbConn.prepareStatement("INSERT INTO wygenerowaneHasla (hasloId, czyUzyte, dataWygenerowania) VALUES"
					+ "((SELECT id FROM uzytkownicyHasla WHERE uzytkownikLogin = ? ORDER BY id DESC LIMIT 1), 0, DATE('now'));");
			
			stmt.setString(1, login);
			
			stmt.executeUpdate();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void invalidateRecoveryPassword(String login) {
		try {
			dbConn.createStatement().execute("PRAGMA foreign_keys = ON;");
			PreparedStatement stmt = dbConn.prepareStatement(
				"UPDATE wygenerowaneHasla SET czyUzyte = 1 WHERE hasloId = (SELECT id FROM uzytkownicyHasla WHERE uzytkownikLogin = ? ORDER BY id DESC LIMIT 1);"
			);
			stmt.setString(1, login);

			stmt.executeUpdate();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
}
