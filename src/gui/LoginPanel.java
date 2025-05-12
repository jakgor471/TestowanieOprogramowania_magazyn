package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JTextField emailField;
	private JTextField loginField;
	private JPasswordField passwordField;
	private JPasswordField passwordField2;
	
	private ArrayList<ActionListener> onLogin;
	private ArrayList<ActionListener> onForgot;
	
	public static enum Type{
		Login,
		ForgotPassword,
		PasswordReset
	}
	
	public LoginPanel(Type panelType){
		this.setLayout(new GridBagLayout());
		
		onLogin = new ArrayList<>();
		onForgot = new ArrayList<>();
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 0, 0, 5);
		
		gbc.gridy = 0;
		
		emailField = null;
		passwordField = null;
		passwordField2 = null;
		
		String buttonText = "Zaloguj";
		
		if(panelType == Type.ForgotPassword) {
			gbc.gridx = 0;
			gbc.gridwidth = 2;
			this.add(new JLabel("<html>Na podany e-mail zostanie wysłany mail<br>z wygenerowanym hasłem.</html>"), gbc);
			gbc.gridwidth = 1;
			++gbc.gridy;
			
			emailField = new JTextField(15);
			gbc.gridx = 0;
			this.add(new JLabel("E-mail:"), gbc);
			gbc.gridx = 1;
			this.add(emailField, gbc);
			++gbc.gridy;
			
			buttonText = "Resetuj hasło";
		}
		
		if(panelType == Type.PasswordReset) {
			gbc.gridx = 0;
			gbc.gridwidth = 2;
			this.add(new JLabel("<html>Resetowanie hasła</html>"), gbc);
			gbc.gridwidth = 1;
			++gbc.gridy;
		}
		
		if(panelType == Type.ForgotPassword || panelType == Type.Login) {
			loginField = new JTextField(15);
			gbc.gridx = 0;
			this.add(new JLabel("Login:"), gbc);
			gbc.gridx = 1;
			this.add(loginField, gbc);
			++gbc.gridy;
		}
		
		if(panelType == Type.Login || panelType == Type.PasswordReset) {
			passwordField = new JPasswordField(15);
			gbc.gridx = 0;
			this.add(new JLabel("Hasło:"), gbc);
			gbc.gridx = 1;
			this.add(passwordField, gbc);
			++gbc.gridy;
		}
		
		if(panelType == Type.PasswordReset) {
			passwordField2 = new JPasswordField(15);
			gbc.gridx = 0;
			this.add(new JLabel("Powtórz hasło:"), gbc);
			gbc.gridx = 1;
			this.add(passwordField2, gbc);
			++gbc.gridy;
			
			buttonText = "Resetuj hasło";
		}
		
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		JButton button = new JButton();
		button.setText(buttonText);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for(ActionListener al : onLogin)
					al.actionPerformed(e);
			}
		});
		this.add(button, gbc);
		++gbc.gridy;
		
		if(panelType == Type.Login) {
			gbc.gridx = 0;
			gbc.gridwidth = 2;
			JButton button2 = new JButton();
			button2.setText("Odzyskaj hasło");
			button2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					for(ActionListener al : onForgot)
						al.actionPerformed(e);
				}
			});
			this.add(button2, gbc);
			++gbc.gridy;
		}
		this.setPreferredSize(this.getMaximumSize());
	}
	
	public String getLogin() {
		return loginField.getText().trim();
	}
	
	public String getPassword() {
		if(passwordField == null)
			return null;
		return new String(passwordField.getPassword());
	}
	
	public String getPassword2() {
		if(passwordField2 == null)
			return null;
		return new String(passwordField2.getPassword());
	}
	
	public String getEmail() {
		if(emailField == null)
			return null;
		return emailField.getText().trim();
	}
	
	public void addLoginActionListener(ActionListener al) {
		onLogin.add(al);
	}
	
	public void addForgotActionListener(ActionListener al) {
		onForgot.add(al);
	}
}
