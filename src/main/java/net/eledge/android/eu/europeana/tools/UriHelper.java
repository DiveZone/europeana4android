package net.eledge.android.eu.europeana.tools;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Locale;

import net.eledge.android.eu.europeana.Config;
import net.eledge.android.toolkit.net.UrlBuilder;

public class UriHelper {

	private static final String URL_API = "http://europeana.eu/api/v2/";
	public static final String URL_BLOGFEED = "http://blog.europeana.eu/feed/";

	// API METHODS
	private static final String URL_API_SEARCH = URL_API + "search.json";
	private static final String URL_API_SUGGESTIONS = URL_API + "suggestions.json?rows=%d&query=%s&phrases=false";
	private static final String URL_API_RECORD = URL_API + "record%s.json?wskey=%s";

	// PORTAL URL's
	private static final String URL_PORTAL = "http://www.europeana.eu/portal/";
	private static final String URL_PORTAL_SEARCH = URL_PORTAL + "search.html";
	private static final String URL_PORTAL_RECORD = URL_PORTAL + "record%s.html";

	public static URI getSearchURI(String apikey, String[] terms, int page, int pagesize) {
		try {
			return new URI(createSearchUrl(apikey, terms, page, pagesize).toString());
		} catch (URISyntaxException e) {
			return null;
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public static URI getRecordURI(String apikey, String id) {
		String url = String.format(Locale.US, URL_API_RECORD, id, apikey);
		try {
			return new URI(url);
		} catch (URISyntaxException e) {
			return null;
		}
	}

	public static String createPortalSearchUrl(String[] terms) throws UnsupportedEncodingException {
		UrlBuilder builder = new UrlBuilder(URL_PORTAL_SEARCH);
		setSearchParams(builder, terms, -1, -1);
		return builder.toString();
	}

	public static String getPortalRecordUrl(String id) {
		return String.format(Locale.US, URL_PORTAL_RECORD, id);
	}

	private static UrlBuilder createSearchUrl(String apikey, String[] terms, int page, int pagesize)
			throws UnsupportedEncodingException {
		UrlBuilder builder = new UrlBuilder(URL_API_SEARCH);
		builder.addParam("profile", pagesize==1?"minimal+facets":"minimal", true);
		builder.addParam("wskey", apikey, true);
		setSearchParams(builder, terms, page, pagesize);
		return builder;
	}

	private static void setSearchParams(UrlBuilder builder, String[] terms, int page, int pagesize)
			throws UnsupportedEncodingException {
		if (pagesize > -1) {
			if (page > -1) {
				int start = 1 + ((page - 1) * pagesize);
				builder.addParam("start", String.valueOf(start), true);
			}
			builder.addParam("rows", String.valueOf(pagesize), true);
		}
		for (int i = 0; i < terms.length; i++) {
			String termEncoded = URLEncoder.encode(terms[i], Config.JSON_CHARSET);
			if (i == 0) {
				builder.addParam("query", termEncoded, true);
			} else {
				builder.addMultiParam("qf", termEncoded);
			}
		}
	}

	public static URI getSuggestionURI(String term, int pagesize) {
		try {
			String termEncoded = URLEncoder.encode(term, Config.JSON_CHARSET);
			return new URI(String.format(URL_API_SUGGESTIONS, Integer.valueOf(pagesize), termEncoded));
		} catch (URISyntaxException e) {
			return null;
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

}
