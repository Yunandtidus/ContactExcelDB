package com.mva.model;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import jxl.Workbook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mva.bd.exception.BDException;

public final class Categorie {
	private static final Logger logger = LoggerFactory
			.getLogger(Categorie.class);
	protected File excelFile;
	protected String exactCategorieName;
	protected String printableCategorieName;
	protected List<SousCategorie> sousCategories;
	protected Connection conn;
	protected long lastModification;

	public Categorie(File file) throws BDException {
		this.excelFile = file;
		this.lastModification = file.lastModified();
		this.sousCategories = new ArrayList<SousCategorie>();
		createCategorie();
	}

	public File getExcelFile() {
		return this.excelFile;
	}

	public void setExcelFile(File excelFile) {
		this.excelFile = excelFile;
	}

	public List<SousCategorie> getSousCategories() {
		return this.sousCategories;
	}

	public void setSousCategories(List<SousCategorie> sousCategories) {
		this.sousCategories = sousCategories;
	}

	private void createCategorie() throws BDException {
		this.exactCategorieName = this.excelFile.getName().replace(".xls", "");
		this.printableCategorieName = this.exactCategorieName;
		logger.info("Ajout de la catégorie : " + this.exactCategorieName);
		listerSousCategories();
	}

	private void listerSousCategories() throws BDException {
		try {
			Workbook w = Workbook.getWorkbook(this.excelFile);
			for (String tableName : w.getSheetNames()) {
				if ((!tableName.matches("'.*\\$'.+"))
						&& (!tableName.matches("[^']*\\$.+"))
						&& (!tableName.matches("Feuil.*"))) {
					SousCategorie sousCat = new SousCategorie(tableName,
							this.excelFile);
					this.sousCategories.add(sousCat);
					logger.debug("    - ajout de la sous-catégorie : "
							+ sousCat.getNomSousCategorieAffichable());
				}
			}
			w.close();
		} catch (Exception e) {
			throw new BDException(e);
		}
	}

	public String getPrintableCategorieName() {
		return this.printableCategorieName;
	}

	public void setLastModification(long lastModification) {
		this.lastModification = lastModification;
	}

	public long getLastModification() {
		return this.lastModification;
	}
}
