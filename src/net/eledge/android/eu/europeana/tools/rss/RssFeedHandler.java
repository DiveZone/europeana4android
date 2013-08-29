package net.eledge.android.eu.europeana.tools.rss;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import net.eledge.android.eu.europeana.db.model.BlogArticle;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.text.Html;

public class RssFeedHandler extends DefaultHandler {
	
	SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);

	private BlogArticle article;
	private StringBuilder content = new StringBuilder();

	public List<BlogArticle> articles = new ArrayList<BlogArticle>();

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		content = new StringBuilder();
		if (localName.equalsIgnoreCase("item")) {
			article = new BlogArticle();
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if (localName.equalsIgnoreCase("item")) {
			articles.add(article);
		} else if (article != null) {
			if (localName.equalsIgnoreCase("title")) {
				article.title = content.toString();
			} else if (localName.equalsIgnoreCase("description")) {
				article.description = Html.fromHtml(content.toString().replaceAll("\\<.*?>","")).toString();
			} else if (localName.equalsIgnoreCase("pubDate")) {
				article.pubDate = convertDate(content.toString());
			} else if (localName.equalsIgnoreCase("link")) {
				article.guid = content.toString();
			} else if (qName.equalsIgnoreCase("dc:creator")) {
				article.author = content.toString();
			}
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		content.append(new String(ch, start, length));
	}
	
	private Date convertDate(String str) {
		try {
			return formatter.parse(str);
		} catch (ParseException e) {
			return null;
		}
	}
}
