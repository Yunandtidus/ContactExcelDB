package com.mva.gui;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mva.bd.ExcelFileParser;

public class Application {
	public static final Logger logger = LoggerFactory
			.getLogger(Application.class);

	public static void main(String[] args) throws Throwable {
		try {
			Display display = new Display();
			int[] count = { 4 };
			Image image = new Image(display, 300, 300);
			GC gc = new GC(image);
			gc.setBackground(new Color(display, 255, 224, 224));
			gc.fillRectangle(image.getBounds());
			gc.drawText(
					"Veuillez patienter pendant le chargement des fichiers",
					10, 10);
			gc.dispose();
			Shell splash = new Shell(16384);
			ProgressBar bar = new ProgressBar(splash, 0);

			bar.setMaximum(count[0]);
			Label label = new Label(splash, 0);
			label.setImage(image);
			FormLayout layout = new FormLayout();
			splash.setLayout(layout);
			FormData labelData = new FormData();
			labelData.right = new FormAttachment(100, 0);
			labelData.bottom = new FormAttachment(100, 0);
			label.setLayoutData(labelData);
			FormData progressData = new FormData();
			progressData.left = new FormAttachment(0, 5);
			progressData.right = new FormAttachment(100, -5);
			progressData.bottom = new FormAttachment(100, -5);
			bar.setLayoutData(progressData);
			splash.pack();
			Rectangle splashRect = splash.getBounds();
			Rectangle displayRect = display.getBounds();
			int x = (displayRect.width - splashRect.width) / 2;
			int y = (displayRect.height - splashRect.height) / 2;
			splash.setLocation(x, y);
			splash.open();

			ExcelFileParser efp = new ExcelFileParser(bar);

			Properties prop = new Properties();
			prop.load(new FileInputStream("resources/bd.properties"));
			efp.setMainDirectory(new File(prop.getProperty("location")));

			splash.close();
			image.dispose();
			display.dispose();

			@SuppressWarnings("unused")
			MainWindow mw = new MainWindow(efp);
		} catch (Exception e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(),
					"Erreur", e.getMessage());
			logger.error("Erreur", e);
		}
	}
}
