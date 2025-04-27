package gui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.AbstractListModel;

import shared.Permission;
import shared.User;

public class FilteredUserListModel extends AbstractListModel<User> {
	private List<User> users;
	private ArrayList<User> filtered;
	private String criterium;
	private boolean showForgotten;
	private HashSet<Permission> perms;
	
	public FilteredUserListModel(List<User> users) {
		this.users = users;
		filtered = new ArrayList<User>();
		filter();
		
		perms = new HashSet<Permission>();
		for(Permission p : Permission.values())
			perms.add(p);
	}
	
	public HashSet<Permission> getPermissions(){
		return perms;
	}
	
	public void setPermissions(HashSet<Permission> perms) {
		this.perms = perms;
		filter();
	}
	
	public void setUserList(List<User> users) {
		this.users = users;
		filter();
	}
	
	public void setCriterium(String criterium) {
		this.criterium = criterium.toLowerCase();
		filter();
	}
	
	public void filter() {
		filtered.clear();
		
		boolean filter = !(criterium == null || criterium.isEmpty());
		
		for(User u : users) {
			if(u.isForgotten() ^ showForgotten)
				continue;
			boolean critFilter = filter && (u.getLogin().toLowerCase().contains(criterium) || u.getImie().toLowerCase().contains(criterium) || u.getNazwisko().toLowerCase().contains(criterium));
			boolean permFilter = perms == null;
			
			if(perms != null) {
				for(Permission p : perms) {
					permFilter = permFilter || u.hasPermission(p);
				}
			}
			
			if((!filter || critFilter) && permFilter)
				filtered.add(u);
		}
		
		reload();
	}
	
	public List<User> getUserList(){
		return filtered;
	}
	
	public List<User> getAllUsers(){
		return users;
	} 
	
	public void reload() {
		this.fireContentsChanged(this, 0, getSize());
	}
	
	@Override
	public int getSize() {
		return filtered.size();
	}

	@Override
	public User getElementAt(int index) {
		return filtered.get(index);
	}

	public boolean isShowForgotten() {
		return showForgotten;
	}

	public void setShowForgotten(boolean showForgotten) {
		this.showForgotten = showForgotten;
	}

}
