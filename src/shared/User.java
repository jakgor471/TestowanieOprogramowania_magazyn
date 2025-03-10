package shared;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.IllegalFormatException;

/**
 * Klasa używana do reprezentowania użytkownika i jego danych wyszczególnionych w specyfikacji projektowej.
 * 
 * @author jakgor471
 *
 */
public class User {
	private String login;
	private String imie;
	private String nazwisko;
	private Adres adres;
	private String nrPesel;
	private Date dataUrodzenia;
	private Gender plec;
	private String email;
	private String nrTel;
	
	/**
	 * @return Login.
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @param login Login do ustawienia.
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * @return Imię.
	 */
	public String getImie() {
		return imie;
	}

	/**
	 * @param imie Imię do ustawienia.
	 */
	public void setImie(String imie) {
		this.imie = imie;
	}

	/**
	 * @return Nazwisko.
	 */
	public String getNazwisko() {
		return nazwisko;
	}

	/**
	 * @param nazwisko Nazwisko do ustawienia.
	 */
	public void setNazwisko(String nazwisko) {
		this.nazwisko = nazwisko;
	}

	/**
	 * @return Adres zamieszkania.
	 */
	public Adres getAdres() {
		return adres;
	}

	/**
	 * @param adres Adres zamieszkania do ustawienia.
	 */
	public void setAdres(Adres adres) {
		this.adres = adres;
	}

	/**
	 * @return Numer PESEL.
	 */
	public String getNrPesel() {
		return nrPesel;
	}

	/**
	 * @param nrPesel Numer PESEL do ustawienia.
	 * @throws IllegalArgumentException Jeśli numer PESEL nie spełnia warunków walidacji.
	 * @throws IllegalStateException Jeśli obiekt User nie posiada zdefiniowanej daty urodzenia lub płci.
	 */
	public void setNrPesel(String nrPesel) throws IllegalArgumentException, IllegalStateException {
		if(dataUrodzenia == null)
			throw new IllegalStateException("Nr PESEL może być ustawiony dopiero po ustawieniu daty urodzenia");
		if(plec == null)
			throw new IllegalStateException("Nr PESEL może być ustawiony dopiero po ustawieniu płci");
		
		nrPesel = nrPesel.strip();
		
		if(!DataValidation.validatePesel(nrPesel, dataUrodzenia, plec))
			return;
		
		this.nrPesel = nrPesel;
	}

	/**
	 * @return Data urodzenia.
	 */
	public Date getDataUrodzenia() {
		return dataUrodzenia;
	}

	/**
	 * @param dataUrodzenia Data urodzenia do ustawienia.
	 */
	public void setDataUrodzenia(Date dataUrodzenia) {
		this.dataUrodzenia = dataUrodzenia;
	}

	/**
	 * @return Płeć.
	 */
	public Gender getPlec() {
		return plec;
	}

	/**
	 * @param plec Płeć do ustawienia.
	 */
	public void setPlec(Gender plec) {
		this.plec = plec;
	}

	/**
	 * @return Adres e-mail.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email Adres e-mail do ustawienia.
	 * @throws IllegalArgumentException Jeśli adres e-mail nie spełnia warunków walidacji.
	 */
	public void setEmail(String email) throws IllegalArgumentException {
		email = email.trim();
		
		if(!DataValidation.validateEmail(email))
			return;
		
		this.email = email;
	}

	/**
	 * @return Numer telefonu.
	 */
	public String getNrTel() {
		return nrTel;
	}

	/**
	 * @param nrTel Numer telefonu do ustawienia.
	 * @throws IllegalArgumentException Jeśli numer telefonu ma długość inną niż 9 cyfr.
	 */
	public void setNrTel(String nrTel) {
		nrTel.trim();
		
		if(nrTel.length() != 9)
			throw new IllegalArgumentException("Numer telefonu musi mieć 9 cyfr");
		this.nrTel = nrTel;
	}

	/**
	 * Klasa reprezentująca poszczególne składowe adresu
	 * 
	 * @author jakgor471
	 *
	 */
	public static class Adres{
		protected String miejscowosc;
		protected String kodPocztowy;
		protected String ulica;
		protected String nrPosesji;
		protected String nrLokalu;
		
		public String getMiejscowosc() {
			return miejscowosc;
		}
		public void setMiejscowosc(String miejscowosc) {
			this.miejscowosc = miejscowosc;
		}
		public String getKodPocztowy() {
			return kodPocztowy;
		}
		public void setKodPocztowy(String kodPocztowy) {
			this.kodPocztowy = kodPocztowy;
		}
		public String getUlica() {
			return ulica;
		}
		public void setUlica(String ulica) {
			this.ulica = ulica;
		}
		public String getNrPosesji() {
			return nrPosesji;
		}
		public void setNrPosesji(String nrPosesji) {
			this.nrPosesji = nrPosesji;
		}
		public String getNrLokalu() {
			return nrLokalu;
		}
		public void setNrLokalu(String nrLokalu) {
			this.nrLokalu = nrLokalu;
		}
	}
	
	/**
	 * Typ wyliczeniowy reprezentujący płci użytkowników
	 * @author jakgor471
	 *
	 */
	public static enum Gender{
		Kobieta,
		Mezczyzna
	}
}
