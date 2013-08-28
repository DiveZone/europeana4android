package net.eledge.android.eu.europeana.tools;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.eledge.android.eu.europeana.db.model.Article;
import net.eledge.android.eu.europeana.tools.rss.RssFeedHandler;
import net.eledge.android.toolkit.async.ListenerNotifier;
import net.eledge.android.toolkit.async.listener.TaskListener;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class RssReader extends AsyncTask<String, Void, List<Article>> {
	
	private Activity mActivity;
	
	private TaskListener<List<Article>> mListener;
	
	public RssReader(Activity activity, TaskListener<List<Article>> listener) {
		super();
		mActivity = activity;
		mListener = listener;
	}

	@Override
	protected List<Article> doInBackground(String... urls) {
		String feed = urls[0];
		URL url = null;
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();

			url = new URL(feed);
			RssFeedHandler rh = new RssFeedHandler();

			xr.setContentHandler(rh);
			xr.parse(new InputSource(url.openStream()));

			return rh.articles;
		} catch (IOException e) {
			Log.e("RSS Handler IO", e.getMessage() + " >> " + e.toString());
		} catch (SAXException e) {
			Log.e("RSS Handler SAX", e.toString());
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			Log.e("RSS Handler Parser Config", e.toString());
		}

		return null;
	}
	
	@Override
	protected void onPostExecute(List<Article> articles) {
		mActivity.runOnUiThread(new ListenerNotifier<List<Article>>(mListener, articles));
	}

}
