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
			
			PreparedStatement stmt2 = dbConn.prepareStatement("SELECT idUprawnienia FROM uzytkownicyUprawnienia WHERE uzytkownikLogin = ?");
			
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()) {
				try {
					User u = new User();
					u.setLogin(rs.getString("login"));
					u.setHasloHash(rs.getString("haslo"));
					u.setImie(rs.getString("imie"));
					u.setNazwisko(rs.getString("nazwisko"));
					u.setNrPesel(rs.getString("nrPesel"));
					u.setDataUrodzenia(DataValidation.stringToDate(rs.getString("dataUrodzenia")));
					u.setPlec(Gender.values()[rs.getInt("plec")]);
					u.setEmail(rs.getString("email"));
					u.setNrTel(rs.getString("numerTelefonu"));
					u.setForgotten(rs.getBoolean("zapomniany"));
					
					if(u.isForgotten()) {
						u.setZapomnianyPrzez(rs.getString("zapomnianyPrzez"));
						u.setDataZapomnienia(DataValidation.stringToDate(rs.getString("dataZapomnienia")));
					}
					
					u.getAdres().setKodPocztowy(rs.getString("kodPocztowy"));
					u.getAdres().setMiejscowosc(rs.getString("miejscowosc"));
					u.getAdres().setUlica(rs.getString("ulica"));
					u.getAdres().setNrPosesji(rs.getString("nrPosesji"));
					u.getAdres().setNrLokalu(rs.getString("nrLokalu"));
					
					stmt2.setString(1, rs.getString("login"));
					ResultSet rs2 = stmt2.executeQuery();
					
					while(rs2.next()) {
						int uprId = rs2.getInt(1);
						
						if(uprId < 1 || uprId > Permission.values().length)
							continue;
						
						u.addPermission(Permission.values()[uprId - 1]);
					}
					
					users.add(u);
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
	 * @param uzytkownik Użytkownik do dodania do bazy danych.
	 * @throws IllegalArgumentException Jeśli login użytkownika nie jest unikalny w bazie danych.
	 */
	public void addUser(User uzytkownik) throws IllegalArgumentException{
		DataValidation.validatePesel(uzytkownik.getNrPesel(), uzytkownik.getDataUrodzenia(), uzytkownik.getPlec());
		DataValidation.validateEmail(uzytkownik.getEmail());
		DataValidation.validateNrTel(uzytkownik.getNrTel());
		
		try {
			PreparedStatement stmt = dbConn.prepareStatement("SELECT Count(login) FROM uzytkownicy WHERE login = ?;");
			stmt.setString(1, uzytkownik.getLogin());
			
			ResultSet rs = stmt.executeQuery();
			if(rs.next() && rs.getInt(1) > 0)
				throw new IllegalArgumentException("Nieunikalny login użytkownika!");
			
			stmt = dbConn.prepareStatement("INSERT INTO uzytkownicy (login, haslo, imie, nazwisko, nrPesel, dataUrodzenia, plec, email, numerTelefonu, zapomniany) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
			stmt.setString(1, uzytkownik.getLogin());
			stmt.setString(2, uzytkownik.getHasloHash());
			stmt.setString(3, uzytkownik.getImie());
			stmt.setString(4, uzytkownik.getNazwisko());
			stmt.setString(5, uzytkownik.getNrPesel());
			stmt.setString(6, DataValidation.dateToSqlString(uzytkownik.getDataUrodzenia()));
			stmt.setInt(7, uzytkownik.getPlec().ordinal());
			stmt.setString(8, uzytkownik.getEmail());
			stmt.setString(9, uzytkownik.getNrTel());
			stmt.setBoolean(10, uzytkownik.isForgotten());
			
			stmt.executeUpdate();
			
			stmt = dbConn.prepareStatement("INSERT INTO adresy (miejscowosc, kodPocztowy, ulica, nrPosesji, nrLokalu) VALUES (?, ?, ?, ?, ?)");
			stmt.setString(1, uzytkownik.getAdres().getMiejscowosc());
			stmt.setString(2, uzytkownik.getAdres().getKodPocztowy());
			stmt.setString(3, uzytkownik.getAdres().getUlica());
			stmt.setString(4, uzytkownik.getAdres().getNrPosesji());
			stmt.setString(5, uzytkownik.getAdres().getNrLokalu());
			
			stmt.executeUpdate();
			
			rs = dbConn.createStatement().executeQuery("SELECT last_insert_rowid();");
			if(rs.next()) {
				int id = rs.getInt(1);
				
				stmt = dbConn.prepareStatement("INSERT INTO uzytkownicyAdresy (uzytkownikLogin, adresId) VALUES (?, ?)");
				stmt.setString(1, uzytkownik.getLogin());
				stmt.setInt(2, id);
				
				stmt.executeUpdate();
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void editUser(User user, String oldLogin) throws IllegalArgumentException {
		DataValidation.validatePesel(user.getNrPesel(), user.getDataUrodzenia(), user.getPlec());
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
			
			stmt = dbConn.prepareStatement("UPDATE uzytkownicy SET login = ?, haslo = ?, imie = ?, nazwisko = ?, nrPesel = ?, dataUrodzenia = ?, plec = ?, email = ?, numerTelefonu = ?, zapomniany = ?, dataZapomnienia = ?, zapomnianyPrzez = ? WHERE login = ?;");
			stmt.setString(1, user.getLogin());
			stmt.setString(2, user.getHasloHash());
			stmt.setString(3, user.getImie());
			stmt.setString(4, user.getNazwisko());
			stmt.setString(5, user.getNrPesel());
			stmt.setString(6, DataValidation.dateToSqlString(user.getDataUrodzenia()));
			stmt.setInt(7, user.getPlec().ordinal());
			stmt.setString(8, user.getEmail());
			stmt.setString(9, user.getNrTel());
			stmt.setBoolean(10, user.isForgotten());
			
			if(user.isForgotten()) {
				stmt.setString(11, DataValidation.dateToSqlString(user.getDataZapomnienia()));
				stmt.setString(12, user.getZapomnianyPrzez());
			}
			
			stmt.setString(13, oldLogin);
			
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
	
	public void editUserPermissions(User user, HashSet<Permission> perms) {
		try {
			dbConn.createStatement().execute("PRAGMA foreign_keys = ON;");
			PreparedStatement stmt = dbConn.prepareStatement("DELETE FROM uzytkownicyUprawnienia WHERE uzytkownikLogin = ?;");
			stmt.setString(1, user.getLogin());
			stmt.executeUpdate();
			
			stmt = dbConn.prepareStatement("INSERT INTO uzytkownicyUprawnienia VALUES(?, ?);");
			stmt.setString(1, user.getLogin());
			
			for(Permission p : perms) {
				stmt.setInt(2, p.ordinal() + 1);
				stmt.executeUpdate();
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
}
