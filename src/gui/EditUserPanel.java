package gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import shared.DataValidation;
import shared.User;
import shared.User.Adres;

/**
 * Panel umożliwiający edytowanie właściwości użytkownika
 * @author jakgor471
 *
 */
public class EditUserPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final URL errorIcon = EditUserPanel.class.getResource("/images/exclamation.png");
	
	private User edytowanyUzytkownik;
	
	private EnumMap<Fields, InputIconPair> pola;
	
	private static class InputIconPair{
		public Component input;
		public JLabel icon;
	}
	
	public static enum Fields{
		Imie,
		Nazwisko,
		Login,
		Email,
		NrTel,
		NrLokalu,
		NrPosesji,
		NrPesel,
		DataUr,
		Plec,
		Miejscowosc,
		Ulica,
		KodPocztowy
	}
	
	private InputIconPair createField(Fields field, Component input) {
		InputIconPair pair = new InputIconPair();
		pair.input = input;
		
		JLabel icon = new JLabel();
		icon.setVisible(false);
		icon.setIcon(new ImageIcon(errorIcon));
		
		pair.icon = icon;
		
		pola.put(field, pair);
		
		return pair;
	}
	
	public EditUserPanel() {
		this.setBorder(BorderFactory.createEtchedBorder());
		
		this.setLayout(new GridBagLayout());		
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 0, 0, 5);
		
		InputIconPair nowe = null;
		pola = new EnumMap<>(Fields.class);
		
		gbc.gridy = 0;
		
		nowe = createField(Fields.Login, new JTextField(15));
		gbc.gridx = 0;
		this.add(new JLabel("Login:"), gbc);
		gbc.gridx = 1;
		this.add(nowe.input, gbc);
		gbc.gridx = 2;
		this.add(nowe.icon, gbc);
		++gbc.gridy;
		
		gbc.gridx = 0;
		this.add(Box.createVerticalStrut(10), gbc);
		++gbc.gridy;
		
		nowe = createField(Fields.Imie, new JTextField(15));
		gbc.gridx = 0;
		this.add(new JLabel("Imię:"), gbc);
		gbc.gridx = 1;
		this.add(nowe.input, gbc);
		gbc.gridx = 2;
		this.add(nowe.icon, gbc);
		++gbc.gridy;
		
		nowe = createField(Fields.Nazwisko, new JTextField(15));
		gbc.gridx = 0;
		this.add(new JLabel("Nazwisko:"), gbc);
		gbc.gridx = 1;
		this.add(nowe.input, gbc);
		gbc.gridx = 2;
		this.add(nowe.icon, gbc);
		++gbc.gridy;
		
		gbc.gridx = 0;
		this.add(Box.createVerticalStrut(10), gbc);
		++gbc.gridy;
		
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		this.add(new JLabel("Adres zamieszkania"), gbc);
		gbc.gridwidth = 1;
		++gbc.gridy;
		
		nowe = createField(Fields.Miejscowosc, new JTextField(15));
		gbc.gridx = 0;
		this.add(new JLabel("Miejscowość:"), gbc);
		gbc.gridx = 1;
		this.add(nowe.input, gbc);
		gbc.gridx = 2;
		this.add(nowe.icon, gbc);
		++gbc.gridy;
		
		nowe = createField(Fields.KodPocztowy, new JTextField(15));
		gbc.gridx = 0;
		this.add(new JLabel("Kod pocztowy:"), gbc);
		gbc.gridx = 1;
		this.add(nowe.input, gbc);
		gbc.gridx = 2;
		this.add(nowe.icon, gbc);
		++gbc.gridy;
		
		nowe = createField(Fields.Ulica, new JTextField(15));
		gbc.gridx = 0;
		this.add(new JLabel("Ulica:"), gbc);
		gbc.gridx = 1;
		this.add(nowe.input, gbc);
		gbc.gridx = 2;
		this.add(nowe.icon, gbc);
		++gbc.gridy;
		
		nowe = createField(Fields.NrPosesji, new JTextField(15));
		gbc.gridx = 0;
		this.add(new JLabel("Nr posesji:"), gbc);
		gbc.gridx = 1;
		this.add(nowe.input, gbc);
		gbc.gridx = 2;
		this.add(nowe.icon, gbc);
		++gbc.gridy;
		
		nowe = createField(Fields.NrLokalu, new JTextField(15));
		gbc.gridx = 0;
		this.add(new JLabel("Nr lokalu:"), gbc);
		gbc.gridx = 1;
		this.add(nowe.input, gbc);
		gbc.gridx = 2;
		this.add(nowe.icon, gbc);
		++gbc.gridy;
		
		gbc.gridx = 0;
		this.add(Box.createVerticalStrut(10), gbc);
		++gbc.gridy;
		
		nowe = createField(Fields.NrPesel, new JTextField(15));
		gbc.gridx = 0;
		this.add(new JLabel("Nr PESEL:"), gbc);
		gbc.gridx = 1;
		this.add(nowe.input, gbc);
		gbc.gridx = 2;
		this.add(nowe.icon, gbc);
		++gbc.gridy;
		
		nowe = createField(Fields.DataUr, new JTextField(15));
		gbc.gridx = 0;
		this.add(new JLabel("Data urodzenia DD-MM-RRRR:"), gbc);
		gbc.gridx = 1;
		this.add(nowe.input, gbc);
		gbc.gridx = 2;
		this.add(nowe.icon, gbc);
		++gbc.gridy;
		
		nowe = createField(Fields.Plec, new JComboBox<User.Gender>(User.Gender.values()));
		gbc.gridx = 0;
		this.add(new JLabel("Płeć:"), gbc);
		gbc.gridx = 1;
		this.add(nowe.input, gbc);
		gbc.gridx = 2;
		this.add(nowe.icon, gbc);
		++gbc.gridy;
		
		gbc.gridx = 0;
		this.add(Box.createVerticalStrut(10), gbc);
		++gbc.gridy;
		
		nowe = createField(Fields.Email, new JTextField(15));
		gbc.gridx = 0;
		this.add(new JLabel("Adres e-mail:"), gbc);
		gbc.gridx = 1;
		this.add(nowe.input, gbc);
		gbc.gridx = 2;
		this.add(nowe.icon, gbc);
		++gbc.gridy;
		
		nowe = createField(Fields.NrTel, new JTextField(15));
		gbc.gridx = 0;
		this.add(new JLabel("Nr telefonu:"), gbc);
		gbc.gridx = 1;
		this.add(nowe.input, gbc);
		gbc.gridx = 2;
		this.add(nowe.icon, gbc);
		++gbc.gridy;
		
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		JButton button = new JButton();
		button.setText("Zatwierdź");
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				validateData();
			}
			
		});
		this.add(button, gbc);
		gbc.gridx = 2;
		this.add(Box.createHorizontalStrut(16), gbc);
	}
	
	/**
	 * Metoda ustawia edytowanego użytkownika oraz uzupełnia pola formularza na podstawie pól użytkownika.
	 * Obiekt uzytkownik jest klonowany - formularz nie modyfikuje pól obiektu przekazywanego jako argument.
	 * @param uzytkownik Użytkownik, którego dane mają być edytowane.
	 */
	public void setUzytkownik(User uzytkownik) {
		this.edytowanyUzytkownik = (User)uzytkownik.clone();
		
		((JTextField)pola.get(Fields.KodPocztowy).input).setText(edytowanyUzytkownik.getAdres().getKodPocztowy());
		((JTextField)pola.get(Fields.Miejscowosc).input).setText(edytowanyUzytkownik.getAdres().getMiejscowosc());
		((JTextField)pola.get(Fields.NrLokalu).input).setText(edytowanyUzytkownik.getAdres().getNrLokalu());
		((JTextField)pola.get(Fields.NrPosesji).input).setText(edytowanyUzytkownik.getAdres().getNrPosesji());
		((JTextField)pola.get(Fields.Ulica).input).setText(edytowanyUzytkownik.getAdres().getUlica());
		
		((JTextField)pola.get(Fields.Login).input).setText(edytowanyUzytkownik.getLogin());
		((JTextField)pola.get(Fields.Imie).input).setText(edytowanyUzytkownik.getImie());
		((JTextField)pola.get(Fields.Nazwisko).input).setText(edytowanyUzytkownik.getNazwisko());
		((JTextField)pola.get(Fields.NrPesel).input).setText(edytowanyUzytkownik.getNrPesel());
		((JTextField)pola.get(Fields.DataUr).input).setText(DataValidation.dateToString(edytowanyUzytkownik.getDataUrodzenia()));
		((JComboBox<User.Gender>)pola.get(Fields.Plec).input).setSelectedItem(edytowanyUzytkownik.getPlec());
		((JTextField)pola.get(Fields.Email).input).setText(edytowanyUzytkownik.getEmail());
		((JTextField)pola.get(Fields.NrTel).input).setText(edytowanyUzytkownik.getNrTel());
	}
	
	/**
	 * Metoda zwraca obiekt użytkownika, którego pola są zgodne z wartościami z formularza.
	 * W przypadku błędu walidacji metoda zwraca null, a wiadomość wyjątku wyświetlana jest na dole formularza.
	 * @return Użytkownik lub null jeśli wartości z formularza nie spełniają warunków walidacji.
	 */
	public User getUzytkownik() {
		User nowy = new User();
		Adres adr = new Adres();
		
		if(!validateData())
			return null;
		
		try {
			adr.setKodPocztowy(((JTextField)pola.get(Fields.KodPocztowy).input).getText());
			adr.setMiejscowosc(((JTextField)pola.get(Fields.Miejscowosc).input).getText());
			adr.setNrLokalu(((JTextField)pola.get(Fields.NrLokalu).input).getText());
			adr.setNrPosesji(((JTextField)pola.get(Fields.NrPosesji).input).getText());
			adr.setUlica(((JTextField)pola.get(Fields.Ulica).input).getText());
			
			nowy.setAdres(adr);
			nowy.setDataUrodzenia(DataValidation.validateDate(((JTextField)pola.get(Fields.DataUr).input).getText()));
			nowy.setEmail(((JTextField)pola.get(Fields.Email).input).getText());
			nowy.setImie(((JTextField)pola.get(Fields.Imie).input).getText());
			nowy.setNazwisko(((JTextField)pola.get(Fields.Nazwisko).input).getText());
			nowy.setPlec((User.Gender)((JComboBox<User.Gender>)pola.get(Fields.Plec).input).getSelectedItem());
			nowy.setNrPesel(((JTextField)pola.get(Fields.NrPesel).input).getText());
			nowy.setLogin(((JTextField)pola.get(Fields.Login).input).getText());
			nowy.setNrTel(((JTextField)pola.get(Fields.NrTel).input).getText());
		} catch(Exception e) {
			
		}
		
		return nowy;
	}
	
	/**
	 * Uruchamia procedurę walidacji danych.
	 * Jeśli pole/pola nie przejdą walidacji wyświetlona zostanie ikona, po najechaniu której
	 * wyświetli się powiadomienie z informacją o błędzie.
	 * 
	 * @return Prawda jeśli proces walidacji przeszedł pomyślnie.
	 */
	public boolean validateData() {
		for(Entry<Fields, InputIconPair> iip : pola.entrySet()) {
			if(iip.getValue().input instanceof JTextField) {
				setError(iip.getKey(), null);
			}
		}
		
		for(Entry<Fields, InputIconPair> iip : pola.entrySet()) {
			if(iip.getValue().input instanceof JTextField) {
				JTextField textfield = (JTextField)iip.getValue().input;
				
				if(textfield.getText().trim().isEmpty()) {
					setError(iip.getKey(), "Pole nie może być puste!");
				}
			}
		}
		
		Date d = null;
		User.Gender plec = (User.Gender)((JComboBox<User.Gender>)pola.get(Fields.Plec).input).getSelectedItem();
		String text = ((JTextField)pola.get(Fields.DataUr).input).getText().trim();
		if(!text.trim().isEmpty()) {
			try {
				d = DataValidation.validateDate(text);
			} catch(IllegalArgumentException e) {
				setError(Fields.DataUr, e.getMessage());
			}
		}
		
		text = ((JTextField)pola.get(Fields.Email).input).getText().trim();
		if(!text.trim().isEmpty()) {
			try {
				DataValidation.validateEmail(text);
			} catch(IllegalArgumentException e) {
				setError(Fields.Email, e.getMessage());
			}
		}
		
		text = ((JTextField)pola.get(Fields.NrTel).input).getText().trim();
		if(!text.trim().isEmpty()) {
			try {
				DataValidation.validateNrTel(text);
			} catch(IllegalArgumentException e) {
				setError(Fields.NrTel, e.getMessage());
			}
		}
		
		text = ((JTextField)pola.get(Fields.NrPesel).input).getText().trim();
		if(!text.trim().isEmpty()) {
			try {
				DataValidation.validatePesel(text, d, plec);
			} catch(IllegalArgumentException e) {
				setError(Fields.NrPesel, e.getMessage());
			}
		}
		
		return true;
	}
	
	/**
	 * 
	 * @param field Typ wyliczeniowy wskazujący na pole, przy którym ma pojawić się komunikat błędu.
	 * @param error Wiadomość błędu lub null by usunąć komunikat.
	 */
	public void setError(Fields field, String error) {
		InputIconPair iip = pola.get(field);
		
		iip.icon.setVisible(error != null);
		iip.icon.setToolTipText(error);
	}
}
