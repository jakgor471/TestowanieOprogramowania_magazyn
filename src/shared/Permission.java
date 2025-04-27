package shared;

public enum Permission {
	Administrator,
	UserEdit,
	UserAdd,
	UserForget,
	UserPermission;
	
	private static final String[] alias = {"Administrator", "Edycja użytkownika", "Dodanie użytkownika", "Zapomnienie użytkownika", "Edycja uprawnień użytkownika"};
	private static final String[] desc = {
			"Administrator systemu. Może wykonywać wszystkie czynności.",
			"Umożliwia edytowanie danych użytkownika.",
			"Umożliwia dodawanie użytkowników do systemu.",
			"Umożliwia zapominanie użytkowników w systemie.",
			"Umożliwia edytowanie uprawnień użytkowników w systemie."
	};
	
	public String getDescription() {
		return desc[this.ordinal()];
	}
	
	public String toString() {
		return alias[this.ordinal()];
	}
}
