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
	 * @return login
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
	 * @return Imię
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
	 * @return Nazwisko
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
	 * @return Adres zamieszkania
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
	 * @return Numer PESEL
	 */
	public String getNrPesel() {
		return nrPesel;
	}

	/**
	 * @param nrPesel Numer PESEL do ustawienia
	 * @throws IllegalArgumentException Jeśli numer PESEL nie spełnia warunków walidacji.
	 * @throws IllegalStateException Jeśli obiekt User nie posiada zdefiniowanej daty urodzenia lub płci.
	 */
	public void setNrPesel(String nrPesel) throws IllegalArgumentException, IllegalStateException {
		if(dataUrodzenia == null)
			throw new IllegalStateException("Nr PESEL może być ustawiony dopiero po ustawieniu daty urodzenia");
		if(plec == null)
			throw new IllegalStateException("Nr PESEL może być ustawiony dopiero po ustawieniu płci");
		
		if(nrPesel.length() < 2)
			throw new IllegalArgumentException("Nr PESEL zbyt krótki, musi posiadać minimum 2 znaki");
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(dataUrodzenia);
		
		SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
		String pierwszeLiczby = df.format(dataUrodzenia);
		
		nrPesel = nrPesel.strip();
		
		if(!nrPesel.startsWith(pierwszeLiczby))
			throw new IllegalArgumentException("Nr PESEL musi zaczynać się RRMMDD daty urodzenia");
		
		int sumaKontrolna = 0;
		int[] wagi = {1, 3, 7, 9};
		for(int i = 0; i < nrPesel.length() - 1; ++i) {
			int cyfra = Character.getNumericValue(nrPesel.charAt(i));
			
			sumaKontrolna += cyfra * wagi[i % 4];
		}
		
		sumaKontrolna = 10 - (sumaKontrolna % 10);
		
		if(sumaKontrolna != Character.getNumericValue(nrPesel.charAt(nrPesel.length() - 1)))
			throw new IllegalArgumentException("Nieprawidłowa suma kontrolna nr PESEL");
		
		int cyfraPlci = Character.getNumericValue(nrPesel.charAt(nrPesel.length() - 2));
		
		if(cyfraPlci % 2 == 0 && plec != Gender.Kobieta)
			throw new IllegalArgumentException("Przedostatnia cyfra musi być parzysta lub 0 jeśli użytkownik to kobieta");
		if(cyfraPlci % 2 == 1 && plec != Gender.Mezczyzna)
			throw new IllegalArgumentException("Przedostatnia cyfra musi być nieparzysta jeśli użytkownik to mężczyzna");
		
		this.nrPesel = nrPesel;
	}

	/**
	 * @return the dataUrodzenia
	 */
	public Date getDataUrodzenia() {
		return dataUrodzenia;
	}

	/**
	 * @param dataUrodzenia the dataUrodzenia to set
	 */
	public void setDataUrodzenia(Date dataUrodzenia) {
		this.dataUrodzenia = dataUrodzenia;
	}

	/**
	 * @return the plec
	 */
	public Gender getPlec() {
		return plec;
	}

	/**
	 * @param plec the plec to set
	 */
	public void setPlec(Gender plec) {
		this.plec = plec;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the nrTel
	 */
	public String getNrTel() {
		return nrTel;
	}

	/**
	 * @param nrTel the nrTel to set
	 */
	public void setNrTel(String nrTel) {
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
