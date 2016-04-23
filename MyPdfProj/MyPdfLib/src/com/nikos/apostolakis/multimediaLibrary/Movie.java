package com.nikos.apostolakis.multimediaLibrary;

/**
 * The Movie class. Fields for : Title,year,Genre,imdbRating,plot.
 * 
 * @author nickapostol
 * 
 */

public class Movie {

	private String Title, Year, Genre, imdbRating, plot;

	public String getTitle() {
		return Title;
	}

	public String getYear() {
		return Year;
	}

	public String getGenre() {
		return Genre;
	}

	public String getImdbRating() {
		return imdbRating;
	}

	public String getPlot() {
		return plot;
	}

	public Movie(String Title, String Year, String Genre, String imdbRating, String plot) {
		this.Title = Title;
		this.Year = Year;
		this.Genre = Genre;
		this.imdbRating = imdbRating;
		this.plot = plot;
	}
}
