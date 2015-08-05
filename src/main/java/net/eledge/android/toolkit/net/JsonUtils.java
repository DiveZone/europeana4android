/*
 * Copyright (c) 2013-2015 eLedge.net and the original author or authors.
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

package net.eledge.android.toolkit.net;

import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;

import net.eledge.android.toolkit.net.abstracts.AsyncLoaderListener;
import net.eledge.android.toolkit.net.internal.json.JsonParserTask;

public class JsonUtils {

    public static <T> AsyncTask<String, Void, T> parseJson(@NonNull final AsyncLoaderListener<T> listener,
                                                           @NonNull String url, @NonNull String charset) {
        disableConnectionReuseIfNecessary();
        return new JsonParserTask<>(listener).execute(url, charset);
    }

    private static void disableConnectionReuseIfNecessary() {
        // HTTP connection reuse which was buggy pre-froyo
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
    }

//    private void enableHttpResponseCache() {
//        try {
//            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
//            File httpCacheDir = new File(getCacheDir(), "http");
//            Class.forName("android.net.http.HttpResponseCache")
//                    .getMethod("install", File.class, long.class)
//                    .invoke(null, httpCacheDir, httpCacheSize);
//        } catch (Exception httpResponseCacheNotAvailable) {
//        }
//    }

}
