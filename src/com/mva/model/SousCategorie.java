package com.mva.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mva.bd.exception.BDException;

public final class SousCategorie {
	private static final Logger logger = LoggerFactory
			.getLogger(SousCategorie.class);
	private String nomSousCategorieExact;
	private String nomSousCategorieAffichable;
	private List<String> attributs;
	private File excelFile;
	private Workbook workbook;
	private List<Contact> listContacts;

	public SousCategorie(String nom, File excelFile) throws BDException {
		this.nomSousCategorieExact = nom;
		this.nomSousCategorieAffichable = nom.replace("$", "");

		this.attributs = new ArrayList<String>();

		this.excelFile = excelFile;
		this.listContacts = new ArrayList<Contact>();

		compute();
	}

	public void compute() throws BDException {
		try {
			this.workbook = Workbook.getWorkbook(this.excelFile);
			this.attributs = new ArrayList<String>();
			this.listContacts = new ArrayList<Contact>();

			Sheet sheet = this.workbook.getSheet(this.nomSousCategorieExact);
			for (int c = 0; c < sheet.getColumns(); c++) {
				Cell cell = sheet.getCell(c, 0);
				if (cell.getType().equals(CellType.LABEL)) {
					this.attributs.add(cell.getContents());
				}
				if ((cell.getType().equals(CellType.EMPTY))
						&& (c != sheet.getColumns() - 1)) {
					this.attributs.add(cell.getContents());
				}
			}
			for (int r = 1; r < sheet.getRows(); r++) {
				boolean addContact = false;
				Map<String, Object> map = new HashMap<String, Object>();
				for (int c = 0; c < this.attributs.size(); c++) {
					Cell cell = sheet.getCell(c, r);
					CellType type = cell.getType();
					if (type != CellType.EMPTY) {
						addContact = true;
					}
					if (type == CellType.LABEL) {
						map.put(this.attributs.get(c), cell.getContents());
					} else if (type == CellType.NUMBER) {
						String s = cell.getContents().replaceAll(" ", "");

						map.put(this.attributs.get(c), s);
					} else if (type == CellType.DATE) {
						Object o = null;
						try {
							o = new Date(cell.getContents());
						} catch (Exception e) {
							o = cell.getContents();
						}
						map.put(this.attributs.get(c), o);
					} else if (type == CellType.EMPTY) {
						map.put(this.attributs.get(c), cell.getContents());
					} else if (type == CellType.NUMBER_FORMULA) {
						map.put(this.attributs.get(c), cell.getContents());
					} else {
						throw new BDException("La cellule ["
								+ (char) ('A' + c)
								// r + 1 car les numéros de lignes commencent à
								// 1 dans Excel
								+ (r + 1) + "] du fichier ("
								+ excelFile.getName() + ", onglet = "
								+ nomSousCategorieAffichable
								+ ") contenant la valeur ("
								+ cell.getContents() + ") est de type (" + type
								+ ") et n'est pas supporté par l'application");
					}
				}
				if (addContact) {
					Contact contact = new Contact();
					contact.setAttributes(map);
					this.listContacts.add(contact);
				}
			}
		} catch (Exception e) {
			logger.error(this.nomSousCategorieExact, e);
			throw new BDException(e);
		} finally {
			this.workbook.close();
			this.workbook = null;
		}
	}

	public String getNomSousCategorieAffichable() {
		return this.nomSousCategorieAffichable;
	}

	public List<String> getListAttributs() {
		return this.attributs;
	}

	public List<Contact> getListContacts() {
		return this.listContacts;
	}
}
