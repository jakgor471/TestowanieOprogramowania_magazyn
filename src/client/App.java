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
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import gui.EditUserPanel;
import gui.EditUserPermissionPanel;
import gui.FilteredUserListModel;
import gui.InfoPanel;
import gui.LoginPanel;
import gui.LoginPanel.Type;
import gui.PermissionListModel;
import server.ClientCommunicationHandler;
import server.DBServer;
import shared.DataValidation;
import shared.Permission;
import shared.User;
import shared.User.Gender;

public class App {
	public static final String appTytul = "Magazyn";
	
	private static void exec(ServerCommunicationHandler serverHandler) {
		//TODO Radio buttony w filtrowaniu zamiast checkboxa WSZYSTKIE, zrobione
		if(serverHandler.getSession() == null)
			return;
		
		JFrame frame = new JFrame(appTytul + " - zalogowano jako: " + serverHandler.getSession().getLogin());
		JMenuBar menu = new JMenuBar();
		
		JMenu sessionMenu = new JMenu("Sesja");
		menu.add(sessionMenu);
		
		JMenuItem logout = new JMenuItem("Wyloguj");
		logout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int option = JOptionPane.showConfirmDialog(
				    frame,
				    "Czy na pewno wylogować?",
				    "Wylogowanie",
				    JOptionPane.OK_CANCEL_OPTION,
				    JOptionPane.PLAIN_MESSAGE
				);
				
				if(option != JOptionPane.OK_OPTION)
					return;
				
				frame.dispose();
				serverHandler.endSession();
				login(serverHandler);
			}
		});
		sessionMenu.add(logout);
		
		frame.setJMenuBar(menu);
		
		List<User> users = serverHandler.getUsers();
		
		if(users == null) {
			JOptionPane.showMessageDialog(frame, "Nie można wczytać użytkowników! Upewnij się, że posiadasz uprawnienia do systemu.", "BŁĄD!", JOptionPane.ERROR_MESSAGE);
			users = new ArrayList<>();
		}
		
		FilteredUserListModel userListModel = new FilteredUserListModel(users);
		
		JList<User> userList = new JList<User>();
		userList.setModel(userListModel);
		
		InfoPanel userInfo = new InfoPanel();
		
		User user = new User();
		user.setLogin("login56");
		user.setName("Jakub");
		user.setLastname("Nowak");
		user.setEmail("jakub.nowak@edu.uni.lodz.pl");
		user.setNrPesel("02070803628");
		user.setNrTel("692504256");
		user.setGender(Gender.Kobieta);
		
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
				
				eup.setUzytkownik(user);
				eup.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae2) {
						(new SwingWorker<Void, Void>(){

							@Override
							protected Void doInBackground() throws Exception {
								try {
									serverHandler.addUser(eup.getUzytkownik());
									subframe.dispose();
									userListModel.setUserList(serverHandler.getUsers());
									userList.setModel(userListModel);
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
				User selectedUser = userList.getModel().getElementAt(userList.getSelectedIndex());
				JDialog subframe = new JDialog(frame, "Edytuj użytkownika");
				EditUserPanel eup = new EditUserPanel(serverHandler.getSession().isAdmin());
				
				EditUserPermissionPanel perms = new EditUserPermissionPanel();
				perms.setPermissions(selectedUser.getPermissions());
				
				JTabbedPane tabs = new JTabbedPane();
				tabs.addTab("Dane", new JScrollPane(eup));
				
				if(serverHandler.getSession().hasPermission(Permission.UserPermission))
					tabs.addTab("Uprawnienia", new JScrollPane(perms));
				
				subframe.getContentPane().add(tabs);
				
				subframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				subframe.setSize(400, 520);
				subframe.setLocation(frame.getLocation());
				subframe.setVisible(true);
				
				eup.setUzytkownik(selectedUser);
				eup.addSetPasswordActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae2) {
						String password;
						
						boolean valid = false;
						do {
							JPasswordField passwordField = new JPasswordField();
							JPasswordField passwordField2 = new JPasswordField();
							Object[] message = {
								"Nowe hasło:", passwordField,
							    "Powtórz hasło:", passwordField2,
							};

							int option = JOptionPane.showConfirmDialog(
							    subframe,
							    message,
							    "Ustaw/zmień hasło",
							    JOptionPane.OK_CANCEL_OPTION,
							    JOptionPane.PLAIN_MESSAGE
							);
							
							if(option != JOptionPane.OK_OPTION)
								return;
							
							password = new String(passwordField.getPassword());
							String password2 = new String(passwordField2.getPassword());
							
							if(!password.equals(password2)) {
								JOptionPane.showMessageDialog(subframe, "Podane hasła nie są takie same", "BŁĄD!", JOptionPane.ERROR_MESSAGE);
								continue;
							}
							
							try {
								valid = DataValidation.validatePassword(password);
							} catch(IllegalArgumentException e) {
								JOptionPane.showMessageDialog(subframe, e.getMessage(), "BŁĄD!", JOptionPane.ERROR_MESSAGE);
							}
						} while(!valid);
						
						try {
							serverHandler.editUserPassword(selectedUser.getLogin(), password);
						} catch(Exception e) {
							JOptionPane.showMessageDialog(subframe, e.getMessage(), "BŁĄD!", JOptionPane.ERROR_MESSAGE);
							return;
						}
						
						JOptionPane.showMessageDialog(subframe, "Zmieniono hasło.", "INFO", JOptionPane.INFORMATION_MESSAGE);
					}
				});
				eup.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae2) {
						HashSet<Permission> editedPerms = perms.getPermissions();
						
						if(editedPerms.isEmpty() && !selectedUser.isForgotten()) {
							JOptionPane.showMessageDialog(subframe, "Użytkownik musi posiadać przynajmniej jedno uprawnienie!", "BŁĄD!", JOptionPane.ERROR_MESSAGE);
							return;
						}
						
						(new SwingWorker<Void, Void>(){

							@Override
							protected Void doInBackground() throws Exception {
								try {
									User newUser = eup.getUzytkownik();
									serverHandler.editUser(newUser, eup.getOriginalLogin());
									serverHandler.editUserPermissions(newUser, perms.getPermissions());
									subframe.dispose();
									userListModel.setUserList(serverHandler.getUsers());
									//userList.setModel(userListModel);
									
									if(userList.getSelectedIndex() > -1)
										userInfo.setUserInfo(userList.getModel().getElementAt(userList.getSelectedIndex()));
									
									if(!perms.getPermissions().equals(selectedUser.getPermissions()))
										JOptionPane.showMessageDialog(subframe, "Zmieniono uprawnienia użytkownika.", "INFO", JOptionPane.INFORMATION_MESSAGE);
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
							userList.setModel(userListModel);
							if(userList.getSelectedIndex() > -1)
								userInfo.setUserInfo(userList.getModel().getElementAt(userList.getSelectedIndex()));
							
						} catch(Exception e) {
							JOptionPane.showMessageDialog(frame, "Błąd przy zapominaniu użytkownika! " + e.getMessage(), "BŁĄD!", JOptionPane.ERROR_MESSAGE);
						}
						
						return null;
					}
					
				}).execute();
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
				
				p.setPermissions(perms);
				
				panel.add(new JLabel("Musi posiadać: "));
				panel.add(p);
				JRadioButton radio1 = new JRadioButton("Wszystkie zaznaczone uprawnienia");
				JRadioButton radio2 = new JRadioButton("Przynajmniej jedno zaznaczone uprawnienie");
				ButtonGroup bg = new ButtonGroup();
				bg.add(radio1);
				bg.add(radio2);
				radio1.setSelected(userListModel.isAllPerms());
				radio2.setSelected(!userListModel.isAllPerms());
				panel.add(radio1);
				panel.add(radio2);
				panel.add(new JLabel("Status użytkownika"));
				
				JCheckBox checkBox = new JCheckBox("Tylko zapomnieni");
				panel.add(checkBox);
				
				checkBox.setSelected(userListModel.isShowForgotten());
				
				JButton filtruj = new JButton("Filtruj");
				
				filtruj.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						userListModel.setShowForgotten(checkBox.isSelected());
						userListModel.setAllPerms(radio1.isSelected());
						userListModel.setPermissions(p.getPermissions());
						userListModel.filter();
					}
				});
				
				panel.add(filtruj);
				
				subframe.getContentPane().add(new JScrollPane(panel));
				
				subframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				subframe.setSize(300, 350);
				subframe.setLocation(frame.getLocation());
				subframe.setVisible(true);
			}
		});
		
		Box fbox = Box.createHorizontalBox();

		fbox.add(findtext);
		fbox.add(filterEnt);
		
		Box fbox2 = Box.createHorizontalBox();
		if(serverHandler.getSession().hasPermission(Permission.UserAdd))
			fbox2.add(addUser);
		if(serverHandler.getSession().hasPermission(Permission.UserEdit))
			fbox2.add(editUser);
		if(serverHandler.getSession().hasPermission(Permission.UserForget))
			fbox2.add(forgetUser);
		
		entcpl.add((Component) fbox);
		entcpl.add((Component) fbox2);

		leftPanel.add((Component) entcpl, "South");
		
		JTabbedPane mainTabs = new JTabbedPane();
		mainTabs.addTab("Użytkownicy", leftPanel);
		if(serverHandler.getSession().hasPermission(Permission.Administrator))
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
	
	private static void resetPassword(ServerCommunicationHandler serverHandler) {
		JFrame loginFrame = new JFrame(appTytul + " - Resetowanie hasła");
		LoginPanel loginPane = new LoginPanel(Type.PasswordReset);
		loginFrame.getContentPane().add(loginPane);
		
		loginPane.addLoginActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(!loginPane.getPassword().equals(loginPane.getPassword2())) {
					JOptionPane.showMessageDialog(loginFrame, "Hasła muszą być takie same!", "BŁĄD!", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				try {
					serverHandler.editUserPassword(serverHandler.getSession().getLogin(), loginPane.getPassword());
				} catch(Exception e) {
					JOptionPane.showMessageDialog(loginFrame, e.getMessage(), "BŁĄD!", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				JOptionPane.showMessageDialog(loginFrame, "Hasło zmienione. Proszę zalogować się ponownie.", "INFO", JOptionPane.INFORMATION_MESSAGE);
				loginFrame.dispose();
				serverHandler.endSession();
				login(serverHandler);
			}
		});
		
		loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		loginFrame.setSize(320, 220);
		loginFrame.setLocationRelativeTo(null);
		loginFrame.setVisible(true);
	}
	
	private static void login(ServerCommunicationHandler serverHandler) {
		JFrame loginFrame = new JFrame(appTytul + " - Logowanie");
		LoginPanel loginPane = new LoginPanel(Type.Login);
		loginFrame.getContentPane().add(loginPane);
		
		loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		loginFrame.setSize(320, 220);
		loginFrame.setLocationRelativeTo(null);
		loginFrame.setVisible(true);
		
		loginPane.addLoginActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if(!serverHandler.createSession(loginPane.getLogin(), loginPane.getPassword())) {
						JOptionPane.showMessageDialog(loginFrame, "Niepoprawne dane logowania!", "BŁĄD!", JOptionPane.ERROR_MESSAGE);
						return;
					}
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(loginFrame, e1.getMessage(), "BŁĄD!", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				if(serverHandler.getSession().isOneTime()) {
					loginFrame.dispose();
					resetPassword(serverHandler);
					return;
				}
				
				loginFrame.dispose();
				exec(serverHandler);
			}
		});
		
		loginPane.addForgotActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JDialog subframe = new JDialog(loginFrame, appTytul + " - odzyskiwanie hasła");
				LoginPanel forgotPane = new LoginPanel(Type.ForgotPassword);
				subframe.getContentPane().add(forgotPane);
				
				forgotPane.addLoginActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {
							serverHandler.resetUserPassword(forgotPane.getLogin(), forgotPane.getEmail());
						}catch(IllegalArgumentException e1) {
							JOptionPane.showMessageDialog(loginFrame, e1.getMessage(), "BŁĄD!", JOptionPane.ERROR_MESSAGE);
							return;
						}
						
						JOptionPane.showMessageDialog(subframe, "Wygenerowane hasło zostanie wysłane na podany e-mail.", "INFO", JOptionPane.INFORMATION_MESSAGE);
						subframe.dispose();
					}
				});
				
				subframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				subframe.setSize(320, 220);
				subframe.setLocationRelativeTo(null);
				subframe.setVisible(true);
			}
		});
	}
	
	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		DBServer serwer = new DBServer();
		ClientCommunicationHandler serverHandler = new ClientCommunicationHandler(serwer);
		
		//serverHandler.createSession("administrator", "ZAQ!2wsx");
		if(serverHandler.getSession() == null)
			login(serverHandler);
		else
			exec(serverHandler);
	}
}
