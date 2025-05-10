package gui;

import java.awt.BorderLayout;
import java.util.HashSet;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import shared.Permission;

public class EditUserPermissionPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private HashSet<Permission> editedPerms;
	private PermissionTableModel model;
	
	public EditUserPermissionPanel() {
		//this.setBorder(BorderFactory.createEtchedBorder());
		this.setLayout(new BorderLayout());
		
		JTable perms = new JTable();
		model = new PermissionTableModel();
		
		perms.setModel(model);
		perms.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		perms.getTableHeader().setReorderingAllowed(false);
		perms.getColumnModel().getColumn(0).setMaxWidth(50);
		perms.getColumnModel().getColumn(0).setMinWidth(40);
		perms.setShowGrid(false);
		
		this.add(perms);
		
		this.setMaximumSize(perms.getMaximumSize());
	}
	
	public void setPermissions(HashSet<Permission> perms) {
		this.editedPerms = (HashSet<Permission>)perms.clone();
		this.model.setPermissions(editedPerms);
	}
	
	public HashSet<Permission> getPermissions(){
		return editedPerms;
	}
}
