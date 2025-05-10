package shared;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import shared.User.Gender;

/**
 * Klasa pomocnicza implementująca metody do walidacji danych oraz metody pomocnicze.
 * @author jakgor471
 *
 */
public final class DataValidation {
	private static final int[] WAGI_PESEL = {1, 3, 7, 9};
	private static final SimpleDateFormat PESEL_DATAFORMAT = new SimpleDateFormat("yyMMdd");
	private static final SimpleDateFormat DATAFORMAT = new SimpleDateFormat("dd-MM-yyyy");
	private static final SimpleDateFormat SQLDATAFORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static final Pattern EMAIL_REGEX = Pattern.compile("[\\w\\d\\.\\-]+@[\\w\\d\\.]+\\.\\w+");
	private static final Pattern DATA_REGEX = Pattern.compile("\\d{2}-\\d{2}-\\d{4}");
	private static final Pattern NRTEL_REGEX = Pattern.compile("\\d{9}");
	private static final Pattern PASSWORD_REGEX = Pattern.compile("^(?=(.*[A-Z]){1,})(?=(.*[a-z]){1,})(?=(.*[\\-\\_\\!\\*\\#\\$\\&]){1,})(?=(.*\\d){1,}).*$");
	private static final char[] LETTERS = "AĄBCĆDEĘFGHIJKLŁMNOÓPRSTUWZŹŻXYaąbcćdeęfghijklłmnoóprstuwzźżxy".toCharArray();
	private static final char[] LETTERS_LOWER = "abcdefghijklmnoprstuvwzxy".toCharArray();
	private static final char[] LETTERS_UPPER = "ABCDEFGHIJKLMNOPRSTUVWZXY".toCharArray();
	private static final char[] DIGITS = "0123456789".toCharArray();
	private static final char[] SPECIAL_CHARS = "-_!*#$&".toCharArray();
	private static final Random rand = new Random();
	
	static {
		DATAFORMAT.setLenient(false);
	}
	
	private DataValidation() {
		
	}
	
	public static String generatePassword() {
		char[] passChars = new char[10];
		
		for(int i = 0; i < 3; ++i) {
			passChars[i] = LETTERS_LOWER[rand.nextInt(LETTERS_LOWER.length)];
			passChars[i + 3] = LETTERS_UPPER[rand.nextInt(LETTERS_UPPER.length)];
		}
		
		for(int i = 0; i < 2; ++i) {
			passChars[i + 6] = DIGITS[rand.nextInt(DIGITS.length)];
			passChars[i + 8] = SPECIAL_CHARS[rand.nextInt(SPECIAL_CHARS.length)];
		}
		
		for(int i = 0; i < passChars.length; ++i) {
			int shuffle = rand.nextInt(passChars.length);
			char temp = passChars[i];
			passChars[i] = passChars[shuffle];
			passChars[shuffle] = temp;
		}
		
		return new String(passChars);
	}
	
	public static String hashPassword(String passwd) {
		try {
			MessageDigest mg = MessageDigest.getInstance("SHA-256");
			byte[] bytes = mg.digest(passwd.getBytes());
			
			return Base64.getEncoder().encodeToString(bytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String randomString(int length) {
		StringBuilder sb = new StringBuilder(length);
		
		for(int i = 0; i < length; ++i) {
			sb.append(LETTERS[rand.nextInt(LETTERS.length)]);
		}
		
		return sb.toString();
	}
	
	public static Date randomDate() {
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(1900 + rand.nextInt(100), rand.nextInt(12), rand.nextInt(27));
		
		return cal.getTime();
	}
	
	public static String randomPesel(Date dataUrodzenia, Gender plec) {
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.setTime(dataUrodzenia);
		
		String pierwszeLiczby = PESEL_DATAFORMAT.format(dataUrodzenia);
		int lp = 1000 + rand.nextInt(8999);
		
		if(plec == Gender.Kobieta)
			lp -= lp % 2;
		else
			lp += 1 - (lp % 2);
		
		String pesel = pierwszeLiczby + lp;
		pesel += peselControl(pesel, pesel.length());
		
		return pesel;
	}
	
	/**
	 * Dokonuje walidacji numeru PESEL
	 * @param nrPesel Numer PESEL do sprawdzenia.
	 * @param dataUrodzenia Data urodzenia.
	 * @param plec Płeć.
	 * @throws IllegalArgumentException Jeśli numer PESEL nie spełnia warunków walidacji.
	 * @throws NullPointerException Jeśli dataUrodzenia lub plec jest null.
	 * @return Prawda gdy numer PESEL jest poprawny.
	 */
	public static boolean validatePesel(String nrPesel, Date dataUrodzenia, Gender plec) throws IllegalArgumentException, NullPointerException {
		if(dataUrodzenia == null)
			throw new IllegalStateException("Nr PESEL może być ustawiony dopiero po ustawieniu daty urodzenia");
		if(plec == null)
			throw new IllegalStateException("Nr PESEL może być ustawiony dopiero po ustawieniu płci");
		
		if(nrPesel.length() < 2)
			throw new IllegalArgumentException("Nr PESEL zbyt krótki, musi posiadać minimum 2 znaki");
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(dataUrodzenia);
		
		String pierwszeLiczby = PESEL_DATAFORMAT.format(dataUrodzenia);
		
		if(!nrPesel.startsWith(pierwszeLiczby))
			throw new IllegalArgumentException("Nr PESEL musi zaczynać się RRMMDD daty urodzenia");
		
		if(peselControl(nrPesel, nrPesel.length() - 1) != Character.getNumericValue(nrPesel.charAt(nrPesel.length() - 1)))
			throw new IllegalArgumentException("Nieprawidłowa suma kontrolna nr PESEL");
		
		int cyfraPlci = Character.getNumericValue(nrPesel.charAt(nrPesel.length() - 2));
		
		if(cyfraPlci % 2 == 0 && plec != Gender.Kobieta)
			throw new IllegalArgumentException("Przedostatnia cyfra nr PESEL musi być parzysta lub 0 jeśli użytkownik to kobieta");
		if(cyfraPlci % 2 == 1 && plec != Gender.Mezczyzna)
			throw new IllegalArgumentException("Przedostatnia cyfra musi nr PESEL być nieparzysta jeśli użytkownik to mężczyzna");
		
		return true;
	}
	
	private static int peselControl(String nrPesel, int length) {
		int sumaKontrolna = 0;
		for(int i = 0; i < length; ++i) {
			int cyfra = Character.getNumericValue(nrPesel.charAt(i));
			
			sumaKontrolna += cyfra * WAGI_PESEL[i % 4];
		}
		
		if(sumaKontrolna % 10 == 0)
			return 0;
		else
			return 10 - sumaKontrolna % 10;
	}
	
	/**
	 * Dokonuje walidacji adresu e-mail.
	 * @param email Adres e-mail do sprawdzenia.
	 * @return Prawda gdy adres e-mail jest poprawny.
	 * @throws IllegalArgumentException Jeśli adres e-mail nie spełnia warunków walidacji.
	 */
	public static boolean validateEmail(String email) throws IllegalArgumentException {
		if(email.length() > 255)
			throw new IllegalArgumentException("Adres e-mail musi mieć nie więcej niż 255 znaków");
		
		Matcher match = EMAIL_REGEX.matcher(email);
		
		if(!match.matches())
			throw new IllegalArgumentException("Adres e-mail nie pasuje do wzorca");
		
		return true;
	}
	
	/**
	 * Dokonuje walidacji numeru telefonu.
	 * @param nrTel Numer telefonu do sprawdzenia.
	 * @throws IllegalArgumentException Jeśli numer telefonu ma długość inną niż 9 cyfr.
	 */
	public static boolean validateNrTel(String nrTel) throws IllegalArgumentException{
		if(nrTel.length() != 9)
			throw new IllegalArgumentException("Numer telefonu musi mieć 9 cyfr");
		
		Matcher match = NRTEL_REGEX.matcher(nrTel);
		
		if(!match.matches())
			throw new IllegalArgumentException("Nr telefonu nie pasuje do wzorca");
		
		return true;
	}
	
	/**
	 * Dokonuje walidacji daty.
	 * @param data Ciąg znaków reprezentujący datę w formacie DD-MM-RRRR.
	 * @return obiekt Date reprezentujący podaną datę.
	 * @throws IllegalArgumentException Jeśli podana data nie spełnia warunków walidacji.
	 */
	public static Date validateDate(String data) throws IllegalArgumentException {
		Matcher match = DATA_REGEX.matcher(data);
		
		if(!match.matches())
			throw new IllegalArgumentException("Data nie pasuje do wzorca DD-MM-YYYY");
		
		try {
			Date d = DATAFORMAT.parse(data);
			return d;
		} catch(ParseException e) {
			throw new IllegalArgumentException("Niepoprawna data");
		}
		
	}
	
	public static boolean validatePassword(String password) throws IllegalArgumentException {
		if(password.length() < 8 || password.length() > 15)
			throw new IllegalArgumentException("Hasło musi zawierać nie mniej niż 8 oraz nie więcej niż 15 znaków");
		
		Matcher m = PASSWORD_REGEX.matcher(password);
		
		if(!m.matches())
			throw new IllegalArgumentException("Hasło nie spełnia warunków walidacji");
		
		return true;
	}
	
	/**
	 * Konwertuje datę na ciąg znaków w formacie DD-MM-RRRR.
	 * @param data Data, która zostanie przekonwertowana na ciąg znaków.
	 * @return Ciąg znaków reprezentujący datę.
	 */
	public static String dateToString(Date data) {
		return DATAFORMAT.format(data);
	}
	
	/**
	 * Konwertuje datę na ciąg znaków w formacie RRRR-MM-DD.
	 * @param data Data, która zostanie przekonwertowana na ciąg znaków.
	 * @return Ciąg znaków reprezentujący datę.
	 */
	public static String dateToSqlString(Date data) {
		return SQLDATAFORMAT.format(data);
	}
	
	public static Date stringToDate(String str) throws ParseException {
		return SQLDATAFORMAT.parse(str);
	}
	
	public static Date dateFromPesel(String pesel) throws IllegalArgumentException{
		try {
			int year = 1900 + Integer.parseInt(pesel.substring(0, 2));
			int month = Integer.parseInt(pesel.substring(2, 4));
			int day = Integer.parseInt(pesel.substring(4, 6));
			
			Calendar cal = Calendar.getInstance();
			cal.clear();
			cal.set(year, month - 1, day);
			
			return cal.getTime();
		} catch(NumberFormatException e) {
			throw new IllegalArgumentException("Niepoprawny PESEL");
		}
	}
}
