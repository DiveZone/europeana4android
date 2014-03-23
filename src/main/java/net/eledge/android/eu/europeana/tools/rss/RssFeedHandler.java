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

package net.eledge.android.eu.europeana.tools.rss;

import android.text.Html;

import net.eledge.android.eu.europeana.db.model.BlogArticle;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RssFeedHandler extends DefaultHandler {

    private final SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);

    private BlogArticle article;
    private StringBuilder content = new StringBuilder();

    public List<BlogArticle> articles = new ArrayList<>();

    private final Date mLastViewed;

    public RssFeedHandler(Date lastViewed) {
        super();
        mLastViewed = lastViewed;
    }

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
            article.markedNew = mLastViewed.before(article.pubDate);
            articles.add(article);
        } else if (article != null) {
            if (localName.equalsIgnoreCase("title")) {
                article.title = content.toString();
            } else if (localName.equalsIgnoreCase("description")) {
                article.description = Html.fromHtml(content.toString().replaceAll("<.*?>", "")).toString();
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
