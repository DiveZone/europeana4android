package net.eledge.android.eu.europeana.tools;

import net.eledge.android.eu.europeana.Config;
import net.eledge.android.toolkit.net.UrlBuilder;

import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

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

	public static String getSearchUrl(String apikey, String[] terms, int page, int pagesize) {
        return createSearchUrl(apikey, terms, page, pagesize).toString();
	}

	public static String getRecordUrl(String apikey, String id) {
		return String.format(Locale.US, URL_API_RECORD, id, apikey);
	}

	public static String createPortalSearchUrl(String[] terms) {
		UrlBuilder builder = new UrlBuilder(URL_PORTAL_SEARCH);
		setSearchParams(builder, terms, -1, -1);
		return builder.toString();
	}

	public static String getPortalRecordUrl(String id) {
		return String.format(Locale.US, URL_PORTAL_RECORD, id);
	}

	private static UrlBuilder createSearchUrl(String apikey, String[] terms, int page, int pagesize) {
		UrlBuilder builder = new UrlBuilder(URL_API_SEARCH);
		builder.addParam("profile", pagesize==1?"minimal+facets":"minimal", true);
		builder.addParam("wskey", apikey, true);
		setSearchParams(builder, terms, page, pagesize);
		return builder;
	}

	private static void setSearchParams(UrlBuilder builder, String[] terms, int page, int pagesize) {
		if (pagesize > -1) {
			if (page > -1) {
				int start = 1 + ((page - 1) * pagesize);
				builder.addParam("start", String.valueOf(start), true);
			}
			builder.addParam("rows", String.valueOf(pagesize), true);
		}
		for (int i = 0; i < terms.length; i++) {
			if (i == 0) {
				builder.addParam("query", terms[i], true);
			} else {
				builder.addMultiParam("qf", terms[i]);
			}
		}
	}

	public static String getSuggestionUrl(String term, int pagesize) {
		try {
			String termEncoded = URLEncoder.encode(StringUtils.lowerCase(term), Config.JSON_CHARSET);
			return String.format(URL_API_SUGGESTIONS, Integer.valueOf(pagesize), termEncoded);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

}
