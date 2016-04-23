package com.nikos.apostolakis.multimediaLibrary;

import java.io.IOException;


import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * This class is for downloading online content
 * for the movies. The user inserts the name of the 
 * movie and the task is responsible for downloading
 * the content. 
 * 
 * @author nickapostol
 *
 */

public class DownloadTask {
	private String downloadURL;

	public DownloadTask(String url) {
		this.downloadURL = url;
	}

	/**
	 * When execute is called an http request 
	 * is send to the OMDB server which returns the content
	 * we defined. It returns the movie object with the details
	 * of the movie.
	 * 
	 * @return
	 */
	
	public Movie execute() {
		String url = "http://www.omdbapi.com/";
		String charset = java.nio.charset.StandardCharsets.UTF_8.name();

		try {
			String query = String.format("t=%s&plot=full&r=json", URLEncoder.encode(downloadURL, charset));

			URLConnection connection = new URL(url + "?" + query).openConnection();
			connection.setRequestProperty("Accept-Charset", charset);
			InputStream response = connection.getInputStream();

			StringBuilder responseStrBuilder = new StringBuilder();
			byte b[] = new byte[10];

			while (true) {
				int bytesRead = response.read(b);
				if (bytesRead < 0)
					break;
				else
					responseStrBuilder.append(new String(b, "UTF-8"));
			}

			// System.out.println(responseStrBuilder.toString());
			try {
				JSONObject jObject = new JSONObject(responseStrBuilder.toString());

				String offTitle = jObject.getString("Title");
				String year = jObject.getString("Year");
				String genre = jObject.getString("Genre");
				String imdbRating = jObject.getString("imdbRating");
				String plot = jObject.getString("Plot");

				Movie movie = new Movie(offTitle, year, genre, imdbRating, plot);

				return movie;

			} catch (JSONException e) {
				System.out.println(e.getMessage());
			}

		} catch (UnsupportedEncodingException e) {
			System.out.println(e.getMessage());
		} catch (MalformedURLException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return null;

	}

}
