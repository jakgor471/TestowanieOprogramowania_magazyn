package client;

import java.util.List;

import shared.ActiveSession;
import shared.ServerResponse;
import shared.User;

/**
 * Klasa abstrakcyjna definiująca metody do komunikacji warstwy klienta z warstwą serwera.
 * W tym projekcie jedyną implementacją tej klasy jest klasa LocalServerCommunicationHandler.
 * 
 * @author jakgor471
 *
 */
public abstract class ServerCommunicationHandler {
	private ServerResponse lastResponse;
	
	/**
	 * Inicjuje proces komunikacji z serwerem poprzez rozpoczęcie nowej sesji.
	 * 
	 * @param login Login użytkownika chcącego rozpocząć nową sesję.
	 * @param haslo Hasło użytkownika chcącego rozpoczać nową sesję.
	 * @return Prawda jeśli pomyślnie utworzono sesję.
	 */
	public abstract ActiveSession createSession(String login, String haslo);
	
	/**
	 * Kończy aktywną sesję.
	 * @return Prawda jeśli pomyślnie zakończono sesję.
	 */
	public abstract boolean endSession();
	
	public abstract List<User> getUsers();
	
	/**
	 * 
	 * @param user Użytkownik do dodania.
	 */
	public abstract void addUser(User user);
	
	/**
	 * 
	 * @param user Użytkownik do edycji.
	 * @param oldLogin Jeśli zmieniono login, oldLogin to poprzedni login użytkownika.
	 */
	public abstract void editUser(User user, String oldLogin);
	
	public abstract void forgetUser(User user);
	
	/**
	 * 
	 * @return Odpowiedź serwera po ostatniej interakcji.
	 */
	public ServerResponse getResponse() {
		return lastResponse;
	}
	
	public abstract void addResponseListener(ServerResponseListener listener);
	public abstract void removeResponseListener(ServerResponseListener listener);
}
