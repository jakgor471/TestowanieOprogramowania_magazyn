package shared;

import java.util.HashSet;

/**
 * Klasa reprezentująca sesję użytkownika.
 * Sesja tworzona jest po stronie klienta i serwera w momencie pomyślnego nawiązania połączenia (zalogowania).
 * Identyfikator sesji używany jest do walidacji działań po stronie serwera.
 * @author jakgor471
 *
 */
public class ActiveSession {
	private long sessionId;
	private String login;
	private HashSet<Permission> permission;
	private boolean oneTime;
	
	public ActiveSession(long sessionId, String login) {
		permission = new HashSet<Permission>();
		this.sessionId = sessionId;
		this.login = login;
		oneTime = false;
	}
	
	public boolean isOneTime() {
		return oneTime;
	}

	public void setOneTime(boolean oneTime) {
		this.oneTime = oneTime;
	}
	
	public void setPermissions(HashSet<Permission> perms) {
		this.permission.clear();
		this.permission.addAll(perms);
	}

	public void addPermission(Permission p) {
		permission.add(p);
	}
	
	public void removePermission(Permission p) {
		permission.remove(p);
	}
	
	public boolean hasPermission(Permission p) {
		return permission.contains(p);
	}
	
	public boolean isAdmin() {
		return permission.contains(Permission.Administrator);
	}

	/**
	 * @return Identyfikator sesji
	 */
	public long getSessionId() {
		return sessionId;
	}

	/**
	 * @param sessionId Identyfikator sesji do ustawienia.
	 */
	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}
	
	public String getLogin() {
		return login;
	}
	
}
