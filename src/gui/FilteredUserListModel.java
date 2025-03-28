package gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

import shared.User;

public class FilteredUserListModel extends AbstractListModel<User> {
	private List<User> users;
	private ArrayList<User> filtered;
	private String criterium;
	private boolean showForgotten;
	
	public FilteredUserListModel(List<User> users) {
		this.users = users;
		filtered = new ArrayList<User>();
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
		
		if(criterium == null || criterium.isEmpty()) {
			for(User u : users) {
				if(u.isForgotten() ^ showForgotten)
					continue;
				filtered.add(u);
			}
			
			reload();
			return;
		}
		
		for(User u : users) {
			if(u.isForgotten() ^ showForgotten)
				continue;
			if(u.getLogin().toLowerCase().contains(criterium) || u.getImie().toLowerCase().contains(criterium) || u.getNazwisko().toLowerCase().contains(criterium))
				filtered.add(u);
		}
		
		reload();
	}
	
	public List<User> getUserList(){
		return filtered;
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
