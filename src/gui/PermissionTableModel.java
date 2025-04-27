package gui;

import java.util.HashSet;

import javax.swing.table.AbstractTableModel;

import shared.Permission;

public class PermissionTableModel extends AbstractTableModel{
	private HashSet<Permission> perms;
	
	public PermissionTableModel(HashSet<Permission> perms) {
		this.perms = perms;
	}
	
	public PermissionTableModel() {
	}
	
	public void setPermissions(HashSet<Permission> p) {
		this.perms = p;
		this.fireTableDataChanged();
	}
	
	@Override
	public int getRowCount() {
		return Permission.values().length;
	}
	
	public String getColumnName(int col) {
		final String[] header = new String[] { "Ustaw", "Nazwa uprawnienia" };
		return header[col];
	}

	@Override
	public int getColumnCount() {
		return 2;
	}
	
	public Class getColumnClass(int col) {
		if (col == 0)
			return Boolean.class;
		return String.class;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0)
			return perms.contains(Permission.values()[rowIndex]);
		return Permission.values()[rowIndex].toString();
	}
	
	public void setValueAt(Object val, int row, int col) {
		if (col != 0)
			return;
		boolean b = ((Boolean)val).booleanValue();
		
		if(b) {
			perms.add(Permission.values()[row]);
			return;
		}
		
		perms.remove(Permission.values()[row]);
	}
	
	public boolean isCellEditable(int row, int col) {
		return col == 0;
	}

}
