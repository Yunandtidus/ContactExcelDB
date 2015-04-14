package com.mva.gui;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mva.model.SousCategorie;

public final class SousCategorieTabItem {
	private static final Logger log = LoggerFactory
			.getLogger(SousCategorieTabItem.class);
	private static SousCategorieTabItem singleton;
	private TabItem tabItem;
	private ListViewer listSousCategories;

	public static SousCategorieTabItem getInstance() {
		if (singleton == null) {
			singleton = new SousCategorieTabItem();
		}
		return singleton;
	}

	public void createTabItem(TabFolder parent) {
		this.tabItem = new TabItem(parent, 0);
		this.tabItem.setText("Sous Catégories");

		Composite conteneur = new Composite(parent, 2048);
		this.tabItem.setControl(conteneur);

		conteneur.setLayout(new GridLayout(1, true));

		creationToolBar(conteneur);

		this.listSousCategories = new ListViewer(conteneur, 2562);
		this.listSousCategories.getList().setLayoutData(
				new GridData(4, 4, true, true));
		this.listSousCategories
				.setContentProvider(new IStructuredContentProvider() {
					public Object[] getElements(Object inputElement) {
						@SuppressWarnings("unchecked")
						java.util.List<SousCategorie> v = (java.util.List<SousCategorie>) inputElement;
						return v.toArray();
					}

					public void dispose() {
						System.out.println("Disposing ...");
					}

					public void inputChanged(Viewer viewer, Object oldInput,
							Object newInput) {
						SousCategorieTabItem.log.debug("Input changed: old="
								+ oldInput + ", new=" + newInput);
					}
				});
		this.listSousCategories.setLabelProvider(new LabelProvider() {
			public Image getImage(Object element) {
				return null;
			}

			public String getText(Object element) {
				SousCategorie el = (SousCategorie) element;
				return el.getNomSousCategorieAffichable();
			}
		});
		this.listSousCategories
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent sce) {
						if ((sce.getSelection() instanceof IStructuredSelection)) {
							IStructuredSelection selection = (IStructuredSelection) sce
									.getSelection();
							SousCategorieTabItem.this
									.toDoOnSelection(selection);
						}
					}
				});
	}

	public void setListSousCategories(
			java.util.List<SousCategorie> sousCategories) {
		this.listSousCategories.setInput(sousCategories);
	}

	@SuppressWarnings("unchecked")
	public java.util.List<SousCategorie> getListSousCategories() {
		return (java.util.List<SousCategorie>) this.listSousCategories
				.getInput();
	}

	public void creationToolBar(Composite parent) {
		Composite c = new Composite(parent, 0);
		c.setLayoutData(new GridData(1, 1, false, false));
		c.setLayout(new FillLayout(256));

		Button b = new Button(c, 8);
		b.setText("Sélectionner tout");
		b.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent se) {
				SousCategorieTabItem.this.listSousCategories.getList()
						.selectAll();
				SousCategorieTabItem.this
						.toDoOnSelection((StructuredSelection) SousCategorieTabItem.this.listSousCategories
								.getSelection());
			}

			public void widgetDefaultSelected(SelectionEvent se) {
				throw new UnsupportedOperationException("Not supported yet.");
			}
		});
		b = new Button(c, 8);
		b.setText("Désélectionner tout");
		b.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent se) {
				SousCategorieTabItem.this.listSousCategories.getList()
						.deselectAll();
				SousCategorieTabItem.this
						.toDoOnSelection((StructuredSelection) SousCategorieTabItem.this.listSousCategories
								.getSelection());
			}

			public void widgetDefaultSelected(SelectionEvent se) {
				throw new UnsupportedOperationException("Not supported yet.");
			}
		});
	}

	public void toDoOnSelection(IStructuredSelection selection) {
		Set<String> listAttrDispo = new TreeSet<String>();
		for (Object o : selection.toArray()) {
			SousCategorie cat = (SousCategorie) o;
			for (String sc : cat.getListAttributs()) {
				listAttrDispo.add(sc);
			}
		}
		AttributsTabItem.getInstance().setListAttributs(listAttrDispo);
	}
}
