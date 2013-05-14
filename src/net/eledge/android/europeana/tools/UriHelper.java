package net.eledge.android.europeana.tools;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

import net.eledge.android.europeana.Config;

public class UriHelper {

	public static URL getSearchURL(String term, int page) {
		try {
			String termEncoded = URLEncoder.encode(term, Config.JSON_CHARSET);
			return new URL(String.format(Config.URL_API_SEARCH, Config.API_KEY, Integer.valueOf(page), termEncoded));
		} catch (MalformedURLException e) {
			return null;
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public static URI getSearchURI(String term, int page) {
		try {
			String termEncoded = URLEncoder.encode(term, Config.JSON_CHARSET);
			return new URI(String.format(Config.URL_API_SEARCH, Config.API_KEY, Integer.valueOf(page), termEncoded));
		} catch (URISyntaxException e) {
			return null;
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public static URI getSuggestionURI(String term) {
		try {
			String termEncoded = URLEncoder.encode(term, Config.JSON_CHARSET);
			return new URI(String.format(Config.URL_API_SUGGESTIONS, termEncoded));
		} catch (URISyntaxException e) {
			return null;
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

}
