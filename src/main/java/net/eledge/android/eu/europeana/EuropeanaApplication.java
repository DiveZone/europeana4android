package net.eledge.android.eu.europeana;

import android.app.Application;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Typeface;

import net.eledge.android.toolkit.net.ImageCacheManager;

import org.springframework.security.crypto.encrypt.AndroidEncryptors;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.sqlite.SQLiteConnectionRepository;
import org.springframework.social.connect.sqlite.support.SQLiteConnectionRepositoryHelper;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.europeana.api.Europeana;
import org.springframework.social.europeana.connect.EuropeanaConnectionFactory;

import java.util.Locale;

public class EuropeanaApplication extends Application {

    // META DATA
    public static final String METADATA_EUROPEANA_API_PUBLICKEY = "eu.europeana.api.v2.API_PUBLIC_KEY";
    public static final String METADATA_EUROPEANA_API_PRIVATEKEY = "eu.europeana.api.v2.API_PRIVATE_KEY";

    private ConnectionFactoryRegistry connectionFactoryRegistry;
    private SQLiteOpenHelper repositoryHelper;
    private ConnectionRepository connectionRepository;

    private ImageCacheManager imageCacheManager;

    private Typeface europeanaFont;

    public EuropeanaApplication() {
        super();
    }

    @Override
    public void onCreate() {
        // create a new ConnectionFactoryLocator and populate it with Facebook ConnectionFactory
        this.connectionFactoryRegistry = new ConnectionFactoryRegistry();
        this.connectionFactoryRegistry.addConnectionFactory(new EuropeanaConnectionFactory(getEuropeanaPublicKey(),
                getEuropeanaPrivateKey()));

        // set up the database and encryption
        this.repositoryHelper = new SQLiteConnectionRepositoryHelper(this);
        this.connectionRepository = new SQLiteConnectionRepository(this.repositoryHelper,
                this.connectionFactoryRegistry, AndroidEncryptors.text("password", "5c0744940b5c369b"));
    }

    public String getEuropeanaPublicKey() {
        return getApplicationInfo().metaData.getString(METADATA_EUROPEANA_API_PUBLICKEY);
    }

    private String getEuropeanaPrivateKey() {
        return getApplicationInfo().metaData.getString(METADATA_EUROPEANA_API_PRIVATEKEY);
    }

    public ImageCacheManager getImageCacheManager() {
        if (imageCacheManager == null) {
            imageCacheManager = new ImageCacheManager(getApplicationContext(), 60 * 60 * 60 * 24);
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

    public String getLocale() {
        return Locale.getDefault().getDisplayLanguage();
    }

    public ConnectionRepository getConnectionRepository() {
        return connectionRepository;
    }

    public EuropeanaConnectionFactory getEuropeanaConnectionFactory() {
        return (EuropeanaConnectionFactory) this.connectionFactoryRegistry.getConnectionFactory(Europeana.class);
    }

    public boolean isMyEuropeanaConnected() {
        return connectionRepository.findPrimaryConnection(Europeana.class) != null;
    }
}
