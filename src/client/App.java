package client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import gui.EditUserPanel;
import gui.EditUserPermissionPanel;
import gui.FilteredUserListModel;
import gui.InfoPanel;
import gui.PermissionListModel;
import server.ClientCommunicationHandler;
import server.DBServer;
import shared.Permission;
import shared.User;
import shared.User.Gender;

public class App {
	public static final String appTytul = "Magazyn";
	
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		DBServer serwer = new DBServer();
		ClientCommunicationHandler serverHandler = new ClientCommunicationHandler(serwer);
		serverHandler.createSession("admin", "admin");
		
		JFrame frame = new JFrame(appTytul);
		
		List<User> users = serverHandler.getUsers();
		FilteredUserListModel userListModel = new FilteredUserListModel(users);
		
		JList<User> userList = new JList<User>();
		userList.setModel(userListModel);
		
		InfoPanel userInfo = new InfoPanel();
		
		User user = new User();
		user.setLogin("login133");
		user.setHaslo("B@rdzo_SkomplikowaneH@$$70");
		user.setImie("Jakub");
		user.setNazwisko("Nowak");
		user.setEmail("jakub.nowak@edu.uni.lodz.pl");
		user.setNrPesel("02070803628");
		user.setNrTel("692504256");
		user.setPlec(Gender.Kobieta);
		
		user.getAdres().setKodPocztowy("95-200");
		user.getAdres().setMiejscowosc("Pabianice");
		user.getAdres().setNrLokalu("32");
		user.getAdres().setNrPosesji("6/8");
		user.getAdres().setUlica("Ostatnia");
		
		//user.forgetUser();
		
		JButton addUser = new JButton("Dodaj");
		addUser.setEnabled(true);
		addUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JDialog subframe = new JDialog(frame, "Dodaj użytkownika");
				EditUserPanel eup = new EditUserPanel();
				subframe.getContentPane().add(new JScrollPane(eup));
				
				subframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				subframe.setSize(400, 520);
				subframe.setLocation(frame.getLocation());
				subframe.setVisible(true);
				
				//eup.setUzytkownik(user);
				eup.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae2) {
						(new SwingWorker<Void, Void>(){

							@Override
							protected Void doInBackground() throws Exception {
								try {
									serverHandler.addUser(eup.getUzytkownik());
									subframe.dispose();
									userListModel.setUserList(serverHandler.getUsers());
								} catch(Exception e) {
									JOptionPane.showMessageDialog(subframe, "Błąd przy dodawaniu użytkownika! " + e.getMessage(), "BŁĄD!", JOptionPane.ERROR_MESSAGE);
									e.printStackTrace();
								}
								
								return null;
							}
							
						}).execute();
					}
				});
			}
		});
		
		JButton editUser = new JButton("Edytuj");
		editUser.setEnabled(false);
		editUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(userList.getSelectedIndex() < 0)
					return;
				User u = userList.getModel().getElementAt(userList.getSelectedIndex());
				JDialog subframe = new JDialog(frame, "Edytuj użytkownika");
				EditUserPanel eup = new EditUserPanel();
				
				EditUserPermissionPanel perms = new EditUserPermissionPanel();
				perms.setUprawnienia(u.getUprawnienia());
				
				JTabbedPane tabs = new JTabbedPane();
				tabs.addTab("Dane", new JScrollPane(eup));
				tabs.addTab("Uprawnienia", new JScrollPane(perms));
				
				subframe.getContentPane().add(tabs);
				
				subframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				subframe.setSize(400, 520);
				subframe.setLocation(frame.getLocation());
				subframe.setVisible(true);
				
				eup.setUzytkownik(u);
				eup.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae2) {
						HashSet<Permission> editedPerms = perms.getUprawnienia();
						
						if(editedPerms.isEmpty() && !u.isForgotten()) {
							JOptionPane.showMessageDialog(subframe, "Użytkownik musi posiadać przynajmniej jedno uprawnienie!", "BŁĄD!", JOptionPane.ERROR_MESSAGE);
							return;
						}
						
						(new SwingWorker<Void, Void>(){

							@Override
							protected Void doInBackground() throws Exception {
								try {
									User newUser = eup.getUzytkownik();
									serverHandler.editUser(newUser, eup.getOriginalLogin());
									serverHandler.editUserPermissions(newUser, editedPerms);
									subframe.dispose();
									userListModel.setUserList(serverHandler.getUsers());
									userInfo.setUserInfo(userList.getModel().getElementAt(userList.getSelectedIndex()));
								} catch(Exception e) {
									JOptionPane.showMessageDialog(subframe, "Błąd przy edytowaniu użytkownika! " + e.getMessage(), "BŁĄD!", JOptionPane.ERROR_MESSAGE);
								}
								
								return null;
							}
							
						}).execute();
					}
				});
			}
		});
		
		JButton forgetUser = new JButton("Zapomnij");
		forgetUser.setEnabled(false);
		forgetUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(userList.getSelectedIndex() < 0)
					return;
				
				User selected = userList.getSelectedValue();
				
				int result = JOptionPane.showConfirmDialog(frame, "Zapomnieć użytkownika " + selected.getLogin() + "?", "Zapomnij użytkownika", JOptionPane.YES_NO_OPTION);
				if(result != JOptionPane.YES_OPTION)
					return;
				
				(new SwingWorker<Void, Void>(){

					@Override
					protected Void doInBackground() throws Exception {
						try {
							serverHandler.forgetUser(selected);
							userListModel.setUserList(serverHandler.getUsers());
							userInfo.setUserInfo(userList.getModel().getElementAt(userList.getSelectedIndex()));
						} catch(Exception e) {
							JOptionPane.showMessageDialog(frame, "Błąd przy zapominaniu użytkownika! " + e.getMessage(), "BŁĄD!", JOptionPane.ERROR_MESSAGE);
						}
						
						return null;
					}
					
				}).execute();
				
				userInfo.setUserInfo(userList.getModel().getElementAt(userList.getSelectedIndex()));
			}
		});
		
		userList.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;
				
				boolean selected = userList.getSelectedIndex() > -1;
				
				editUser.setEnabled(selected);
				forgetUser.setEnabled(selected);
				
				if(selected) {
					userInfo.setUserInfo(userList.getModel().getElementAt(userList.getSelectedIndex()));
				} else
					userInfo.setText("<body></body>");
			}
			
		});
		
		JList<Permission> permList = new JList<Permission>();
		permList.setModel(new PermissionListModel());
		
		permList.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;
				
				boolean selected = permList.getSelectedIndex() > -1;
				
				if(selected) {
					Permission p = permList.getSelectedValue();
					
					ArrayList<User> usersByPerm = new ArrayList<User>();
					
					for(User u : userListModel.getAllUsers()) {
						if(u.hasPermission(p))
							usersByPerm.add(u);
					}
					
					userInfo.setPermissionInfo(permList.getModel().getElementAt(permList.getSelectedIndex()), usersByPerm);
				} else
					userInfo.setText("<body></body>");
			}
			
		});
		
		JPanel leftPanelPerm = new JPanel(new BorderLayout());
		leftPanelPerm.add(new JScrollPane(permList));
		
		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.add(new JScrollPane(userList), "Center");
		
		JPanel entcpl = new JPanel();
		entcpl.setLayout(new BoxLayout(entcpl, BoxLayout.Y_AXIS));
		
		JButton filterEnt = new JButton("Filtruj");
		filterEnt.setToolTipText("Filter the entitiy list, hold Shift to clear the filter");
		JTextField findtext = new JTextField();
		findtext.setToolTipText("Text to search for");
		
		findtext.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				update();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				update();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				update();
			}
			
			public void update() {
				userListModel.setCriterium(findtext.getText().trim());
			}
		});
		
		filterEnt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JDialog subframe = new JDialog(frame, "Filtruj");
				
				JPanel panel = new JPanel();
				BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
				panel.setLayout(layout);
				
				EditUserPermissionPanel p = new EditUserPermissionPanel();
				
				final HashSet<Permission> perms = userListModel.getPermissions();
				
				p.setUprawnienia(perms);
				
				panel.add(new JLabel("Filtruj uprawnienia"));
				panel.add(p);
				JCheckBox checkBox2 = new JCheckBox("Musi posiadać WSZYSTKIE");
				panel.add(checkBox2);
				panel.add(new JLabel("Status użytkownika"));
				
				JCheckBox checkBox = new JCheckBox("Tylko zapomnieni");
				panel.add(checkBox);
				
				checkBox.setSelected(userListModel.isShowForgotten());
				
				JButton filtruj = new JButton("Filtruj");
				
				filtruj.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						userListModel.setShowForgotten(checkBox.isSelected());
						userListModel.setAllPerms(checkBox2.isSelected());
						userListModel.setPermissions(p.getUprawnienia());
						userListModel.filter();
					}
				});
				
				panel.add(filtruj);
				
				subframe.getContentPane().add(new JScrollPane(panel));
				
				subframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				subframe.setSize(300, 250);
				subframe.setLocation(frame.getLocation());
				subframe.setVisible(true);
			}
		});
		
		Box fbox = Box.createHorizontalBox();

		fbox.add(findtext);
		fbox.add(filterEnt);
		
		Box fbox2 = Box.createHorizontalBox();
		fbox2.add(addUser);
		fbox2.add(editUser);
		fbox2.add(forgetUser);
		
		entcpl.add((Component) fbox);
		entcpl.add((Component) fbox2);

		leftPanel.add((Component) entcpl, "South");
		
		JTabbedPane mainTabs = new JTabbedPane();
		mainTabs.addTab("Użytkownicy", leftPanel);
		mainTabs.addTab("Uprawnienia", leftPanelPerm);
		
		JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mainTabs, userInfo);
		
		frame.getContentPane().add(mainSplit);
		mainSplit.setDividerLocation(275);
		mainSplit.setResizeWeight(0.5);
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(720, 520);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
