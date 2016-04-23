package com.nikos.apostolakis.multimediaLibrary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileSupervisor {

	/**
	 * Creates a new Mlab File
	 * 
	 * @param fileName
	 *            the filename to be created.
	 * @return returns the file or NULL if error.
	 */

	public static File createNewFile(String fileName) {
		File newFile = new File(fileName + ".mlab");
		if (newFile.exists()) {
			return newFile;
		} else {
			try {
				newFile.createNewFile();
				return newFile;
			} catch (IOException e) {
				System.out.println(e.getMessage());
				return null;
			}

		}
	}

	/**
	 * Reads the content in bytes of a file
	 * 
	 * @param fileName
	 *            the name of the file to be opened.
	 * @return the output String
	 */

	public static String openFile(String fileName) {
		File file = new File(fileName);
		FileInputStream fileStream = null;
		try {
			fileStream = new FileInputStream(file);
			byte fileContent[] = new byte[(int) file.length()];
			fileStream.read(fileContent);
			fileStream.close();
			return new String(fileContent);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Saves the given text to the given filename
	 * 
	 * @param text
	 *            the text to be saved to the file.
	 * @param fileName
	 *            the filename save
	 * @return 1: if operation successful 0: if error occurred -1: if
	 *         fileNotfoundException has occurred.
	 */
	public static int saveFile(String text, String fileName) {
		File file = new File(fileName);
		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream(file);
			byte fileContent[] = new byte[text.length()];
			fileContent = text.getBytes();
			fout.write(fileContent);
			fout.close();
			return 1;
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			return -1;
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return 0;
	}

	public static File saveAs(String text, String fileName) {
		File file = FileSupervisor.createNewFile(fileName);
		if (file != null) {
			FileSupervisor.saveFile(text, file.getAbsolutePath());
			return file;
		} else {
			System.out.println("File already exists");
		}
		return null;
	}

}
