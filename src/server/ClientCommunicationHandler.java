package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import client.ServerCommunicationHandler;
import client.ServerResponseListener;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import server.DBServer.LoginResult;
import shared.ActiveSession;
import shared.DataValidation;
import shared.Permission;
import shared.User;

public class ClientCommunicationHandler extends ServerCommunicationHandler {
	private ActiveSession session;
	private DBServer server;
	private Properties props;
	
	public ClientCommunicationHandler(DBServer server) {
		this.server = server;
		
		props = new Properties();
		
		File propFile = new File("server.properties");
		if(!propFile.exists()) {
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.example.com");
			props.put("mail.smtp.port", "587");
			props.put("mail.password", "admin");
			props.put("mail.address", "admin@gmail.com");
			
			try(FileOutputStream fo = new FileOutputStream(propFile)){
				props.store(fo, null);
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		try(FileInputStream fi = new FileInputStream(propFile)) {
			props.load(fi);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public ActiveSession getSession() {
		return session;
	}
	
	public boolean createSession(String login, String haslo) throws Exception {
		if(session != null)
			return false;
		
		String ip = "127.0.0.1";
		
		String hash = DataValidation.hashPassword(haslo);
		DBServer.LoginResult result = server.validateUserPassword(login, hash);
		
		if(result == LoginResult.Invalid) {
			long blockTime = server.loginBlockTime(login, "127.0.0.1", 2);
			
			if(blockTime > -1) {
				long newLoginTime = blockTime + 60000 - System.currentTimeMillis();
				
				if(newLoginTime > 0)
					throw new Exception("Przekroczono limit prób! Kolejne logowanie możliwe za " + Math.round(newLoginTime/1000) + " sekund.");
				else
					server.resetLoginAttempts(login, ip);
			} else
				server.addLoginAttempt(login, ip);
			
			return false;
		}
		
		session = new ActiveSession(1, login);
		session.setOneTime(result == LoginResult.OneTimePassword);
		
		if(session.isOneTime()) {
			return true;
		}
		
		session.setPermissions(server.getUserPermissions(login));
		return true;
	}
	
	public void resetUserPassword(String login, String email) {
		if(!server.validateUserEmail(login, email))
			throw new IllegalArgumentException("Niepasujące dane!");
		
		String generatedPassword = DataValidation.generatePassword();
		
		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(props.getProperty("mail.address"), props.getProperty("mail.password"));
			}
		});
		
		server.addRecoveryPassword(login, DataValidation.hashPassword(generatedPassword));
		
		new Thread(() -> {
			try {
				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(props.getProperty("mail.address")));
				message.setRecipients(
				    Message.RecipientType.TO,
				    InternetAddress.parse(email)
				);
				message.setSubject("Magazyn - odzyskiwanie hasła");
				message.setText("Dzień dobry,\n\nWygenerowane hasło do odzyskiwania:\n" + generatedPassword);
				
				Transport.send(message);
			} catch (MessagingException e) {
			    e.printStackTrace();
			}
		}).start();
	}
	
	public boolean isOneTimeLogin() {
		return session.isOneTime();
	}
	
	public List<User> getUsers(){
		if(session == null || !(session.hasPermission(Permission.User) || session.isAdmin()))
			return null;
		return server.getUsers();
	}
	
	public void addUser(User user) throws RuntimeException{
		if(user == null)
			return;
		
		if(session == null || !(session.isAdmin() || session.hasPermission(Permission.UserAdd)))
			throw new RuntimeException("Nieprawidłowe uprawnienia!");
		
		try {
			server.addUser((User)user.clone());
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
			throw new RuntimeException("Problem bazodanowy! " + e.getMessage());
		}
	}
	
	public void editUser(User user, String oldLogin) throws RuntimeException{
		if(user == null)
			return;
		
		if(oldLogin == null)
			oldLogin = user.getLogin();
		
		if(session == null || !(session.isAdmin() || session.hasPermission(Permission.UserEdit)))
			throw new RuntimeException("Nieprawidłowe uprawnienia!");
		
		try {
			server.editUser((User)user.clone(), oldLogin);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public void editUserPassword(String login, String password) {
		boolean recoveryMode = session.getLogin().equals(login) && session.isOneTime();
		if((session == null || !session.isAdmin()) && !recoveryMode)
			throw new RuntimeException("Nieprawidłowe uprawnienia!");
		
		DataValidation.validatePassword(password);
		
		if(recoveryMode) {
			server.invalidateRecoveryPassword(login);
		}
		
		try {
			server.editUserPassword(login, DataValidation.hashPassword(password));
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public void editUserPermissions(User user, HashSet<Permission> perms) {
		if(user == null)
			return;
		
		if(session == null || !(session.isAdmin() || session.hasPermission(Permission.UserPermission)))
			throw new RuntimeException("Nieprawidłowe uprawnienia!");
		
		try {
			server.editUserPermissions(user.getLogin(), (HashSet<Permission>)perms.clone());
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public void forgetUser(User user) {
		if(session == null || !(session.isAdmin() || session.hasPermission(Permission.UserForget)))
			throw new RuntimeException("Nieprawidłowe uprawnienia!");
		
		try {
			User forgotten = (User)user.clone();
			forgotten.forgetUser();
			forgotten.setForgottenBy(session.getLogin());
			forgotten.setForgottenDate(new Date());
			server.editUser(forgotten, forgotten.getLogin());
			server.editUserPermissions(user.getLogin(), new HashSet<Permission>());
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public boolean endSession() {
		session = null;
		return true;
	}

	@Override
	public void addResponseListener(ServerResponseListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeResponseListener(ServerResponseListener listener) {
		// TODO Auto-generated method stub
		
	}
}
