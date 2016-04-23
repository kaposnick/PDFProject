package com.nikos.apostolakis.multimediaLibrary;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

public class PDFcreator {

	/**
	 * Creates a new PDF file.
	 * 
	 * @param name
	 *            name of the file
	 * @return result: 1 if the PDF file was created successfully 0 if an error
	 *         has occurred.
	 */
	public int createNewfile(String name) {
		PDDocument doc = new PDDocument();
		try {
			doc.save(name);
			doc.close();
			return 1;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return 0;
		}
	}

	/**
	 * It is responsible for the pdf merging.
	 * 
	 * @param destinationFilename
	 *            the destination file name
	 * @param mergedPdfs
	 *            the array of the to-merged filenames
	 * 
	 * @return 1 if operation successful 0 if an error has occurred.
	 */
	public int concatPDFs(String destinationFilename, ArrayList<String> mergedPdfs) {
		PDFMergerUtility ut = new PDFMergerUtility();
		try {
			for (int i = 0; i < mergedPdfs.size(); i++) {
				ut.addSource(mergedPdfs.get(i));
			}
			ut.setDestinationFileName(destinationFilename);
			ut.mergeDocuments(null);
			return 1;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return 0;
		}
	}

	/**
	 * Gets some of the pages.
	 * 
	 * @param fileName
	 *            the fileName
	 * @param initialPage
	 *            the initialpage of the document
	 * @param finalPage
	 *            the finalpage of the document
	 * @return 0: if an error has occurred. 1: if operation successful 2: if
	 *         user didn't give right input.
	 */
	public int getPages(String fileName, int initialPage, int finalPage) {
		try {
			File file = new File(fileName);
			PDDocument pdfDoc = PDDocument.load(file);
			int sum = pdfDoc.getNumberOfPages();
			if (initialPage > sum || finalPage > sum || initialPage > finalPage || initialPage <= 0 || finalPage <= 0) {
				return 2;
			} else {
				PDDocument newPDF = new PDDocument();
				for (int i = initialPage; i <= finalPage; i++) {
					PDPage page = pdfDoc.getPage(i - 1);
					newPDF.addPage(page);
				}
				newPDF.save(file.getParentFile().getAbsolutePath() + File.separatorChar
						+ file.getName().substring(0, file.getName().indexOf('.')) + '_' + initialPage + "-"
						+ finalPage + ".pdf");
				newPDF.close();
				return 1;
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return 0;
		}
	}

	/**
	 * Returns all the pages
	 * 
	 * @param fileName
	 *            the fileName
	 * @return result 1: if operation successful 2: if operation aborted.
	 */
	public int getAllPages(String fileName) {
		try {
			File pdf = new File(fileName);
			PDDocument currentPdf = PDDocument.load(pdf);
			int numberofPages = currentPdf.getNumberOfPages();
			for (int i = 1; i <= numberofPages; i++) {
				PDPage page = currentPdf.getPage(i - 1);
				PDDocument doc1 = new PDDocument();
				doc1.addPage(page);
				int index = pdf.getName().indexOf('.');
				doc1.save(pdf.getParentFile().getAbsolutePath() + File.separatorChar
						+ pdf.getName().substring(0, index) + "_page_" + i + ".pdf");
				doc1.close();

			}
			return 1;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return 0;
		}
	}

	/**
	 * Seperates the file according to the pagenumber
	 * 
	 * @param fileName
	 *            the filename
	 * @param pageNumber
	 *            the pagenumber of the document to be split
	 * @return result: -1 if user input is false; 1 if operation is successful 0
	 *         if operation aborted.
	 */

	public int seperate(String fileName, int pageNumber) {
		try {
			File file = new File(fileName);
			PDDocument doc = PDDocument.load(file);
			int numberofpages = doc.getNumberOfPages();
			if (pageNumber > numberofpages || pageNumber <= 0) {
				return -1;
			} else {
				PDDocument firstPDF = new PDDocument();
				int i;
				for (i = 0; i < pageNumber; i++) {
					PDPage page = doc.getPage(i);
					firstPDF.addPage(page);
				}
				firstPDF.save(file.getParentFile().getAbsolutePath() + File.separatorChar
						+ file.getName().substring(0, file.getName().indexOf('.')) + "_part1.pdf");
				firstPDF.close();

				if (pageNumber == numberofpages)
					return 1;

				PDDocument secondPDF = new PDDocument();
				for (int j = i; j < numberofpages; j++) {
					PDPage page = doc.getPage(j);
					secondPDF.addPage(page);
				}
				secondPDF.save(file.getParentFile().getAbsolutePath() + File.separatorChar
						+ file.getName().substring(0, file.getName().indexOf('.')) + "_part2.pdf");
				secondPDF.close();
				return 1;
			}

		} catch (IOException e) {
			System.out.println(e.getMessage());
			return 0;
		}

	}

}
