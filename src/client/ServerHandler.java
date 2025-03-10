package client;

import shared.User;

/**
 * Interfejs definiujący metody do komunikacji warstwy klienta z warstwą serwera.
 * W tym projekcie jedyną implementacją tego interfejsu jest klasa LocalServerHandler.
 * 
 * @author jakgor471
 *
 */
public interface ServerHandler {
	/**
	 * Inicjuje proces komunikacji z serwerem poprzez rozpoczęcie nowej sesji.
	 * 
	 * @param login Login użytkownika chcącego rozpocząć nową sesję.
	 * @param haslo Hasło użytkownika chcącego rozpoczać nową sesję.
	 */
	public void createSession(String login, String haslo);
	
	/**
	 * Kończy aktywną sesję.
	 */
	public void endSession();
	
	/**
	 * 
	 * @param user Użytkownik do dodania
	 */
	public void addUser(User user);
}
