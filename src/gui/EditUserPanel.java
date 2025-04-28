package gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map.Entry;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
	
	private User editedUser;
	private String originalLogin;
	
	private EnumMap<Fields, InputIconPair> fields;
	private ArrayList<ActionListener> onClick;
	
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
		
		fields.put(field, pair);
		
		return pair;
	}
	
	public EditUserPanel() {
		//this.setBorder(BorderFactory.createEtchedBorder());
		
		this.setLayout(new GridBagLayout());		
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 0, 0, 5);
		
		InputIconPair nowe = null;
		fields = new EnumMap<>(Fields.class);
		onClick = new ArrayList<ActionListener>();
		
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
				if(!validateData())
					return;
				
				for(ActionListener al : onClick)
					al.actionPerformed(e);
			}
			
		});
		this.add(button, gbc);
		gbc.gridx = 2;
		this.add(Box.createHorizontalStrut(16), gbc);
	}
	
	public void addActionListener(ActionListener al) {
		onClick.add(al);
	}
	
	public void removeActionListener(ActionListener al) {
		onClick.remove(al);
	}
	
	public String getOriginalLogin() {
		return originalLogin;
	}
	
	/**
	 * Metoda ustawia edytowanego użytkownika oraz uzupełnia pola formularza na podstawie pól użytkownika.
	 * Obiekt uzytkownik jest klonowany - formularz nie modyfikuje pól obiektu przekazywanego jako argument.
	 * @param uzytkownik Użytkownik, którego dane mają być edytowane.
	 */
	public void setUzytkownik(User uzytkownik) {
		this.editedUser = (User)uzytkownik.clone();
		
		originalLogin = editedUser.getLogin();
		
		((JTextField)fields.get(Fields.KodPocztowy).input).setText(editedUser.getAdres().getKodPocztowy());
		((JTextField)fields.get(Fields.Miejscowosc).input).setText(editedUser.getAdres().getMiejscowosc());
		((JTextField)fields.get(Fields.NrLokalu).input).setText(editedUser.getAdres().getNrLokalu());
		((JTextField)fields.get(Fields.NrPosesji).input).setText(editedUser.getAdres().getNrPosesji());
		((JTextField)fields.get(Fields.Ulica).input).setText(editedUser.getAdres().getUlica());
		
		((JTextField)fields.get(Fields.Login).input).setText(editedUser.getLogin());
		((JTextField)fields.get(Fields.Imie).input).setText(editedUser.getName());
		((JTextField)fields.get(Fields.Nazwisko).input).setText(editedUser.getLastname());
		((JTextField)fields.get(Fields.NrPesel).input).setText(editedUser.getNrPesel());
		((JTextField)fields.get(Fields.DataUr).input).setText(DataValidation.dateToString(editedUser.getBirthDate()));
		((JComboBox<User.Gender>)fields.get(Fields.Plec).input).setSelectedItem(editedUser.getGender());
		((JTextField)fields.get(Fields.Email).input).setText(editedUser.getEmail());
		((JTextField)fields.get(Fields.NrTel).input).setText(editedUser.getNrTel());
	}
	
	/**
	 * Metoda zwraca obiekt użytkownika, którego pola są zgodne z wartościami z formularza.
	 * W przypadku błędu walidacji metoda zwraca null, a wiadomość wyjątku wyświetlana jest na dole formularza.
	 * @return Użytkownik lub null jeśli wartości z formularza nie spełniają warunków walidacji.
	 */
	public User getUzytkownik() {
		if(editedUser == null)
			editedUser = new User();
		User nowy = (User)editedUser.clone();
		Adres adr = new Adres();
		
		if(!validateData())
			return null;
		
		try {
			adr.setKodPocztowy(((JTextField)fields.get(Fields.KodPocztowy).input).getText());
			adr.setMiejscowosc(((JTextField)fields.get(Fields.Miejscowosc).input).getText());
			adr.setNrLokalu(((JTextField)fields.get(Fields.NrLokalu).input).getText());
			adr.setNrPosesji(((JTextField)fields.get(Fields.NrPosesji).input).getText());
			adr.setUlica(((JTextField)fields.get(Fields.Ulica).input).getText());
			
			nowy.setAdres(adr);
			nowy.setBirthDate(DataValidation.validateDate(((JTextField)fields.get(Fields.DataUr).input).getText()));
			nowy.setEmail(((JTextField)fields.get(Fields.Email).input).getText());
			nowy.setName(((JTextField)fields.get(Fields.Imie).input).getText());
			nowy.setLastname(((JTextField)fields.get(Fields.Nazwisko).input).getText());
			nowy.setGender((User.Gender)((JComboBox<User.Gender>)fields.get(Fields.Plec).input).getSelectedItem());
			nowy.setNrPesel(((JTextField)fields.get(Fields.NrPesel).input).getText());
			nowy.setLogin(((JTextField)fields.get(Fields.Login).input).getText());
			nowy.setNrTel(((JTextField)fields.get(Fields.NrTel).input).getText());
		} catch(Exception e) {
			return null;
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
		boolean error = false;
		
		for(Entry<Fields, InputIconPair> iip : fields.entrySet()) {
			if(iip.getValue().input instanceof JTextField) {
				setError(iip.getKey(), null);
			}
		}
		
		for(Entry<Fields, InputIconPair> iip : fields.entrySet()) {
			if(iip.getValue().input instanceof JTextField) {
				JTextField textfield = (JTextField)iip.getValue().input;
				
				if(textfield.getText().trim().isEmpty()) {
					setError(iip.getKey(), "Pole nie może być puste!");
					error = error || true;
				}
			}
		}
		
		Date d = null;
		User.Gender plec = (User.Gender)((JComboBox<User.Gender>)fields.get(Fields.Plec).input).getSelectedItem();
		String text = ((JTextField)fields.get(Fields.DataUr).input).getText().trim();
		if(!text.trim().isEmpty()) {
			try {
				d = DataValidation.validateDate(text);
			} catch(IllegalArgumentException e) {
				setError(Fields.DataUr, e.getMessage());
				error = error || true;
			}
		}
		
		text = ((JTextField)fields.get(Fields.Email).input).getText().trim();
		if(!text.trim().isEmpty()) {
			try {
				DataValidation.validateEmail(text);
			} catch(IllegalArgumentException e) {
				setError(Fields.Email, e.getMessage());
				error = error || true;
			}
		}
		
		text = ((JTextField)fields.get(Fields.NrTel).input).getText().trim();
		if(!text.trim().isEmpty()) {
			try {
				DataValidation.validateNrTel(text);
			} catch(IllegalArgumentException e) {
				setError(Fields.NrTel, e.getMessage());
				error = error || true;
			}
		}
		
		text = ((JTextField)fields.get(Fields.NrPesel).input).getText().trim();
		if(!text.trim().isEmpty()) {
			try {
				DataValidation.validatePesel(text, d, plec);
			} catch(IllegalArgumentException e) {
				setError(Fields.NrPesel, e.getMessage());
				error = error || true;
			}
		}
		
		return !error;
	}
	
	public void setEditable(boolean editable) {
		for(Entry<Fields, InputIconPair> iip : fields.entrySet()) {
			iip.getValue().input.setEnabled(editable);
		}
	}
	
	/**
	 * 
	 * @param field Typ wyliczeniowy wskazujący na pole, przy którym ma pojawić się komunikat błędu.
	 * @param error Wiadomość błędu lub null by usunąć komunikat.
	 */
	public void setError(Fields field, String error) {
		InputIconPair iip = fields.get(field);
		
		iip.icon.setVisible(error != null);
		iip.icon.setToolTipText(error);
	}
}
