package shared;

public enum Permission {
	Administrator,
	UserEdit,
	UserAdd,
	UserForget,
	UserPermission;
	
	private static final String[] alias = {"Administrator", "Edycja użytkownika", "Dodanie użytkownika", "Zapomnienie użytkownika", "Edycja uprawnień użytkownika"};
	
	public String toString() {
		return alias[this.ordinal()];
	}
}
