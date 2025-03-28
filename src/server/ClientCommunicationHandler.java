package server;

import java.util.Date;
import java.util.List;

import client.ServerCommunicationHandler;
import client.ServerResponseListener;
import shared.ActiveSession;
import shared.Permission;
import shared.User;

public class ClientCommunicationHandler extends ServerCommunicationHandler {
	private ActiveSession session;
	private DBServer server;
	
	public ClientCommunicationHandler(DBServer server) {
		this.server = server;
	}
	
	public ActiveSession createSession(String login, String haslo) {
		if(session != null)
			return null;
		
		session = new ActiveSession(1, login);
		session.addPermission(Permission.Administrator);
		return session;
	}
	
	public List<User> getUsers(){
		return server.getUsers();
	}
	
	public void addUser(User user) throws RuntimeException{
		if(user == null)
			return;
		
		if(session == null || !session.isAdmin())
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
		
		if(session == null || !session.isAdmin())
			throw new RuntimeException("Nieprawidłowe uprawnienia!");
		
		try {
			server.editUser((User)user.clone(), oldLogin);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
			throw new RuntimeException("Problem bazodanowy! " + e.getMessage());
		}
	}
	
	public void forgetUser(User user) {
		if(session == null || !session.isAdmin())
			throw new RuntimeException("Nieprawidłowe uprawnienia!");
		
		try {
			User forgotten = (User)user.clone();
			forgotten.forgetUser();
			forgotten.setZapomnianyPrzez(session.getLogin());
			forgotten.setDataZapomnienia(new Date());
			server.editUser(forgotten, forgotten.getLogin());
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
			throw new RuntimeException("Problem bazodanowy! " + e.getMessage());
		}
	}

	@Override
	public boolean endSession() {
		// TODO Auto-generated method stub
		return false;
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
