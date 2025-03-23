package server;

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
		
		if(!session.isAdmin())
			throw new RuntimeException("Nieprawidłowe uprawnienia!");
		
		server.addUser(user);
	}
	
	public void editUser(User user, String oldLogin) throws RuntimeException{
		if(user == null)
			return;
		
		if(oldLogin == null)
			oldLogin = user.getLogin();
		
		if(!session.isAdmin())
			throw new RuntimeException("Nieprawidłowe uprawnienia!");
		
		server.editUser(user, oldLogin);
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
