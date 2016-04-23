package com.nikos.apostolakis.development.gui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class MyFileChooser extends JFileChooser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MyFileChooser(FileFilter filter, boolean multipleChoices) {
		super();
		this.setMultiSelectionEnabled(multipleChoices);
		File workingDirectory = new File(System.getProperty("user.dir"));
		this.setCurrentDirectory(workingDirectory);
		this.setFileFilter(filter);
		this.addChoosableFileFilter(filter);
	}

}
