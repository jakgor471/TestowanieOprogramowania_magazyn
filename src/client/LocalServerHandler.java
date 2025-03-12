package client;

import shared.ActiveSession;
import shared.User;

/**
 * Klasa implementująca interfejs ServerHandler, odpowiedzialna za wewnątrzprocesową komunikację z serwerem po stronie klienta
 * @author jakgor471
 *
 */
public class LocalServerHandler implements ServerHandler{
	private ActiveSession session;
	
	
	public void createSession(String login, String haslo) throws IllegalStateException{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void endSession() {
		// TODO Auto-generated method stub
		
	}
	
	public void addUser(User user) {
		
	}

}
