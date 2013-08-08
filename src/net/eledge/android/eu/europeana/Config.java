package net.eledge.android.eu.europeana;

public class Config {

	public static final boolean DEBUGMODE = true;

	public static final String API_KEY = "eu4andrd";

	public static final String URL_API = "http://europeana.eu/api/v2/";

	public static final String URL_API_SEARCH = URL_API
			+ "search.json?wskey=%s&start=%d&rows=%d&profile=portal&query=%s";
	public static final String URL_API_SUGGESTIONS = URL_API
			+ "suggestions.json?rows=12&query=%s&phrases=false";

	// public static final String URL_API_IMAGE_BRIEF =
	// "http://europeanastatic.eu/api/image?uri=%s&size=BRIEF_DOC&type=IMAGE&rswUserId=unknown";
	// public static final String URL_API_IMAGE_FULL =
	// "http://europeanastatic.eu/api/image?uri=%s&size=FULL_DOC&type=IMAGE&rswUserId=unknown";

	public static final String JSON_CHARSET = "UTF-8";

	public static final String URL_PORTAL_SEARCH = "http://europeana.eu/portal/search.html?query=%s";

	private Config() {
	}
}