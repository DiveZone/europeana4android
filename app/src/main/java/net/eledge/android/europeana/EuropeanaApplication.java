/*
 * Copyright (c) 2013-2016 eLedge.net and the original author or authors.
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

package net.eledge.android.europeana;

import android.app.Application;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.otto.Bus;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class EuropeanaApplication extends Application {

  // META DATA
  public static final String METADATA_EUROPEANA_API_PUBLICKEY = "eu.europeana.api.v2.API_PUBLIC_KEY";
  public static final String METADATA_EUROPEANA_API_PRIVATEKEY = "eu.europeana.api.v2.API_PRIVATE_KEY";

  public static Typeface europeanaFont;

  private Bundle metaData;

  public static EuropeanaApplication _instance;

  public static Bus bus;

  public EuropeanaApplication() {
    super();
    _instance = this;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    if (LeakCanary.isInAnalyzerProcess(this)) {
      return;
    }
    JodaTimeAndroid.init(this);
    try {
      LeakCanary.install(this);
      metaData = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA).applicationInfo.metaData;
      europeanaFont = Typeface.createFromAsset(getAssets(), "europeana.ttf");
      //activate auto tracking
      bus = new Bus();
      Realm.init(this);
      Realm.setDefaultConfiguration(new RealmConfiguration.Builder().build());
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

  public String getLocale() {
    return Locale.getDefault().getDisplayLanguage();
  }

  public boolean isWifiConnected() {
    return false;
  }
}
