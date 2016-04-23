package com.nikos.apostolakis.multimediaLibrary;

import java.util.ArrayList;
import java.util.HashMap;

public class MainClass {

	public static void main(String[] args) {
		MlabParser parser = new MlabParser();
		ArrayList<HashMap<String, String>> dom = parser.parse("example.mlab");
		parser.paint(dom, "example.pdf");
		DownloadTask task = new DownloadTask("American Sniper");
		Movie movie = null;
		movie = task.execute();
		if (movie != null) {
			System.out.println(movie.getTitle());
			System.out.println(movie.getYear());
			System.out.println(movie.getGenre());
			System.out.println(movie.getImdbRating());
			System.out.println(movie.getPlot());
		}
	}

}
