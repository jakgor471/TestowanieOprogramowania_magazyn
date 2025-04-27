package gui;

import java.awt.BorderLayout;
import java.util.HashSet;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.html.HTMLEditorKit;

import shared.DataValidation;
import shared.Permission;
import shared.User;

public class InfoPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	JTextPane textPane;

	public InfoPanel() {
		this.setLayout(new BorderLayout());
		textPane = new JTextPane();
		textPane.setEditable(false);

		HTMLEditorKit ek = new HTMLEditorKit();
		textPane.setEditorKit(ek);

		JScrollPane scp = new JScrollPane(textPane);

		this.add(scp);
		scp.getVerticalScrollBar().setValue(0);
	}

	public void setText(String text) {
		textPane.setText(text);
		textPane.setCaretPosition(0);
	}
	
	public void setUserInfo(User u) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<h1>").append(u.getLogin()).append("</h1><hr>");
		sb.append("<b>Imię</b>: ").append(u.getImie()).append("<br>");
		sb.append("<b>Nazwisko</b>: ").append(u.getNazwisko()).append("<br><br>");
		
		if(!u.isForgotten()) {
			sb.append("<b>Adres zamieszkania</b><br>");
			sb.append("<b>Miejscowość</b>: ").append(u.getAdres().getMiejscowosc()).append("<br>");
			sb.append("<b>Kod pocztowy</b>: ").append(u.getAdres().getKodPocztowy()).append("<br>");
			sb.append("<b>Ulica</b>: ").append(u.getAdres().getUlica()).append("<br>");
			sb.append("<b>Nr posesji</b>: ").append(u.getAdres().getNrPosesji()).append("<br>");
			sb.append("<b>Nr lokalu</b>: ").append(u.getAdres().getNrLokalu()).append("<br><br>");
			
			sb.append("<b>Nr PESEL</b>: ").append(u.getNrPesel()).append("<br>");
			sb.append("<b>Data urodzenia</b>: ").append(DataValidation.dateToString(u.getDataUrodzenia())).append("<br>");
			sb.append("<b>Płeć</b>: ").append(u.getPlec()).append("<br>");
			sb.append("<b>Adres e-mail</b>: ").append(u.getEmail()).append("<br>");
			sb.append("<b>Nr telefonu</b>: ").append(u.getNrTel()).append("<br>");
		} else {
			sb.append("<b>Data zapomnienia</b>: ").append(DataValidation.dateToString(u.getDataZapomnienia())).append("<br>");
			sb.append("<b>Zapomniany przez</b>: ").append(u.getZapomnianyPrzez()).append("<br>");
		}
		
		sb.append("<h2>Uprawnienia</h2>");
		HashSet<Permission> perms = u.getUprawnienia();
		if(!perms.isEmpty()) {
			sb.append("<ul>");
			for(Permission p : perms) {
				sb.append("<li>").append(p.toString()).append("</li>");
			}
			sb.append("</ul>");
		}
		
		setText(sb.toString());
	}
}
