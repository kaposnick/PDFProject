package com.nikos.apostolakis.development.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.nikos.apostolakis.multimediaLibrary.DownloadTask;
import com.nikos.apostolakis.multimediaLibrary.FileSupervisor;
import com.nikos.apostolakis.multimediaLibrary.MlabParser;
import com.nikos.apostolakis.multimediaLibrary.Movie;
import com.nikos.apostolakis.multimediaLibrary.PDFcreator;

public class GuiExecutor extends JFrame implements ActionListener {

	private static JTextArea textArea;
	private static StatusBar statusbar;
	private static JMenuBar menubar;
	private static JToolBar toolbar;
	private static JMenu helpMenu;
	private static JList<Object> fileList;
	private static JScrollPane textScrollPane;

	private final static String elements[] = { "Heading1", "Heading2", "Heading3", "Heading4", "Paragraph", "Format",
			"New Line", "Link", "Image", "Ordered List", "Unordered List" };

	private ArrayList<File> openFiles = new ArrayList<>();;

	private File currentOpenFile;

	private static JMenuItem new_MenuItem, open_MenuItem, save_MenuItem, saveas_MenuItem, close_MenuItem,
			exit_MenuItem;

	private static JMenuItem createpdf_MenuItem, onlinecontent_MenuItem, onlinepdf_MenuItem, mergepdfs_MenuItem,
			extractallpages_MenuItem, extractpages_MenuItem, splitpdf_MenuItem;

	private static JMenuItem info;

	private static final long serialVersionUID = 1L;
	private final static int window_x = 1200;
	private final static int window_y = 800;
	private boolean hasChanged = false;

	public GuiExecutor() {
		initUI();
	}

	private void initUI() {
		createToolBar();
		createMenuBar();

		createStatusBar();
		createFileListArea();
		createTextArea();

		setTitle("Simple example");
		setSize(window_x, window_y);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	private void createTextArea() {
		textArea = new JTextArea();
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textScrollPane = new JScrollPane(textArea);
		textArea.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				if (currentOpenFile != null && !hasChanged) {
					hasChanged = true;
					setTitle(currentOpenFile.getName() + "*");
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				if (currentOpenFile != null && !hasChanged) {
					hasChanged = true;
					setTitle(currentOpenFile.getName() + "*");
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});
		add(textScrollPane, BorderLayout.CENTER);
	}

	private void createFileListArea() {
		openFiles = null;
		fileList = new JList<>();
		fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fileList.setLayoutOrientation(JList.VERTICAL);

		fileList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (currentOpenFile != null && fileList.getSelectedValue() != null) {
					if (!currentOpenFile.equals(fileList.getSelectedValue())) {
						if (hasChanged) {
							int i = JOptionPane.showConfirmDialog(getContentPane(),
									"Do you want to save your changes?", "", JOptionPane.YES_NO_OPTION);
							if (i == 0) {
								save_MenuItem.doClick();
							}
						}
					}
					currentOpenFile = (File) fileList.getSelectedValue();
					textArea.setText(FileSupervisor.openFile(currentOpenFile.getAbsolutePath()));
					setTitle(currentOpenFile.getName());
					hasChanged = false;
				} else if (currentOpenFile == null) {
					if (!openFiles.isEmpty()) {
						currentOpenFile = openFiles.get(0);
						textArea.setText(FileSupervisor.openFile(currentOpenFile.getAbsolutePath()));
						setTitle(currentOpenFile.getName());
						hasChanged = false;
					}
				}
			}
		});

		JScrollPane listScroller = new JScrollPane(fileList);
		listScroller.setPreferredSize(new Dimension(250, 80));
		add(listScroller, BorderLayout.WEST);
	}

	private void createStatusBar() {
		statusbar = new StatusBar();
		statusbar.setBorder(LineBorder.createGrayLineBorder());
		add(statusbar, BorderLayout.SOUTH);
	}

	private void createToolBar() {
		toolbar = new JToolBar();
		for (String element : elements) {
			final JButton button = new JButton(element);
			button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					String buttonText = button.getText();
					switch (buttonText) {
					case "Heading1":
					case "Heading2":
					case "Heading3":
					case "Heading4":
						textArea.insert("&;" + buttonText + " fontType:font_id \n", textArea.getCaretPosition());
						break;
					case "New Line":
						textArea.insert("&;NewLine\n", textArea.getCaretPosition());
						break;
					case "Paragraph":
					case "Format":
					case "Unordered List":
					case "Ordered List":
						textArea.insert("&;" + buttonText.replaceAll("\\s+", "")
								+ " fontSize:num fontType:type_id fontStyle:style_id fontColor:color_id \n",
								textArea.getCaretPosition());
						break;
					case "Image":
						textArea.insert("&;Image scale:value \n", textArea.getCaretPosition());
					default:
						break;

					}
				}
			});
			toolbar.add(button);
		}
		add(toolbar, BorderLayout.NORTH);
	}

	private void createMenuBar() {
		menubar = new JMenuBar();
		Class<? extends GuiExecutor> cl = this.getClass();

		JMenu fileMenu = new JMenu("File");

		String url_new = "/toolbarButtonGraphics/general/New16.gif";
		java.net.URL url = cl.getResource(url_new);
		KeyStroke keystroke_New = KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK);
		new_MenuItem = new JMenuItem("New");
		new_MenuItem.addActionListener(this);
		new_MenuItem.setAccelerator(keystroke_New);
		new_MenuItem.setIcon(new ImageIcon(url));

		String url_open = "/toolbarButtonGraphics/general/Open16.gif";
		url = cl.getResource(url_open);
		KeyStroke keystroke_Open = KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK);
		open_MenuItem = new JMenuItem("Open");
		open_MenuItem.addActionListener(this);
		open_MenuItem.setAccelerator(keystroke_Open);
		open_MenuItem.setIcon(new ImageIcon(url));

		String url_save = "/toolbarButtonGraphics/general/Save16.gif";
		url = cl.getResource(url_save);
		KeyStroke keystroke_Save = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK);
		save_MenuItem = new JMenuItem("Save");
		save_MenuItem.addActionListener(this);
		save_MenuItem.setAccelerator(keystroke_Save);
		save_MenuItem.setIcon(new ImageIcon(url));

		String url_saveas = "/toolbarButtonGraphics/general/SaveAs16.gif";
		url = cl.getResource(url_saveas);
		saveas_MenuItem = new JMenuItem("Save As");
		saveas_MenuItem.addActionListener(this);
		saveas_MenuItem.setIcon(new ImageIcon(url));

		KeyStroke keystroke_Close = KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK);
		close_MenuItem = new JMenuItem("Close");
		close_MenuItem.addActionListener(this);
		close_MenuItem.setAccelerator(keystroke_Close);

		exit_MenuItem = new JMenuItem("Exit");
		exit_MenuItem.addActionListener(this);

		fileMenu.add(new_MenuItem);
		fileMenu.add(open_MenuItem);
		fileMenu.add(save_MenuItem);
		fileMenu.add(saveas_MenuItem);
		fileMenu.add(close_MenuItem);
		fileMenu.addSeparator();
		fileMenu.add(exit_MenuItem);

		JMenu editMenu = new JMenu("Edit");

		createpdf_MenuItem = new JMenuItem("Create PDF");
		createpdf_MenuItem.addActionListener(this);

		onlinecontent_MenuItem = new JMenuItem("Online Content");
		onlinecontent_MenuItem.addActionListener(this);

		onlinepdf_MenuItem = new JMenuItem("Online PDF");
		onlinepdf_MenuItem.addActionListener(this);

		mergepdfs_MenuItem = new JMenuItem("Merge PDFs");
		mergepdfs_MenuItem.addActionListener(this);

		extractallpages_MenuItem = new JMenuItem("Extract All Pages");
		extractallpages_MenuItem.addActionListener(this);

		extractpages_MenuItem = new JMenuItem("Extract Pages");
		extractpages_MenuItem.addActionListener(this);

		splitpdf_MenuItem = new JMenuItem("Split PDF");
		splitpdf_MenuItem.addActionListener(this);

		editMenu.add(createpdf_MenuItem);
		editMenu.add(onlinecontent_MenuItem);
		editMenu.add(onlinepdf_MenuItem);
		editMenu.add(mergepdfs_MenuItem);
		editMenu.add(extractallpages_MenuItem);
		editMenu.add(extractpages_MenuItem);
		editMenu.add(splitpdf_MenuItem);

		helpMenu = new JMenu("Help");

		String url_info = "/toolbarButtonGraphics/general/About16.gif";
		url = cl.getResource(url_info);
		KeyStroke keystroke_Info = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0);
		info = new JMenuItem("More info");
		info.addActionListener(this);
		info.setAccelerator(keystroke_Info);
		info.setIcon(new ImageIcon(url));

		helpMenu.add(info);

		menubar.add(fileMenu);
		menubar.add(editMenu);
		menubar.add(helpMenu);

		setJMenuBar(menubar);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(new_MenuItem)) {
			JFrame parent = new JFrame();
			String filename = JOptionPane.showInputDialog(parent, "Create new Mlab File", null);
			if (filename != null) {
				if (!filename.equals("")) {
					currentOpenFile = FileSupervisor.createNewFile(filename);
					if (currentOpenFile != null) {
						setTitle(currentOpenFile.getName());
						if (openFiles == null) {
							openFiles = new ArrayList<>();
						}
						if (!openFiles.contains(currentOpenFile.getAbsolutePath())) {
							openFiles.add(currentOpenFile);
						}
						notifyDataSetChanged();
						hasChanged = false;
						textArea.setText("");
					}
				}
			}
		} else if (e.getSource().equals(open_MenuItem)) {
			MyFileChooser mlabChooser = new MyFileChooser(new FileNameExtensionFilter("Mlab Files", "mlab"), true);
			int returnVal = mlabChooser.showOpenDialog(getContentPane());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File files[] = mlabChooser.getSelectedFiles();
				if (openFiles == null) {
					openFiles = new ArrayList<>();
				}
				for (File file : files) {
					if (!openFiles.contains(file)) {
						openFiles.add(file);
					}
				}

				notifyDataSetChanged();
				currentOpenFile = new File(files[files.length - 1].getAbsolutePath());
				String fileText = FileSupervisor.openFile(currentOpenFile.getAbsolutePath());
				statusbar.setMessage("File " + files[files.length - 1].getName() + " opened.");
				textArea.setText(fileText);
				hasChanged = false;
				setTitle(currentOpenFile.getName());
			} else {
				System.out.println("Open command cancelled by user.");
			}
		} else if (e.getSource().equals(save_MenuItem)) {
			if (currentOpenFile != null) {
				FileSupervisor.saveFile(textArea.getText(), currentOpenFile.getAbsolutePath());
				setNewStatus();
			} else {
				JOptionPane.showMessageDialog(this, "No open File");
			}
		} else if (e.getSource().equals(saveas_MenuItem)) {
			if (currentOpenFile != null) {
				JFrame parent = new JFrame();
				String filename = JOptionPane.showInputDialog(parent, "Save as", null);
				currentOpenFile = FileSupervisor.saveAs(textArea.getText(), filename);
				setTitle(currentOpenFile.getName());
				openFiles.add(0, currentOpenFile);
				notifyDataSetChanged();
				setNewStatus();
			} else {
				JOptionPane.showMessageDialog(this, "No open File");
			}
		} else if (e.getSource().equals(close_MenuItem)) {
			if (currentOpenFile != null) {
				checkForWritten();
				textArea.setText("");
				statusbar.setMessage("File " + currentOpenFile.getName() + " closed.");
				openFiles.remove(currentOpenFile);
				currentOpenFile = null;
				notifyDataSetChanged();
				if (openFiles.isEmpty()) {
					setTitle("No opened file");
				}
			}
		} else if (e.getSource().equals(exit_MenuItem)) {
			int i = JOptionPane.showConfirmDialog(getContentPane(), "Are you sure you want to exit?");
			if (i == 0)
				System.exit(0);
		} else if (e.getSource().equals(createpdf_MenuItem)) {
			if (currentOpenFile != null) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						MlabParser parser = new MlabParser();
						int index = currentOpenFile.getAbsolutePath().indexOf('.');
						parser.paint(parser.parse(currentOpenFile.getAbsolutePath()), currentOpenFile.getAbsolutePath()
								.substring(0, index) + ".pdf");
					}
				}).start();
				JOptionPane.showMessageDialog(this, "PDF created.");
			} else {
				JOptionPane.showMessageDialog(this, "No open File.");
			}
		} else if (e.getSource().equals(onlinecontent_MenuItem)) {
			JFrame parent = new JFrame();
			String movieName = JOptionPane.showInputDialog(parent, "Give the movie title", null);
			if (movieName != null) {
				DownloadTask task = new DownloadTask(movieName);
				Movie movie = task.execute();
				StringBuilder pdfMovieString = new StringBuilder();
				pdfMovieString.append("&;Heading1 fontType:1\n");
				pdfMovieString.append("Title: " + movie.getTitle() + "\n");
				pdfMovieString.append("&;Heading3 fontType:1\n");
				pdfMovieString.append("Year: " + movie.getYear() + "\n");
				pdfMovieString.append("&;Heading3 fontType:1\n");
				pdfMovieString.append("Genre: " + movie.getGenre() + "\n");
				pdfMovieString.append("&;Heading3 fontType:1\n");
				pdfMovieString.append("ImdbRating: " + movie.getImdbRating() + "\n");
				pdfMovieString.append("&;Paragraph fontSize:12 fontType:1 fontStyle:1 fontColor:1\n");
				pdfMovieString.append("Plot: " + movie.getPlot() + "\n");
				textArea.setText(pdfMovieString.toString());
			}
		} else if (e.getSource().equals(onlinepdf_MenuItem)) {
			JFrame parent = new JFrame();
			final String movieName = JOptionPane.showInputDialog(parent, "Give the movie title", null);
			if (movieName != null) {
				final Movie movie;
				DownloadTask task = new DownloadTask(movieName);
				movie = task.execute();

				StringBuilder pdfMovieString = new StringBuilder();
				pdfMovieString.append("&;Heading1 fontType:1\n");
				pdfMovieString.append("Title: " + movie.getTitle() + "\n");
				pdfMovieString.append("&;Heading3 fontType:1\n");
				pdfMovieString.append("Year: " + movie.getYear() + "\n");
				pdfMovieString.append("&;Heading3 fontType:1\n");
				pdfMovieString.append("Genre: " + movie.getGenre() + "\n");
				pdfMovieString.append("&;Heading3 fontType:1\n");
				pdfMovieString.append("ImdbRating: " + movie.getImdbRating() + "\n");
				pdfMovieString.append("&;Paragraph fontSize:12 fontType:1 fontStyle:1 fontColor:1\n");
				pdfMovieString.append("Plot: " + movie.getPlot() + "\n");

				String movieString = pdfMovieString.toString();
				File movieFile = FileSupervisor.createNewFile(movie.getTitle());
				try {
					PrintWriter out = new PrintWriter(movieFile);
					out.println(movieString);
					out.close();

					MlabParser parser = new MlabParser();
					int index = movieFile.getAbsolutePath().indexOf('.');
					parser.paint(parser.parse(movieFile.getAbsolutePath()),
							movieFile.getAbsolutePath().substring(0, index) + ".pdf");
					JOptionPane.showMessageDialog(this, movie.getTitle() + ".pdf created.", "Movie to PDF",
							JOptionPane.INFORMATION_MESSAGE);
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}

		} else if (e.getSource().equals(mergepdfs_MenuItem)) {
			MyFileChooser pdfChooser = new MyFileChooser(new FileNameExtensionFilter("PDF Files", "pdf"), true);
			int returnVal = pdfChooser.showOpenDialog(getContentPane());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File files[] = pdfChooser.getSelectedFiles();
				ArrayList<String> fileNames = new ArrayList<>();
				for (File file : files) {
					fileNames.add(file.getName());
				}
				JFrame parent = new JFrame();
				String filename = JOptionPane.showInputDialog(parent, "Give a filename for the merged pdf", null);
				if (filename != null && filename != "") {
					PDFcreator pdfCreator = new PDFcreator();
					pdfCreator.concatPDFs(files[0].getParentFile().getAbsolutePath() + File.separatorChar + filename
							+ ".pdf", fileNames);
					JOptionPane.showMessageDialog(this, filename + ".pdf created.", "Merge PDFs",
							JOptionPane.INFORMATION_MESSAGE);
				}
			} else {
				System.out.println("Open command cancelled by user.");
			}

		} else if (e.getSource().equals(extractallpages_MenuItem)) {
			MyFileChooser pdfChooser = new MyFileChooser(new FileNameExtensionFilter("PDF Files", "pdf"), false);
			int returnVal = pdfChooser.showOpenDialog(getContentPane());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = pdfChooser.getSelectedFile();
				PDFcreator pdFcreator = new PDFcreator();
				pdFcreator.getAllPages(file.getAbsolutePath());
				JOptionPane.showMessageDialog(this, "Extracting all pages from " + file.getAbsolutePath()
						+ " successful.", "Extract all pages", JOptionPane.INFORMATION_MESSAGE);
			}

		} else if (e.getSource().equals(extractpages_MenuItem)) {
			MyFileChooser pdfChooser = new MyFileChooser(new FileNameExtensionFilter("PDF Files", "pdf"), false);
			int returnVal = pdfChooser.showOpenDialog(getContentPane());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = pdfChooser.getSelectedFile();
				JFrame parent = new JFrame();
				while (true) {
					String selectedPages = JOptionPane.showInputDialog(parent,
							"Selected pages to extract\n <StartPage>-<EndPage>", null);
					if (selectedPages != null) {
						if (selectedPages.indexOf('-') > 0) {
							String values[] = selectedPages.split("-");
							int startingPage = Integer.parseInt(values[0]);
							int endPage = Integer.parseInt(values[1]);
							PDFcreator pdfCreator = new PDFcreator();
							int result = pdfCreator.getPages(file.getAbsolutePath(), startingPage, endPage);
							if (result == 0 || result == 2) {
								JOptionPane.showMessageDialog(this, "Insert pages in valid format");
							} else {
								JOptionPane.showMessageDialog(this, "Everything went fine! Pages extracted.",
										"Extracting specifid pages", JOptionPane.INFORMATION_MESSAGE);
								break;
							}
						} else {
							JOptionPane.showMessageDialog(this, "Non valid Input",
									"Input must contain valid page numbers seperated by -",
									JOptionPane.INFORMATION_MESSAGE);
						}
					} else {
						break;
					}
				}
			}

		} else if (e.getSource().equals(splitpdf_MenuItem)) {
			MyFileChooser splitChooser = new MyFileChooser(new FileNameExtensionFilter("PDF Files", "pdf"), false);
			int returnVal = splitChooser.showOpenDialog(getContentPane());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = splitChooser.getSelectedFile();
				JFrame parent = new JFrame();
				while (true) {
					String selectedPage = JOptionPane.showInputDialog(parent, "Select page to split", null);
					if (selectedPage != null) {
						int page = Integer.parseInt(selectedPage);
						PDFcreator pdFcreator = new PDFcreator();
						int result = pdFcreator.seperate(file.getAbsolutePath(), page);
						if (result != 1) {
							JOptionPane
									.showMessageDialog(this, "Error spliting file. Please insert valid page number.");
						} else {
							JOptionPane.showMessageDialog(this, "Everything went fine! Pages extracted.",
									"Splitting pdf", JOptionPane.INFORMATION_MESSAGE);
							break;
						}
					}
				}
			}

		} else if (e.getSource().equals(info)) {
			String DeveloperInfo = "This project was developed by Nikos Apostolakis.\n"
					+ "He is a java developer with experience in Android Development, \n"
					+ "front-end and back-end development. If you want any android-project or website\n"
					+ "to be materialized do not hesitate to call him at : +306946308450.";
			JOptionPane.showMessageDialog(this, DeveloperInfo, "About the Developer", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void checkForWritten() {
		if (hasChanged) {
			int i = JOptionPane.showConfirmDialog(getContentPane(),
					"It seems like you haven't saved your current changes.Do you want to save them before closing?",
					"Close File", JOptionPane.YES_NO_OPTION);
			if (i == 0) {
				save_MenuItem.doClick();
			}
		}

	}

	private void setNewStatus() {
		double bytes = currentOpenFile.length();
		String sizeString = null;
		if (bytes < 1024) {
			sizeString = (int) bytes + " bytes.";
		} else if (bytes < Math.pow(1024, 2)) {
			sizeString = String.format("%.1f Kb", bytes / 1024);
		} else if (bytes < Math.pow(1024, 3)) {
			sizeString = String.format("%.1f Mb", (bytes / 1024) / 1024);
		}
		hasChanged = false;
		setTitle(currentOpenFile.getName());
		statusbar.setMessage("File " + currentOpenFile.getName() + " saved. Size: " + sizeString);
	}

	private void notifyDataSetChanged() {
		FileNames[] files = new FileNames[openFiles.size()];
		for (int i = 0; i < openFiles.size(); i++) {
			files[i] = new FileNames(openFiles.get(i).getAbsolutePath());
		}
		fileList.setListData(files);
		textScrollPane.revalidate();
		textScrollPane.repaint();
	}

	private class FileNames extends File {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public FileNames(String path) {
			super(path);
		}

		@Override
		public String toString() {
			return this.getName();
		}
	}

	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				GuiExecutor ex = new GuiExecutor();
				ex.setVisible(true);
			}
		});

	}

}
