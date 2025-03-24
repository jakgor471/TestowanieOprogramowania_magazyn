package shared;

import java.util.Date;

/**
 * Klasa używana do reprezentowania użytkownika i jego danych wyszczególnionych w specyfikacji projektowej.
 * 
 * @author jakgor471
 *
 */
public class User implements Cloneable{
	private String login;
	private String imie;
	private String nazwisko;
	private Adres adres;
	private String nrPesel;
	private Date dataUrodzenia;
	private Gender plec;
	private String email;
	private String nrTel;
	private String hasloHash;
	private boolean zapomniany;
	
	public User() {
		adres = new Adres();
		dataUrodzenia = new Date();
		zapomniany = false;
	}
	
	public String toString() {
		String imieNazwisko = imie + " " + nazwisko;
		
		if(imieNazwisko.length() > 32) {
			imieNazwisko = imieNazwisko.substring(0, 29) + "...";
		}
		
		return login + ": " + imieNazwisko;
	}
	
	public void forgetUser() {
		imie = DataValidation.randomString(32);
		nazwisko = DataValidation.randomString(32);
		plec = (Math.random() > 0.5 ? Gender.Kobieta : Gender.Mezczyzna);
		dataUrodzenia = DataValidation.randomDate();
		nrPesel = DataValidation.randomPesel(dataUrodzenia, plec);
		zapomniany = true;
	}
	
	public boolean isForgotten() {
		return zapomniany;
	}

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
	 */
	public void setNrPesel(String nrPesel) {		
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
	 */
	public void setEmail(String email) throws IllegalArgumentException {
		this.email = email;
	}

	/**
	 * @return Numer telefonu.
	 */
	public String getNrTel() {
		return nrTel;
	}

	public void setNrTel(String nrTel) {
		this.nrTel = nrTel;
	}
	
	public String getHasloHash() {
		return hasloHash;
	}

	public void setHasloHash(String hasloHash) {
		this.hasloHash = hasloHash;
	}
	
	public void setHaslo(String haslo) {
		this.hasloHash = DataValidation.hashPassword(haslo);
	}

	public Object clone() {
		User nowy = new User();
		nowy.adres = (Adres)this.adres.clone();
		nowy.dataUrodzenia = (Date)this.dataUrodzenia.clone();
		nowy.email = this.email;
		nowy.imie = this.imie;
		nowy.login = this.login;
		nowy.nazwisko = this.nazwisko;
		nowy.nrPesel = this.nrPesel;
		nowy.nrTel = this.nrTel;
		nowy.plec = this.plec;
		nowy.hasloHash = this.hasloHash;
		
		return nowy;
	}

	/**
	 * Klasa reprezentująca poszczególne składowe adresu
	 * 
	 * @author jakgor471
	 *
	 */
	public static class Adres implements Cloneable{
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
		
		public Object clone() {
			Adres nowy = new Adres();
			nowy.kodPocztowy = this.kodPocztowy;
			nowy.miejscowosc = this.miejscowosc;
			nowy.nrLokalu = this.nrLokalu;
			nowy.nrPosesji = this.nrPosesji;
			nowy.ulica = this.ulica;
			
			return nowy;
		}
	}
	
	/**
	 * Typ wyliczeniowy reprezentujący płci użytkowników
	 * @author jakgor471
	 *
	 */
	public static enum Gender{
		Kobieta,
		Mezczyzna;
	
		/**
		 * @return Przyjazna nazwa.
		 */
		public String toString() {
			if(this.ordinal() == 0)
				return "Kobieta";
			return "Mężczyzna";
		}
	}
}
