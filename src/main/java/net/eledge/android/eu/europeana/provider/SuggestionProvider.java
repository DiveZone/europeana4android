package net.eledge.android.eu.europeana.provider;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;

import net.eledge.android.eu.europeana.search.model.Suggestions;
import net.eledge.android.eu.europeana.search.model.suggestion.Item;
import net.eledge.android.eu.europeana.tools.UriHelper;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;

public class SuggestionProvider extends ContentProvider {
	
	public static String AUTHORITY = "net.eledge.android.eu.europeana.provider.SuggestionProvider";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/suggestions");

	// UriMatcher stuff
	private static final int SEARCH_SUGGEST = 1;
	private static final UriMatcher sURIMatcher = buildUriMatcher();

	private final String[] columns = { BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_TEXT_2,
			SearchManager.SUGGEST_COLUMN_INTENT_DATA };
	
	@Override
	public String getType(Uri uri) {
		switch (sURIMatcher.match(uri)) {
		case SEARCH_SUGGEST:
			return SearchManager.SUGGEST_MIME_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URL " + uri);
		}
	}

	@Override
	public boolean onCreate() {
		return true;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		switch (sURIMatcher.match(uri)) {
		case SEARCH_SUGGEST:
			String query = null;
			if (uri.getPathSegments().size() > 1) {
				query = uri.getLastPathSegment().toLowerCase(Locale.getDefault());
			}
			return getSuggestions(query);
		default:
			throw new IllegalArgumentException("Unknown Uri: " + uri);
		}
	}

	private Cursor getSuggestions(String query) {
        if (StringUtils.isBlank(query)) {
            return null;
        }
        String url = UriHelper.getSuggestionUrl(query, 12);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        MatrixCursor cursor = new MatrixCursor(columns);
        int i = 0;
        for (Item item: restTemplate.getForObject(url, Suggestions.class).items) {
            String[] tmp = { Integer.toString(i), item.term, item.field + " (" + item.frequency + ")", item.term };
            cursor.addRow(tmp);
            i++;
        }
        return cursor;
	}

	/**
	 * Builds up a UriMatcher for search suggestion and shortcut refresh queries.
	 */
	private static UriMatcher buildUriMatcher() {
		UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
		matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);
		return matcher;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		throw new UnsupportedOperationException();
	}

}
