package shared;

import java.util.Date;
import java.util.HashSet;

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
	private boolean zapomniany;
	private Date dataZapomnienia;
	private String zapomnianyPrzez;
	private HashSet<Permission> uprawnienia;
	
	public User() {
		adres = new Adres();
		dataUrodzenia = new Date();
		zapomniany = false;
		uprawnienia = new HashSet<Permission>();
	}
	
	public void setPermissions(HashSet<Permission> perms) {
		this.uprawnienia.clear();
		this.uprawnienia.addAll(perms);
	}
	
	public void addPermission(Permission p) {
		uprawnienia.add(p);
	}
	
	public void removePermission(Permission p) {
		uprawnienia.remove(p);
	}
	
	public boolean hasPermission(Permission p) {
		return uprawnienia.contains(p);
	}
	
	public HashSet<Permission> getPermissions(){
		return uprawnienia;
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
		dataZapomnienia = new Date();
	}
	
	public boolean isForgotten() {
		return zapomniany;
	}
	
	public void setForgotten(boolean b) {
		this.zapomniany = b;
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
	public String getName() {
		return imie;
	}

	/**
	 * @param imie Imię do ustawienia.
	 */
	public void setName(String imie) {
		this.imie = imie;
	}

	/**
	 * @return Nazwisko.
	 */
	public String getLastname() {
		return nazwisko;
	}

	/**
	 * @param nazwisko Nazwisko do ustawienia.
	 */
	public void setLastname(String nazwisko) {
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
	public Date getBirthDate() {
		return dataUrodzenia;
	}

	/**
	 * @param dataUrodzenia Data urodzenia do ustawienia.
	 */
	public void setBirthDate(Date dataUrodzenia) {
		this.dataUrodzenia = dataUrodzenia;
	}

	/**
	 * @return Płeć.
	 */
	public Gender getGender() {
		return plec;
	}

	/**
	 * @param plec Płeć do ustawienia.
	 */
	public void setGender(Gender plec) {
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

	public Date getForgottenDate() {
		return dataZapomnienia;
	}

	public void setForgottenDate(Date dataZapomnienia) {
		this.dataZapomnienia = dataZapomnienia;
	}

	public String getForgottenBy() {
		return zapomnianyPrzez;
	}

	public void setForgottenBy(String zapomnianyPrzez) {
		this.zapomnianyPrzez = zapomnianyPrzez;
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
		nowy.zapomniany = this.zapomniany;
		nowy.uprawnienia = (HashSet<Permission>)this.uprawnienia.clone();
		nowy.zapomnianyPrzez = this.zapomnianyPrzez;
		
		if(this.zapomniany)
			nowy.dataZapomnienia = (Date)this.dataZapomnienia.clone();
		
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
		
		private static final String[] alias = {"Kobieta", "Mężczyzna"};
		
		/**
		 * @return Przyjazna nazwa.
		 */
		public String toString() {
			return alias[this.ordinal()];
		}
	}
}
