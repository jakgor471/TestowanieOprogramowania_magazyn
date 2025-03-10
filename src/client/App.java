package client;

import java.util.Date;

import shared.User;
import shared.User.Gender;

public class App {
	public static void main(String[] args) {
		User u = new User();
		u.setPlec(Gender.Kobieta);
		u.setDataUrodzenia(new Date(2002-1900, 6, 8));
		u.setNrPesel("02070803628");
	}
}
