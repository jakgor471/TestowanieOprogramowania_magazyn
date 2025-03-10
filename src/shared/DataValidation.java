package shared;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import shared.User.Gender;

/**
 * Klasa pomocnicza implementująca metody do walidacji danych.
 * @author jakgor471
 *
 */
public class DataValidation {
	private static final int[] WAGI_PESEL = {1, 3, 7, 9};
	private static final Pattern EMAIL_REGEX = Pattern.compile("[\\w\\d\\.]+@[\\w\\d\\.]+");
	
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
		if(nrPesel.length() < 2)
			throw new IllegalArgumentException("Nr PESEL zbyt krótki, musi posiadać minimum 2 znaki");
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(dataUrodzenia);
		
		SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
		String pierwszeLiczby = df.format(dataUrodzenia);
		
		if(!nrPesel.startsWith(pierwszeLiczby))
			throw new IllegalArgumentException("Nr PESEL musi zaczynać się RRMMDD daty urodzenia");
		
		int sumaKontrolna = 0;
		for(int i = 0; i < nrPesel.length() - 1; ++i) {
			int cyfra = Character.getNumericValue(nrPesel.charAt(i));
			
			sumaKontrolna += cyfra * WAGI_PESEL[i % 4];
		}
		
		sumaKontrolna = 10 - (sumaKontrolna % 10);
		
		if(sumaKontrolna != Character.getNumericValue(nrPesel.charAt(nrPesel.length() - 1)))
			throw new IllegalArgumentException("Nieprawidłowa suma kontrolna nr PESEL");
		
		int cyfraPlci = Character.getNumericValue(nrPesel.charAt(nrPesel.length() - 2));
		
		if(cyfraPlci % 2 == 0 && plec != Gender.Kobieta)
			throw new IllegalArgumentException("Przedostatnia cyfra musi być parzysta lub 0 jeśli użytkownik to kobieta");
		if(cyfraPlci % 2 == 1 && plec != Gender.Mezczyzna)
			throw new IllegalArgumentException("Przedostatnia cyfra musi być nieparzysta jeśli użytkownik to mężczyzna");
		
		return true;
	}
	
	/**
	 * Dokonuje walidacji adresu e-mail.
	 * @param email Adres e-mail do sprawdzenia.
	 * @return Prawda gdy adres e-mail jest poprawny.
	 * @throws IllegalArgumentException
	 */
	public static boolean validateEmail(String email) throws IllegalArgumentException {
		if(email.length() > 255)
			throw new IllegalArgumentException("Adres e-mail musi mieć nie więcej niż 255 znaków");
		
		Matcher match = EMAIL_REGEX.matcher(email);
		
		if(!match.matches())
			throw new IllegalArgumentException("Adres e-mail nie pasuje do wzorca");
		
		return true;
	}
}
