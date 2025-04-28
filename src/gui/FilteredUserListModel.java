package gui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.AbstractListModel;

import shared.Permission;
import shared.User;

public class FilteredUserListModel extends AbstractListModel<User> {
	private static final long serialVersionUID = 1L;
	private List<User> users;
	private ArrayList<User> filtered;
	private String criterium;
	private boolean showForgotten;
	private HashSet<Permission> perms;
	private boolean allPerms;
	
	public FilteredUserListModel(List<User> users) {
		this.users = users;
		filtered = new ArrayList<User>();
		filter();
		
		perms = new HashSet<Permission>();
		for(Permission permission : Permission.values())
			perms.add(permission);
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
		
		for(User user : users) {
			if(user.isForgotten() ^ showForgotten)
				continue;
			boolean critFilter = filter && (user.getLogin().toLowerCase().contains(criterium) || user.getName().toLowerCase().contains(criterium) || user.getLastname().toLowerCase().contains(criterium));
			boolean permFilterOr = perms == null;
			boolean permFilterAnd = true;
			
			if(perms != null) {
				for(Permission permission : perms) {
					permFilterOr = permFilterOr || user.hasPermission(permission);
					permFilterAnd = permFilterAnd && user.hasPermission(permission);
				}
			}
			
			if((!filter || critFilter) && ((permFilterOr && !allPerms || permFilterAnd && allPerms) || perms.isEmpty() && user.getPermissions().isEmpty()))
				filtered.add(user);
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
		return filtered.get(Math.min(index, getSize() - 1));
	}

	public boolean isShowForgotten() {
		return showForgotten;
	}

	public void setShowForgotten(boolean showForgotten) {
		this.showForgotten = showForgotten;
	}

	public boolean isAllPerms() {
		return allPerms;
	}

	public void setAllPerms(boolean allPerms) {
		this.allPerms = allPerms;
	}

}
