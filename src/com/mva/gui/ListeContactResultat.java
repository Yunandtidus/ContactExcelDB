package com.mva.gui;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mva.model.Contact;

public class ListeContactResultat {
	private static final Logger Log = LoggerFactory
			.getLogger(ListeContactResultat.class);
	private static ListeContactResultat singleton;
	private NumberFormat nf;
	private Composite parent;
	private TableViewer tView;
	private List<String> baseTitles;
	private SimpleDateFormat df;
	private WritableCellFormat cf;

	protected ListeContactResultat() {
		this.nf = new DecimalFormat();
		this.nf.setMaximumFractionDigits(0);
		this.nf.setMaximumFractionDigits(0);
		this.nf.setMinimumIntegerDigits(5);
		this.nf.setMinimumIntegerDigits(5);

		this.df = new SimpleDateFormat("dd/MM/yyyy");

		String[] array = { "Organisme", "Civilité", "Prénom", "Nom", "Adresse",
				"BP", "CP", "Ville", "Tél", "Portable", "Fax", "E-Mail",
				"Site Internet", "Site", "Structures", "Média", "Nom du chœur",
				"Lieu siège social", "Adresse siège social", "CP siège social",
				"Ville siège social", "Tél siège social", "E-mail chœur",
				"Site internet chœur", "Nom président", "Prénom président",
				"Adresse président", "CP président", "Ville président",
				"Tél président", "Tél portable président", "E-mail président",
				"Nom Chef de chœur", "Prénom chef de chœur",
				"Adresse chef de chœur", "CP chef de chœur",
				"Ville chef de chœur", "Tél chef de chœur",
				"Tél portable chef de chœur", "E-mail chef de chœur",
				"Nom 2è Chef de chœur", "Prénom 2è chef de chœur",
				"Adresse 2è chef de chœur", "CP 2è chef de chœur",
				"Ville 2è chef de chœur", "Tél 2è chef de chœur",
				"E-mail 2è chef de chœur", "Nom autre contact",
				"Prénom autre contact", "Fonction autre contact",
				"Adresse autre contact", "CP autre contact",
				"Ville autre contact", "Tél autre contact",
				"Tél portable autre contact", "E-mail autre contact",
				"Lieu de répétition (mairie, école…)", "Adresse répétition",
				"CP répétition", "Ville répétition", "Jour répétition",
				"Horaires répétition", "Souhaite figurer ds annuaire en ligne",
				"Typologie de chœur", "Contexte - rattachement",
				"Répertoire pratiqué régulièrement", "Accueil handicaps",
				"Nom structure juridique", "Adresse structure juridique",
				"Code postal structure juridique", "Ville structure juridique",
				"Courriel structure juridique",
				"Site internet structure juridique",
				"Téléphone structure juridique",
				"Téléphone portable structure juridique",
				"Fax structure juridique", "Typologie du chœur",
				"Effectif global",
				"Le répertoire du choeur est un répertoire :",
				"Répertoire privilégié par le chœur",
				"Répertoire pratiqué régulièrement",
				"Fonction liturgique ?, Si oui, c'est :" };

		this.baseTitles = new ArrayList<String>(Arrays.asList(array));
	}

	public static ListeContactResultat getInstance() {
		if (singleton == null) {
			singleton = new ListeContactResultat();
		}
		return singleton;
	}

	public Control createContent(Composite parent) {
		this.parent = new Composite(parent, 0);
		this.parent.setLayout(new FillLayout());
		return parent;
	}

	public void setResults(Collection<String> columnsTitles,
			Collection<Contact> contacts) {
		for (Control c : this.parent.getChildren()) {
			c.dispose();
		}
		this.tView = new TableViewer(this.parent);
		this.tView.setLabelProvider(new ITableLabelProvider() {
			public void addListener(ILabelProviderListener il) {
			}

			public void dispose() {
			}

			public boolean isLabelProperty(Object o, String string) {
				return true;
			}

			public void removeListener(ILabelProviderListener il) {
			}

			public Image getColumnImage(Object o, int i) {
				return null;
			}

			public String getColumnText(Object o, int i) {
				String title = ListeContactResultat.this.tView.getTable()
						.getColumn(i).getText();
				Map<String, Object> map = ((Contact) o).getAttributes();
				ListeContactResultat.Log.debug(title + " => " + map.keySet());
				try {
					if ((map.containsKey(title)) && (map.get(title) != null)) {
						if ((map.get(title) instanceof Date)) {
							return ListeContactResultat.this.df.format(map
									.get(title));
						}
						return map.get(title).toString();
					}
				} catch (Exception e) {
					return map.get(title).toString();
				}
				return "";
			}
		});
		this.tView.setContentProvider(new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {
				@SuppressWarnings("unchecked")
				Collection<Contact> v = (Collection<Contact>) inputElement;
				return v.toArray();
			}

			public void dispose() {
				ListeContactResultat.Log.debug("Disposing ...");
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				ListeContactResultat.Log.debug("Input changed: old=" + oldInput
						+ ", new=" + newInput);
			}
		});
		List<String> titles = new ArrayList<String>(this.baseTitles);
		for (String s : columnsTitles) {
			if (!titles.contains(s)) {
				titles.add(s);
			}
		}
		List<String> toRemove = new ArrayList<String>();
		for (String s : titles) {
			if (!columnsTitles.contains(s)) {
				toRemove.add(s);
			}
		}
		titles.removeAll(toRemove);
		for (String columTitle : titles) {
			TableColumn column = new TableColumn(this.tView.getTable(), 0);

			column.setText(columTitle);
		}
		this.tView.getTable().setLinesVisible(true);
		this.tView.getTable().setHeaderVisible(true);

		this.tView.setInput(contacts);
		for (TableColumn tc : this.tView.getTable().getColumns()) {
			tc.pack();
		}
		this.parent.layout();
	}

	public void remplirExcel(String path) {
		try {
			File file = new File(path);

			WorkbookSettings wbSettings = new WorkbookSettings();

			wbSettings.setLocale(new Locale("fr", "FR"));

			WritableWorkbook workbook = Workbook.createWorkbook(file,
					wbSettings);

			WritableSheet excelSheet = workbook.createSheet("Feuil1", 0);

			this.cf = new WritableCellFormat();

			int c = 0;
			for (TableColumn tc : this.tView.getTable().getColumns()) {
				addLabel(excelSheet, 0, c, tc.getText());
				CellView cv = excelSheet.getColumnView(c);
				cv.setAutosize(true);
				excelSheet.setColumnView(c, cv);
				c++;
			}
			int r = 1;
			for (TableItem ti : this.tView.getTable().getItems()) {
				for (int col = 0; col < c; col++) {
					addLabel(excelSheet, r, col, ti.getText(col));
				}
				r++;
			}
			workbook.write();
			workbook.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void addLabel(WritableSheet sheet, int row, int column, String s)
			throws WriteException, RowsExceededException {
		Label label = new Label(column, row, s, this.cf);
		sheet.addCell(label);
	}
}
