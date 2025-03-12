package client;

import java.awt.BorderLayout;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import gui.EditUserPanel;
import shared.User;
import shared.User.Gender;

public class App {
	public static final String appTytul = "Magazyn";
	
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		JFrame frame = new JFrame(appTytul);
		
		EditUserPanel eup = new EditUserPanel();
		
		User user = new User();
		user.setLogin("login123");
		user.setImie("Jakub");
		user.setNazwisko("Nowak");
		user.setEmail("jakub.nowak@edu.uni.lodz.pl");
		user.setNrPesel("02070803628");
		user.setNrTel("692504256");
		user.setPlec(Gender.Kobieta);
		
		user.getAdres().setKodPocztowy("95-200");
		user.getAdres().setMiejscowosc("Pabianice");
		user.getAdres().setNrLokalu("123");
		user.getAdres().setNrPosesji("6/8");
		user.getAdres().setUlica("Ostatnia");
		
		eup.setUzytkownik(user);
		
		frame.getContentPane().add(eup);
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(720, 520);
		frame.setVisible(true);
	}
}
