package net.eledge.android.eu.europeana.provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Locale;

import net.eledge.android.eu.europeana.Config;
import net.eledge.android.eu.europeana.tools.UriHelper;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

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
		if (TextUtils.isEmpty(query)) {
			return null;
		}
		URI url = UriHelper.getSuggestionURI(query, 12);
		try {
			HttpResponse response = new DefaultHttpClient().execute(new HttpGet(url));
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),
					Config.JSON_CHARSET));
			StringBuilder json = new StringBuilder();
			String line = reader.readLine();
			while (line != null) {
				json.append(line);
				line = reader.readLine();
			}
			JSONObject jsonObj = new JSONObject(json.toString());
			JSONArray array = jsonObj.getJSONArray("items");
			if ((array != null) && (array.length() > 0)) {
				MatrixCursor cursor = new MatrixCursor(columns);
				for (int i = 0; i < array.length(); i++) {
					JSONObject item = array.getJSONObject(i);
					final String term = item.getString("term");
					final String field = item.getString("field");
					final long freq = item.getLong("frequency");
					StringBuilder sb = new StringBuilder(field);
					sb.append(" (").append(freq).append(")");
					String[] tmp = { Integer.toString(i), term, sb.toString() , term };
					cursor.addRow(tmp);
				}
				return cursor;
			}
		} catch (IOException e) {
			// ignore
		} catch (JSONException e) {
			// ignore
		}
		return null;
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
