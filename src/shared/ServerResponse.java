package shared;

/**
 * Klasa służąca do przekazywania wiadomości z serwera do klienta.
 * @author jakgor471
 *
 */
public class ServerResponse {
	public static enum ResponseType{
		ResponseOk,
		ResponseWarning,
		ResponseException
	}
	
	private String message;
	private ResponseType type;
	
	/**
	 * @return Wiadomość serwera
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * @param message Wiadomość do ustawienia
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * @return Typ odpowiedzi
	 */
	public ResponseType getType() {
		return type;
	}
	
	/**
	 * @param type Typ odpowiedzi do ustawienia.
	 */
	public void setType(ResponseType type) {
		this.type = type;
	}
}
