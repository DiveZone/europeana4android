/*
 * Copyright (c) 2014 eLedge.net and the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.eledge.android.eu.europeana;

import android.app.Application;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Typeface;
import android.os.Bundle;

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
    private ConnectionRepository connectionRepository;

    private ImageCacheManager imageCacheManager;

    private Typeface europeanaFont;

    private Bundle metaData;

    public EuropeanaApplication() {
        super();
    }

    @Override
    public void onCreate() {
        try {
            metaData = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA).applicationInfo.metaData;
            // create a new ConnectionFactoryLocator and populate it with Facebook ConnectionFactory
            this.connectionFactoryRegistry = new ConnectionFactoryRegistry();
            this.connectionFactoryRegistry.addConnectionFactory(new EuropeanaConnectionFactory(getEuropeanaPublicKey(),
                    getEuropeanaPrivateKey()));

            // set up the database and encryption
            SQLiteOpenHelper repositoryHelper = new SQLiteConnectionRepositoryHelper(this);
            this.connectionRepository = new SQLiteConnectionRepository(repositoryHelper,
                    this.connectionFactoryRegistry, AndroidEncryptors.text("password", "5c0744940b5c369b"));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getEuropeanaPublicKey() {
        return metaData.getString(METADATA_EUROPEANA_API_PUBLICKEY);
    }

    private String getEuropeanaPrivateKey() {
        return metaData.getString(METADATA_EUROPEANA_API_PRIVATEKEY);
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

    public Europeana getMyEuropeanaApi() {
        return isMyEuropeanaConnected() ? connectionRepository.findPrimaryConnection(Europeana.class).getApi() : null;
    }

    public boolean isMyEuropeanaConnected() {
        return (connectionRepository != null) && (connectionRepository.findPrimaryConnection(Europeana.class) != null);
    }
}
