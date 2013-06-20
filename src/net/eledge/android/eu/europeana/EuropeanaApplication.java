package net.eledge.android.eu.europeana;

import net.eledge.android.toolkit.net.ImageCacheManager;
import android.app.Application;

public class EuropeanaApplication extends Application {
	
	private ImageCacheManager imageCacheManager;
	
	public EuropeanaApplication() {
		super();
	}
	
	public ImageCacheManager getImageCacheManager() {
		if (imageCacheManager == null) {
			imageCacheManager = new ImageCacheManager(getApplicationContext(), 60*60*60*24);
			imageCacheManager.clearCache();
		}
		return imageCacheManager;
	}
	
}
