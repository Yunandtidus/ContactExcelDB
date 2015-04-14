package com.mva.gui;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mva.bd.exception.BDException;
import com.mva.model.Categorie;
import com.mva.model.Contact;
import com.mva.model.SousCategorie;

public class AttributsTabItem {
	private static final Logger logger = LoggerFactory
			.getLogger(MainWindow.class);
	private static AttributsTabItem singleton;
	private Color OK_COLOR;
	private Color KO_COLOR;
	private TabItem tabItem;
	private Map<ExpandItem, Map<String, List<Text>>> mapItemAttributes;
	private Collection<String> listAttributs;
	private Set<Contact> contacts;
	private ExpandBar eBar;

	private AttributsTabItem() {
		this.mapItemAttributes = new HashMap<ExpandItem, Map<String, List<Text>>>();

		this.listAttributs = new ArrayList<String>();
	}

	public static AttributsTabItem getInstance() {
		if (singleton == null) {
			singleton = new AttributsTabItem();
		}
		return singleton;
	}

	public void createTabItem(TabFolder parent) {
		this.OK_COLOR = new Color(Display.getCurrent(), 213, 255, 202);
		this.KO_COLOR = new Color(Display.getCurrent(), 255, 224, 224);

		this.tabItem = new TabItem(parent, 0);
		this.tabItem.setText("Attributs");

		Composite tmp = new Composite(parent, 0);
		tmp.setLayout(new GridLayout());
		this.tabItem.setControl(tmp);

		creationToolBar(tmp);

		this.eBar = new ExpandBar(tmp, 512);
		this.eBar.setLayoutData(new GridData(4, 4, true, true));

		addExpandItem();
		this.eBar.getItem(0).setExpanded(true);
	}

	public void addExpandItem() {
		ScrolledComposite sc = new ScrolledComposite(this.eBar, 768);
		sc.setLayoutData(new GridData(4, 4, true, true, 1, 1));

		Composite mainConteneur = new Composite(sc, 0);
		mainConteneur.setLayout(new GridLayout(6, true));

		sc.setContent(mainConteneur);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setMinSize(mainConteneur.computeSize(-1, -1));

		ExpandItem eItem = new ExpandItem(this.eBar, 0);
		eItem.setText("Contraintes " + this.eBar.getItemCount());
		eItem.setControl(sc);

		setListAttributsPourItem(eItem, this.listAttributs);
	}

	private void setListAttributsPourItem(ExpandItem item,
			Collection<String> list) {
		Map<String, List<Text>> listTexts = new HashMap<String, List<Text>>();
		this.mapItemAttributes.put(item, listTexts);

		Composite cont = (Composite) item.getControl();
		cont = (Composite) cont.getChildren()[0];
		for (Control c : cont.getChildren()) {
			c.dispose();
		}
		for (String s : list) {
			logger.info("affiche attribut " + s);
			Label l = new Label(cont, 0);
			l.setText(s + " : ");
			l.setLayoutData(new GridData(16777224, 16777216, true, true));

			Text t = new Text(cont, 2048);
			t.setLayoutData(new GridData(100, 20));
			t.setBackground(this.OK_COLOR);

			List<Text> texts = new ArrayList<Text>(2);
			texts.add(t);

			t = new Text(cont, 2048);
			t.setLayoutData(new GridData(100, 20));
			t.setBackground(this.KO_COLOR);

			texts.add(t);

			listTexts.put(s, texts);
		}
		cont.layout();

		item.setHeight(cont.computeSize(-1, -1).y);
	}

	public void setListAttributs(Collection<String> list) {
		this.listAttributs = list;

		this.mapItemAttributes.clear();
		for (ExpandItem item : this.eBar.getItems()) {
			setListAttributsPourItem(item, list);
		}
	}

	protected void remplirListeContacts() {
		CategorieTabItem cti = CategorieTabItem.getInstance();
		for (Categorie c : cti.getListCategories()) {
			if (c.getLastModification() != c.getExcelFile().lastModified()) {
				for (SousCategorie sc : c.getSousCategories()) {
					try {
						logger.debug("Recompute d'un fichier modifié");
						sc.compute();
					} catch (BDException e) {
						logger.error(
								"Erreur lors du recalcul d'un fichier modifié",
								e);
					}
				}
			}
		}
		this.contacts = new LinkedHashSet<Contact>();
		Collection<String> titles = new ArrayList<String>();
		for (Map<String, List<Text>> m : this.mapItemAttributes.values()) {
			titles = m.keySet();
			for (SousCategorie sc : SousCategorieTabItem.getInstance()
					.getListSousCategories()) {
				for (Contact c : sc.getListContacts()) {
					boolean addContact = true;
					for (Map.Entry<String, List<Text>> entry : m.entrySet()) {
						logger.info(c.toString() + c.getAttributes().toString()
								+ entry.toString());
						try {
							if (!isValide(c, entry)) {
								addContact = false;
								break;
							}
						} catch (ClassCastException cce) {
							logger.warn("Un champ n'a pas été pris en compte car il n'est pas une chaine de caractères");
						}
					}
					if (addContact) {
						this.contacts.add(c);
					}
				}
			}
		}
		ListeContactResultat lcr = ListeContactResultat.getInstance();

		lcr.setResults(titles, this.contacts);
	}

	public boolean isValide(Contact c, Map.Entry<String, List<Text>> entry) {
		if ((((Text) ((List<Text>) entry.getValue()).get(0)).getText()
				.equals(""))
				&& (((Text) ((List<Text>) entry.getValue()).get(1)).getText()
						.equals(""))) {
			return true;
		}
		if ((!c.getAttributes().containsKey(entry.getKey()))
				|| (c.getAttributes().get(entry.getKey()) == null)) {
			return false;
		}
		String textOK = ((Text) ((List<Text>) entry.getValue()).get(0))
				.getText();
		String textKO = ((Text) ((List<Text>) entry.getValue()).get(1))
				.getText();

		Object contactAttribute = c.getAttributes().get(entry.getKey());
		boolean match = true;

		String attribut = null;
		if ((contactAttribute instanceof String)) {
			attribut = ((String) contactAttribute).toLowerCase();
		} else if ((contactAttribute instanceof Double)) {
			attribut = contactAttribute.toString().replaceAll(" ", "");
		} else {
			logger.warn("Comparaison d'attribut de type inconnu ("
					+ contactAttribute.getClass().toString() + ")");

			return true;
		}
		String[] array = null;
		if (!textOK.equals("")) {
			array = textOK.split(";");
			for (String s : array) {
				s = s.toLowerCase();
				s = s.replaceAll("\\.", "\\\\.");
				s = s.replaceAll("\\*", ".*");
				if ((attribut.contains("/")) && (!attribut.contains("http://"))) {
					String val = attribut;
					val = val.replaceAll(" */", "/");
					val = val.replaceAll("/ *", "/");
					boolean match2 = false;
					for (String str : val.split("/")) {
						match2 |= str.matches(s);
					}
					match &= match2;
				} else {
					match &= attribut.matches(s);
				}
			}
		}
		if (!textKO.equals("")) {
			array = textKO.split(";");
			for (String s : array) {
				s = s.replaceAll("\\.", "\\\\.");
				s = s.replaceAll("\\*", ".*");
				if ((attribut.contains("/")) && (!attribut.contains("http://"))) {
					String val = attribut;
					val = val.replaceAll(" */", "/");
					val = val.replaceAll("/ *", "/");
					boolean match2 = false;
					for (String str : val.split("/")) {
						match2 |= str.matches(s);
					}
					match &= !match2;
				} else {
					match &= !attribut.matches(s);
				}
			}
		}
		return match;
	}

	public void creationToolBar(Composite parent) {
		Composite c = new Composite(parent, 0);
		c.setLayoutData(new GridData(1, 4, false, false));
		c.setLayout(new FillLayout(256));

		Button b1 = new Button(c, 0);
		b1.setText("Valider");

		b1.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent se) {
				AttributsTabItem.this.remplirListeContacts();
			}

			public void widgetDefaultSelected(SelectionEvent se) {
				throw new UnsupportedOperationException("Not supported yet.");
			}
		});
		Button b = new Button(c, 8);
		b.setText("Export");
		b.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent se) {
				FileDialog fd = new FileDialog(Display.getCurrent()
						.getActiveShell(), 8192);
				fd.setFileName("export.xls");
				String path = fd.open();

				ListeContactResultat.getInstance().remplirExcel(path);
			}

			public void widgetDefaultSelected(SelectionEvent se) {
				throw new UnsupportedOperationException("Not supported yet.");
			}
		});
		b = new Button(c, 8);
		b.setText("Envoi mail");
		b.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent se) {
				String mailURL = null;
				try {
					Desktop d = Desktop.getDesktop();

					StringBuilder sb = new StringBuilder();
					sb.append("mailto:");

					Set<String> mails = new HashSet<String>();
					for (Contact c : AttributsTabItem.this.contacts) {
						String mail = null;
						if (c.getAttributes().containsKey("mail")) {
							mail = (String) c.getAttributes().get("mail");
						}
						if (c.getAttributes().containsKey("E-mail")) {
							mail = (String) c.getAttributes().get("E-mail");
						}
						if (c.getAttributes().containsKey("Mail")) {
							mail = (String) c.getAttributes().get("Mail");
						}
						if (c.getAttributes().containsKey("E-Mail")) {
							mail = (String) c.getAttributes().get("E-Mail");
						}
						if (mail != null) {
							Pattern p = Pattern
									.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
							Matcher m = p.matcher(mail);
							while (m.find()) {
								mails.add(m.group());
							}
						}
					}
					for (String s : mails) {
						sb.append(s);
						sb.append(",");
					}
					mailURL = sb.substring(0, sb.length() - 1);

					d.mail(new URI(mailURL));
				} catch (Exception e) {
					Clipboard cb = Toolkit.getDefaultToolkit()
							.getSystemClipboard();
					cb.setContents(new StringSelection(mailURL), null);
					AttributsTabItem.logger.error(mailURL, e);
					MessageDialog
							.openInformation(
									Display.getCurrent().getActiveShell(),
									"Erreur",
									"L'application n'a pas pu ouvrir votre logiciel de messagerie, la liste des mails est dans le presse papier");
				}
			}

			public void widgetDefaultSelected(SelectionEvent se) {
				throw new UnsupportedOperationException("Not supported yet.");
			}
		});
		b = new Button(c, 8);
		b.setText("Ajouter contraintes");
		b.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent se) {
				AttributsTabItem.this.addExpandItem();
			}

			public void widgetDefaultSelected(SelectionEvent se) {
				throw new UnsupportedOperationException("Not supported yet.");
			}
		});
	}
}
