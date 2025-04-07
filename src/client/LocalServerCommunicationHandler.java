package client;

import server.ClientCommunicationHandler;
import shared.ActiveSession;
import shared.Permission;
import shared.User;

/**
 * Klasa implementująca interfejs ServerCommunicationHandler, odpowiedzialna za wewnątrzprocesową komunikację z serwerem po stronie klienta
 * @author jakgor471
 *
 */
public class LocalServerCommunicationHandler{
	private ClientCommunicationHandler serverHandler;
	private ActiveSession session;
	
	public LocalServerCommunicationHandler(ClientCommunicationHandler serwer) {
		this.serverHandler = serwer;
	}
	
	public ActiveSession createSession(String login, String haslo) {
		ActiveSession nowa = serverHandler.createSession(login, haslo);
		nowa.addPermission(Permission.Administrator);
		
		session = nowa;
		return nowa;
	}
	
	public boolean endSession() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public void addUser(User user) {
		serverHandler.addUser((User)user.clone());
	}
	
	public void editUser(User user, String oldLogin) {
		serverHandler.editUser((User)user.clone(), oldLogin);
	}
	
	public void addResponseListener(ServerResponseListener listener) {
		
	}
	
	public void removeResponseListener(ServerResponseListener listener) {
		
	}
}
