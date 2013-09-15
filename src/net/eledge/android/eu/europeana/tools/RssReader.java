package net.eledge.android.eu.europeana.tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.eledge.android.eu.europeana.db.model.BlogArticle;
import net.eledge.android.eu.europeana.tools.rss.RssFeedHandler;
import net.eledge.android.toolkit.async.ListenerNotifier;
import net.eledge.android.toolkit.async.listener.TaskListener;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

public class RssReader extends AsyncTask<String, Void, List<BlogArticle>> {
	
	private Activity mActivity;
	
	private TaskListener<List<BlogArticle>> mListener;
	
	public RssReader(Activity activity, TaskListener<List<BlogArticle>> listener) {
		super();
		mActivity = activity;
		mListener = listener;
	}

	@Override
	protected List<BlogArticle> doInBackground(String... urls) {
		String feed = urls[0];
		InputStream is = null;
		try {
			HttpGet request = new HttpGet(feed);
			AndroidHttpClient.modifyRequestToAcceptGzipResponse(request);
			HttpResponse response = new DefaultHttpClient().execute(request);
			is = AndroidHttpClient.getUngzippedContent(response.getEntity());

			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();

			RssFeedHandler rh = new RssFeedHandler();

			xr.setContentHandler(rh);
			xr.parse(new InputSource(is));

			return rh.articles;
		} catch (IOException e) {
			Log.e("RSS Handler IO", e.getMessage() + " >> " + e.toString());
		} catch (SAXException e) {
			Log.e("RSS Handler SAX", e.toString());
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			Log.e("RSS Handler Parser Config", e.toString());
		} finally {
			IOUtils.closeQuietly(is);
		}

		return null;
	}
	
	@Override
	protected void onPostExecute(List<BlogArticle> articles) {
		if (!isCancelled()) {
			mActivity.runOnUiThread(new ListenerNotifier<List<BlogArticle>>(mListener, articles));
		}
	}

}
