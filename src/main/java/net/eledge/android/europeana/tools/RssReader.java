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

package net.eledge.android.europeana.tools;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

import net.eledge.android.europeana.db.model.BlogArticle;
import net.eledge.android.europeana.tools.rss.RssFeedHandler;
import net.eledge.android.toolkit.async.listener.TaskListener;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.DateTime;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class RssReader extends AsyncTask<String, Void, List<BlogArticle>> {

    private final TaskListener<List<BlogArticle>> mListener;

    private final DateTime mLastViewed;

    public RssReader(DateTime lastViewed, TaskListener<List<BlogArticle>> listener) {
        super();
        mListener = listener;
        mLastViewed = lastViewed;
    }

    @Override
    protected List<BlogArticle> doInBackground(String... urls) {
        String feed = urls[0];
        return RssReader.readFeed(feed, mLastViewed);
    }

    @Override
    protected void onPostExecute(List<BlogArticle> articles) {
        if (!isCancelled()) {
            mListener.onTaskFinished(articles);
        }
    }

    public static List<BlogArticle> readFeed(String url, DateTime lastViewed) {
        InputStream is = null;
        try {
            HttpGet request = new HttpGet(url);
            AndroidHttpClient.modifyRequestToAcceptGzipResponse(request);
            HttpResponse response = new DefaultHttpClient().execute(request);
            is = AndroidHttpClient.getUngzippedContent(response.getEntity());

            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();

            RssFeedHandler rh = new RssFeedHandler(lastViewed);

            xr.setContentHandler(rh);
            xr.parse(new InputSource(is));

            return rh.articles;
        } catch (IOException | SAXException | ParserConfigurationException e) {
            Log.e("RssReader", e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(is);
        }

        return null;
    }

}
