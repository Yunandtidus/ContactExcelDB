package com.mva.gui;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;

import com.mva.bd.ExcelFileParser;

public class MainWindow extends ApplicationWindow {
	private ExcelFileParser efp;
	private CategorieTabItem cti;
	private SousCategorieTabItem scti;
	private AttributsTabItem ati;
	private ListeContactResultat lcr;

	public MainWindow(ExcelFileParser efp) {
		super(null);
		this.efp = efp;

		setBlockOnOpen(true);

		setShellStyle(66800);
		open();
	}

	protected Control createContents(Composite parent) {
		this.seperator1.setVisible(false);
		((Shell) parent).setText("MVA Contacts");
		((Shell) parent).setSize(1024, 768);

		Composite conteneur = new Composite(parent, 0);

		FillLayout layout = new FillLayout(512);
		conteneur.setLayout(layout);

		TabFolder tabFolder = new TabFolder(conteneur, 0);

		this.cti = CategorieTabItem.getInstance();
		this.cti.createTabItem(tabFolder);
		this.cti.setListCategories(this.efp.getAllCategories());

		this.scti = SousCategorieTabItem.getInstance();
		this.scti.createTabItem(tabFolder);

		this.ati = AttributsTabItem.getInstance();
		this.ati.createTabItem(tabFolder);

		this.lcr = ListeContactResultat.getInstance();
		this.lcr.createContent(conteneur);

		return parent;
	}
}
