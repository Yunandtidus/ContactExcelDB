package com.mva.gui;

import java.util.ArrayList;
import java.util.Collection;

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

import com.mva.model.Categorie;
import com.mva.model.SousCategorie;

public final class CategorieTabItem {
	private static final Logger logger = LoggerFactory
			.getLogger(CategorieTabItem.class);
	private static CategorieTabItem singleton;
	private TabItem tabItem;
	private ListViewer listCategories;
	private Composite conteneur;
	private Collection<Categorie> input;

	public static CategorieTabItem getInstance() {
		if (singleton == null) {
			singleton = new CategorieTabItem();
		}
		return singleton;
	}

	public void createTabItem(TabFolder parent) {
		this.tabItem = new TabItem(parent, 0);
		this.tabItem.setText("Catégories");

		this.conteneur = new Composite(parent, 0);
		this.tabItem.setControl(this.conteneur);

		this.conteneur.setLayout(new GridLayout(1, true));

		creationToolBar(this.conteneur);

		this.listCategories = new ListViewer(this.conteneur, 2562);
		this.listCategories.getList().setLayoutData(
				new GridData(4, 4, true, true));
		this.listCategories
				.setContentProvider(new IStructuredContentProvider() {
					public Object[] getElements(Object inputElement) {
						@SuppressWarnings("unchecked")
						java.util.List<Categorie> v = (java.util.List<Categorie>) inputElement;
						return v.toArray();
					}

					public void dispose() {
						CategorieTabItem.logger.debug("Disposing ...");
					}

					public void inputChanged(Viewer viewer, Object oldInput,
							Object newInput) {
						CategorieTabItem.logger.debug("Input changed: old="
								+ oldInput + ", new=" + newInput);
					}
				});
		this.listCategories.setLabelProvider(new LabelProvider() {
			public Image getImage(Object element) {
				return null;
			}

			public String getText(Object element) {
				return ((Categorie) element).getPrintableCategorieName();
			}
		});
		this.listCategories
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent sce) {
						if ((sce.getSelection() instanceof IStructuredSelection)) {
							IStructuredSelection selection = (IStructuredSelection) sce
									.getSelection();
							CategorieTabItem.this.toDoOnSelection(selection);
						}
					}
				});
	}

	public void setListCategories(java.util.List<Categorie> categories) {
		this.input = categories;
		this.listCategories.setInput(categories);
		logger.debug("doLayout");
		this.conteneur.layout();
	}

	public void creationToolBar(Composite parent) {
		Composite c = new Composite(parent, 0);
		c.setLayoutData(new GridData(1, 1, false, false));
		c.setLayout(new FillLayout(256));

		Button b = new Button(c, 8);
		b.setText("Sélectionner tout");
		b.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent se) {
				CategorieTabItem.this.listCategories.getList().selectAll();
				CategorieTabItem.this
						.toDoOnSelection((StructuredSelection) CategorieTabItem.this.listCategories
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
				CategorieTabItem.this.listCategories.getList().deselectAll();
				CategorieTabItem.this
						.toDoOnSelection((StructuredSelection) CategorieTabItem.this.listCategories
								.getSelection());
			}

			public void widgetDefaultSelected(SelectionEvent se) {
				throw new UnsupportedOperationException("Not supported yet.");
			}
		});
	}

	public void toDoOnSelection(IStructuredSelection selection) {
		java.util.List<SousCategorie> listSousCatDispo = new ArrayList<SousCategorie>();
		for (Object o : selection.toArray()) {
			Categorie cat = (Categorie) o;
			for (SousCategorie sc : cat.getSousCategories()) {
				listSousCatDispo.add(sc);
			}
		}
		SousCategorieTabItem.getInstance().setListSousCategories(
				listSousCatDispo);
	}

	public Collection<Categorie> getListCategories() {
		return this.input;
	}
}
