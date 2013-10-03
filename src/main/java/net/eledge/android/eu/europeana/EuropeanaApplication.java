package net.eledge.android.eu.europeana;

import android.app.Application;
import android.graphics.Typeface;

import net.eledge.android.toolkit.net.ImageCacheManager;

public class EuropeanaApplication extends Application {
	
	private ImageCacheManager imageCacheManager;
	
	private Typeface europeanaFont; 
			
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
	
	public Typeface getEuropeanaFont() {
		if (europeanaFont == null) {
			europeanaFont = Typeface.createFromAsset(getAssets(), "europeana.ttf");
		}
		return europeanaFont;
	}
	
}
