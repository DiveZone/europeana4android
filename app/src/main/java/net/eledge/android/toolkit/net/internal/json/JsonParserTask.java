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
package net.eledge.android.toolkit.net.internal.json;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.eledge.android.toolkit.net.abstracts.AsyncLoaderListener;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JsonParserTask<T> extends AsyncTask<String, Void, T> {

  private final Class<T> clazz;
  private final AsyncLoaderListener<T> mListener;

  @SuppressWarnings("unchecked")
  public JsonParserTask(Class<T> clazz, AsyncLoaderListener<T> mListener) {
    this.clazz = clazz;
    this.mListener = mListener;
  }

  private int mHttpStatus = HttpURLConnection.HTTP_INTERNAL_ERROR;
  private DateTime startTime;

  @Override
  final protected void onPreExecute() {
    startTime = DateTime.now();
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  protected T doInBackground(String... params) {
    String json = downloadAsString(params[0], params[1]);
    if (StringUtils.isNotBlank(json)) {
      try {
        return new ObjectMapper().readValue(
          new JsonFactory().createParser(json), clazz
        );
      } catch (IOException e) {
        Log.e(this.getClass().getName(), e.getMessage(), e);
      }
    }
    return null;
  }

  @Override
  final protected void onPostExecute(T t) {
    mListener.onFinished(t, mHttpStatus, new Interval(startTime, DateTime.now()).toDurationMillis());
  }

  @Nullable
  protected String downloadAsString(@NonNull String urlString, @NonNull String charType) {
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    Reader isReader = null;
    try {
      URL url = new URL(urlString);
      urlConnection = (HttpURLConnection) url.openConnection();
      urlConnection.setRequestProperty("Content-Type", "application/json");
      urlConnection.setRequestProperty("Accept", "application/json");
      urlConnection.setRequestMethod("GET");
      mHttpStatus = urlConnection.getResponseCode();
      if (mHttpStatus == HttpURLConnection.HTTP_OK) {
        isReader = new InputStreamReader(urlConnection.getInputStream(), charType);
        reader = new BufferedReader(isReader);
        StringBuilder json = new StringBuilder(2048);
        String line = reader.readLine();
        while (line != null) {
          json.append(line);
          line = reader.readLine();
        }
        return json.toString();
      }
    } catch (IOException e) {
      Log.e(this.getClass().getName(), e.getMessage(), e);
    } finally {
      try {
        if (reader != null) {
          reader.close();
        }
      } catch (IOException ignored) {
      }
      try {
        if (isReader != null) {
          isReader.close();
        }
      } catch (IOException ignored) {
      }
      if (urlConnection != null) {
        urlConnection.disconnect();
      }
    }
    return null;
  }

}
