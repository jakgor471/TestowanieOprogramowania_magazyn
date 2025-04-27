package gui;

import javax.swing.AbstractListModel;

import shared.Permission;

public class PermissionListModel extends AbstractListModel<Permission>{
	private static final long serialVersionUID = 1L;

	@Override
	public int getSize() {
		return Permission.values().length;
	}

	@Override
	public Permission getElementAt(int index) {
		return Permission.values()[index];
	}

}
