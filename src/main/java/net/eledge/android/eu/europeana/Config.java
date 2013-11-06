package net.eledge.android.eu.europeana;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class Config {

	public static Config _instance = new Config();

	public static final boolean DEBUGMODE = true;
	public static final String JSON_CHARSET = "UTF-8";

	public final static String DB_NAME = "europeana4android";
	public final static int DB_VERSION = 2;
	
	// META DATA
	public static final String METADATA_EUROPEANA_API_PUBLICKEY = "eu.europeana.api.v2.API_PUBLIC_KEY";

	private String europeana_publickey = null;

	public String getEuropeanaPublicKey(Activity activity) {
		if (europeana_publickey == null) {
			try {
				ApplicationInfo ai = activity.getPackageManager().getApplicationInfo(activity.getPackageName(),
						PackageManager.GET_META_DATA);
				europeana_publickey = ai.metaData.getString(METADATA_EUROPEANA_API_PUBLICKEY);
			} catch (NameNotFoundException | NullPointerException e) {
                Log.e(getClass().getName(), e.getMessage(), e);
			}
        }
		return europeana_publickey;
	}

	private Config() {
	}
}