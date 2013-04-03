package net.eledge.android.europeana;

public class Config {

	public static final boolean DEBUGMODE = true;

	public static final String API_KEY = "IZHBRKKFDW";

	public static final String URL_SERVER = "http://acceptance.europeana.eu/";
	public static final String URL_API = URL_SERVER + "api/";
	public static final String URL_API_SEARCH = URL_API + "opensearch.json?wskey=%s&startPage=%d&searchTerms=%s";
	public static final String URL_API_IMAGE_BRIEF = "http://europeanastatic.eu/api/image?uri=%s&size=BRIEF_DOC&type=IMAGE&rswUserId=unknown";
	public static final String URL_API_IMAGE_FULL = "http://europeanastatic.eu/api/image?uri=%s&size=FULL_DOC&type=IMAGE&rswUserId=unknown";
	public static final String URL_API_SUGGESTIONS = URL_SERVER + "portal/suggestions.json?term=%s";

	public static final String JSON_CHARSET = "UTF-8";

	private Config() {
	}
}