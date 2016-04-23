package com.nikos.apostolakis.multimediaLibrary;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

/**
 * This is the main class of the project. Mlabparser Class is the class which
 * manipulates all the staff. Initially, mlab parser creates an {@link HashMap}
 * of all the elements of the .mlab file and returns it to the calling class.
 * Afterwards, paint Method paints the given content to a new pdf file.
 * 
 * @author nickapostol
 * 
 */

public class MlabParser {

	/**
	 * Configuration values for the generated pdf file.
	 */
	private final static float pagePadding_x = (float) 30.0;
	private final static float pagePadding_y = (float) 50.0;
	private final static float paragraphLeading = (float) 25.0;
	private final static float lineLeading = (float) 20.0;
	private final static float paragraphWidth = new PDPage(PDRectangle.A4).getMediaBox().getWidth() - 2 * pagePadding_x;

	/**
	 * These are the fonts and the colors the app is gonna use.
	 */
	private PDFont fonts[][] = {
			{ PDType1Font.TIMES_ROMAN, PDType1Font.TIMES_BOLD, PDType1Font.TIMES_ITALIC, PDType1Font.TIMES_BOLD_ITALIC },
			{ PDType1Font.HELVETICA, PDType1Font.HELVETICA_BOLD, PDType1Font.HELVETICA_OBLIQUE,
					PDType1Font.HELVETICA_BOLD_OBLIQUE },
			{ PDType1Font.COURIER, PDType1Font.COURIER_BOLD, PDType1Font.COURIER_OBLIQUE,
					PDType1Font.COURIER_BOLD_OBLIQUE } };

	static final Color colors[] = { Color.black, Color.blue, Color.red, Color.yellow };

	/**
	 * Some final Strings that the mlab parser needs as @parse method inserts
	 * values to the {@link HashMap}.
	 */
	private final static String commandTitle = "command";
	private final static String content = "content";

	private final static String fontType = "fontType";
	private final static String fontSize = "fontSize";
	private final static String fontStyle = "fontStyle";
	private final static String fontColor = "fontColor";
	private final static String scale = "scale";

	private final static String Heading1 = "Heading1";
	private final static String Heading2 = "Heading2";
	private final static String Heading3 = "Heading3";
	private final static String Heading4 = "Heading4";
	private final static String Image = "Image";
	private final static String Paragraph = "Paragraph";
	private final static String Format = "Format";
	private final static String UnorderedList = "UnorderedList";
	private final static String OrderedList = "OrderedList";

	/**
	 * Some fields of the {@link MlabParser} class.
	 */
	private PDFont pdFont;
	private PDDocument document;
	private PDPage currentPage;
	private PDPageContentStream currentPageContentStream;

	public ArrayList<HashMap<String, String>> parse(String fileName) {
		File file = new File(fileName);
		if (!file.exists()) {
			System.out.println("File " + file.getName() + " doesn't exist.");
			return null;
		}
		String fileContent = FileSupervisor.openFile(fileName);
		fileContent = fileContent.replaceAll("\\s", " ");
		StringTokenizer tokens = new StringTokenizer(fileContent);
		ArrayList<HashMap<String, String>> allElements = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> command = null;
		int attributesSum = 0;
		while (tokens.hasMoreTokens()) {
			String token = tokens.nextToken();
			if (token.charAt(0) == '&' && token.charAt(1) == ';') {
				if (command != null) {
					allElements.add(command);
				}
				command = new HashMap<String, String>();
				String title = token.substring(2);
				command.put(commandTitle, title);
				String helper = title.substring(0, title.length() - 1);
				if (helper.equals("Heading") || helper.equals("Imag")) {
					attributesSum = 1;
				} else if (helper.equals("NewLin")) {
					attributesSum = 0;
				} else {
					attributesSum = 4;
				}
			} else {
				if (attributesSum > 0) {
					String attribute = token.substring(0, token.indexOf(':'));
					command.put(attribute, token.substring(token.indexOf(':') + 1));
					attributesSum--;
				} else {
					String alreadyWritten = command.get(content);
					if (alreadyWritten == null) {
						command.put(content, token);
					} else {
						StringBuilder builder = new StringBuilder(command.get(content));
						builder.append(" " + token);
						command.put(content, builder.toString());
					}
				}
			}
		}
		if (command != null) {
			allElements.add(command);
		}
		return allElements;
	}

	public int paint(ArrayList<HashMap<String, String>> dom, String fileName) {
		try {
			document = new PDDocument();
			currentPage = new PDPage(PDRectangle.A4);
			document.addPage(currentPage);
			currentPageContentStream = new PDPageContentStream(document, currentPage);
			float offset_x = pagePadding_x;
			float offset_y = (currentPage.getMediaBox().getHeight() - pagePadding_y);
			for (HashMap<String, String> element : dom) {
				String commandTitle = element.get(MlabParser.commandTitle);
				int fontSize = 1, fontType = 1, fontStyle = 1, fontColor = 1;
				float scale = 1;
				if (commandTitle.equals(Heading1) || commandTitle.equals(Heading2) || commandTitle.equals(Heading3)
						|| commandTitle.equals(Heading4)) {
					fontType = Integer.parseInt(element.get(MlabParser.fontType));
					fontStyle = 2;
					fontSize = 22 - 2 * Character.getNumericValue((commandTitle.charAt(commandTitle.length() - 1)));
					fontColor = 1;
				} else if (commandTitle.equals(Paragraph) || commandTitle.equals(Format)
						|| commandTitle.equals(UnorderedList) || commandTitle.equals(OrderedList)) {
					fontType = Integer.parseInt(element.get(MlabParser.fontType));
					fontStyle = Integer.parseInt(element.get(MlabParser.fontStyle));
					fontSize = Integer.parseInt(element.get(MlabParser.fontSize));
					fontColor = Integer.parseInt(element.get(MlabParser.fontColor));
				} else if (commandTitle.equals(Image)) {
					scale = Float.parseFloat(element.get(MlabParser.scale));
				}
				/* change page if beyond the margins */
				offset_y = checkPage(offset_y);

				/* paint the content inside the page */
				String content = element.get(MlabParser.content);
				//System.out.println(content);
				pdFont = fonts[fontType - 1][fontStyle - 1];
				Color color = colors[fontColor - 1];
				String helperTitle = commandTitle.substring(0, commandTitle.length() - 1);

				if (helperTitle.equals("Heading")) {
					/* IF IT IS A HEADING! */
					offset_x = MlabParser.pagePadding_x;
					int start = 0;
					int end = 0;
					for (int i : possibleWrapPoints(content)) {
						float width = pdFont.getStringWidth(content.substring(start, i)) / 1000 * fontSize;
						if (start < end && width > paragraphWidth) {
							offset_y = checkPage(offset_y);
							paintLine(currentPageContentStream, content, start, end, MlabParser.pagePadding_x,
									offset_y, fontSize, color);
							offset_y -= MlabParser.paragraphLeading;
							start = end;
						}
						end = i;
					}

					paintLine(currentPageContentStream, content, start, content.length(), MlabParser.pagePadding_x,
							offset_y, fontSize, color);
					offset_y -= MlabParser.paragraphLeading;
				} else if (helperTitle.equals("Paragrap") || helperTitle.equals("Forma")) {
					/* IF IT IS A PARAGRAPH OR A FORMAT */
					float additional_offsetX = 0;
					if (helperTitle.equals("Forma")) {
						offset_y += MlabParser.paragraphLeading;
					} else {
						offset_x = MlabParser.pagePadding_x;
					}
					if (offset_x > MlabParser.pagePadding_x) {
						additional_offsetX = offset_x - MlabParser.pagePadding_x;
					}
					int start = 0;
					int end = 0;
					for (int i : possibleWrapPoints(content)) {
						float width = additional_offsetX + pdFont.getStringWidth(content.substring(start, i)) / 1000
								* fontSize;
						if (start < end && width > paragraphWidth) {
							paintLine(currentPageContentStream, content, start, end, offset_x, offset_y, fontSize,
									color);
							offset_y -= MlabParser.lineLeading;
							offset_y = checkPage(offset_y);
							start = end;
							offset_x = MlabParser.pagePadding_x;
							additional_offsetX = 0;
						}
						end = i;
					}
					paintLine(currentPageContentStream, content, start, content.length(), MlabParser.pagePadding_x,
							offset_y, fontSize, color);
					offset_y -= MlabParser.paragraphLeading;
					offset_x = MlabParser.pagePadding_x
							+ pdFont.getStringWidth(content.substring(start, content.length())) / 1000 * fontSize;
				} else if (helperTitle.endsWith("Lis")) {
					/* IF IT IS A LIST */
					int indexSelector = 0;
					String indexSelectorString;
					if (helperTitle.equals("UnorderedLis")) {
						indexSelectorString = "-";
					} else {
						indexSelectorString = indexSelector + ".";
					}
					StringTokenizer tokens = new StringTokenizer(content);
					while (tokens.hasMoreTokens()) {
						String textToShow = indexSelectorString + tokens.nextToken();
						paintLine(currentPageContentStream, textToShow, 0, textToShow.length(),
								MlabParser.pagePadding_x, offset_y, fontSize, color);
						offset_y -= MlabParser.lineLeading;
						offset_y = checkPage(offset_y);
						if (helperTitle.equals("OrderedLis")) {
							indexSelector++;
							indexSelectorString = indexSelector + ".";
						}
					}
				} else if (helperTitle.equals("Imag")) {
					offset_x = MlabParser.pagePadding_x;
					PDImageXObject imageObject = PDImageXObject.createFromFileByContent(new File(content), document);
					if (offset_y - imageObject.getHeight() * scale < MlabParser.pagePadding_y) {
						currentPageContentStream.close();
						currentPage = new PDPage();
						currentPageContentStream = new PDPageContentStream(document, currentPage);
						document.addPage(currentPage);
						offset_y = (int) (currentPage.getMediaBox().getHeight() - MlabParser.pagePadding_y - imageObject
								.getHeight() * scale);
					} else
						offset_y -= imageObject.getHeight() * scale;

					currentPageContentStream.drawImage(imageObject, offset_x, offset_y, imageObject.getWidth() * scale,
							imageObject.getHeight() * scale);
					offset_y -= paragraphLeading;
				} else if (helperTitle.equals("NewLin")) {
					offset_y -= PDType1Font.TIMES_ROMAN.getBoundingBox().getWidth() / 1000 * 12;
					offset_y = checkPage(offset_y - paragraphLeading);
				}
			}
			currentPageContentStream.close();
			document.save(fileName);
			document.close();
			return 1;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return -1;
		}

	}

	/**
	 * This function is responsible for painting the line into the pdf file.
	 * 
	 * @param currentPageContentStream
	 *            The current pagecontent stream
	 * @param content
	 *            the String to paint
	 * @param start
	 *            the index of the starting char
	 * @param end
	 *            the index of the ending char
	 * @param pagepaddingX
	 *            the x-padding to paint
	 * @param offset_y
	 *            the offset from the bottom of the page
	 * @param fontSize2
	 *            the fontsize of the printed chars
	 * @param color
	 *            the color
	 */

	@SuppressWarnings("deprecation")
	private void paintLine(PDPageContentStream currentPageContentStream, String content, int start, int end,
			float pagepaddingX, float offset_y, int fontSize2, Color color) {
		try {
			currentPageContentStream.beginText();
			currentPageContentStream.setFont(pdFont, fontSize2);
			currentPageContentStream.setNonStrokingColor(color);
			currentPageContentStream.moveTextPositionByAmount(pagepaddingX, offset_y);
			currentPageContentStream.drawString(content.substring(start, end));
			currentPageContentStream.endText();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

	}

	/**
	 * It creates a new page and a new contentstream if the content that is
	 * going to be painted overpasses the y-offset
	 * 
	 * @param offset_y
	 *            the current offset of the page
	 * @return returns the new offset
	 */

	private float checkPage(float offset_y) {
		if (offset_y < pagePadding_y) {
			try {
				currentPageContentStream.close();
				currentPage = new PDPage(PDRectangle.A4);
				currentPageContentStream = new PDPageContentStream(document, currentPage);
				document.addPage(currentPage);
				offset_y = (currentPage.getMediaBox().getHeight() - MlabParser.pagePadding_y);
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}

		}
		return offset_y;
	}

	/**
	 * Used to wrap a text into the page.
	 * 
	 * @param text
	 *            the text to wrap
	 * @return the Points that is going to be wrapped.
	 */
	protected static int[] possibleWrapPoints(String text) {
		String[] split = text.split("(?<=\\W)");
		int[] ret = new int[split.length];
		ret[0] = split[0].length();
		for (int i = 1; i < split.length; i++)
			ret[i] = ret[i - 1] + split[i].length();
		return ret;
	}
}
