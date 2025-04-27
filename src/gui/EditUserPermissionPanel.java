package gui;

import java.awt.BorderLayout;
import java.net.URL;
import java.util.HashSet;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import shared.Permission;

public class EditUserPermissionPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final URL errorIcon = EditUserPanel.class.getResource("/images/exclamation.png");
	
	private HashSet<Permission> edytowaneUprawnienia;
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
	}
	
	public void setUprawnienia(HashSet<Permission> perms) {
		this.edytowaneUprawnienia = (HashSet<Permission>)perms.clone();
		this.model.setPermissions(edytowaneUprawnienia);
	}
	
	public HashSet<Permission> getUprawnienia(){
		return edytowaneUprawnienia;
	}
}
