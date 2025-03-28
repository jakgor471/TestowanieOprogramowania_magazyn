package client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import gui.EditUserPanel;
import gui.FilteredUserListModel;
import gui.InfoPanel;
import server.ClientCommunicationHandler;
import server.DBServer;
import shared.DataValidation;
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
		
		user.forgetUser();
		
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
				JDialog subframe = new JDialog(frame, "Edytuj użytkownika");
				EditUserPanel eup = new EditUserPanel();
				subframe.getContentPane().add(new JScrollPane(eup));
				
				subframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				subframe.setSize(400, 520);
				subframe.setLocation(frame.getLocation());
				subframe.setVisible(true);
				
				eup.setUzytkownik(userList.getModel().getElementAt(userList.getSelectedIndex()));
				eup.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae2) {
						(new SwingWorker<Void, Void>(){

							@Override
							protected Void doInBackground() throws Exception {
								try {
									serverHandler.editUser(eup.getUzytkownik(), eup.getOriginalLogin());
									subframe.dispose();
									userListModel.setUserList(serverHandler.getUsers());
									userInfo.setUserInfo(userList.getModel().getElementAt(userList.getSelectedIndex()));
								} catch(Exception e) {
									JOptionPane.showMessageDialog(subframe, "Błąd przy dodawaniu użytkownika! " + e.getMessage(), "BŁĄD!", JOptionPane.ERROR_MESSAGE);
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
				}
			}
			
		});
		
		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.add(new JScrollPane(userList), "Center");
		
		JPanel entcpl = new JPanel();
		entcpl.setLayout(new BoxLayout(entcpl, BoxLayout.Y_AXIS));
		
		JButton filterEnt = new JButton("Zapomnieni");
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
				userListModel.setShowForgotten(!userListModel.isShowForgotten());
				filterEnt.setText(userListModel.isShowForgotten() ? "Aktywni" : "Zapomnieni");
				userListModel.filter();
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
		
		JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, userInfo);
		
		/*User user = new User();
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
		
		for(int i = 0; i < 10; ++i) {
			user.forgetUser();
			DataValidation.validatePesel(user.getNrPesel(), user.getDataUrodzenia(), user.getPlec());
		}
		
		lsh.createSession("admin", "admin");
		lsh.addUser(user);
		
		user.setLogin("haslo123");
		user.getAdres().setMiejscowosc("Świętochłowice");
		lsh.editUser(user, "login133");
		
		eup.setUzytkownik(user);*/
		//eup.setEditable(false);
		
		frame.getContentPane().add(mainSplit);
		mainSplit.setDividerLocation(275);
		mainSplit.setResizeWeight(0.5);
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(720, 520);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
