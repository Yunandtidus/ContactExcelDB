package com.mva.bd;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.widgets.ProgressBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mva.bd.exception.BDException;
import com.mva.model.Categorie;
import com.mva.model.SousCategorie;

public class ExcelFileParser {
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory
			.getLogger(ExcelFileParser.class);
	private File mainFolder;
	private Set<String> attributes;
	private List<Categorie> categories;
	private ProgressBar pb;

	public ExcelFileParser(ProgressBar pb) {
		this.mainFolder = null;
		this.attributes = new HashSet<String>();
		this.categories = new ArrayList<Categorie>();
		this.pb = pb;
	}

	public Collection<String> getAllAtributes() {
		return this.attributes;
	}

	public List<Categorie> getAllCategories() {
		return this.categories;
	}

	public void setMainDirectory(File file) throws BDException {
		if (!file.isDirectory()) {
			throw new BDException(
					"Le dossier contenant les fichiers excels n'est pas accessible");
		}
		this.mainFolder = file;

		File[] excelFiles = this.mainFolder.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith("xls");
			}
		});
		this.pb.setMaximum(excelFiles.length);
		for (File f : excelFiles) {
			this.categories.add(new Categorie(f));
			this.pb.setSelection(this.pb.getSelection() + 1);
		}
		for (Categorie c : this.categories) {
			for (SousCategorie s : c.getSousCategories()) {
				this.attributes.addAll(s.getListAttributs());
			}
		}
	}
}
