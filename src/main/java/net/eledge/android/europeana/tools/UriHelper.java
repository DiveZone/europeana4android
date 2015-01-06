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

import net.eledge.android.europeana.Config;
import net.eledge.urlbuilder.UrlBuilder;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

public class UriHelper {

    private static final String URL_API = "http://europeana.eu/api/v2/";
    public static final String URL_BLOG = "http://blog.europeana.eu";
    public static final String URL_BLOGFEED = URL_BLOG + "/feed/";

    // API METHODS
    private static final String URL_API_SEARCH = URL_API + "search.json";
    private static final String URL_API_SUGGESTIONS = URL_API + "suggestions.json?rows=%d&query=%s&phrases=false";
    private static final String URL_API_RECORD = URL_API + "record%s.json?wskey=%s";

    // PORTAL URL's
    private static final String URL_PORTAL = "http://www.europeana.eu/portal/";
    private static final String URL_PORTAL_SEARCH = URL_PORTAL + "search.html";
    private static final String URL_PORTAL_RECORD = URL_PORTAL + "record%s.html";

    public static String getSearchUrl(String apiKey, String[] terms, int page, int pageSize) {
        return createSearchUrl(apiKey, terms, page, pageSize).toString();
    }

    public static String getRecordUrl(String apiKey, String id) {
        return String.format(Locale.US, URL_API_RECORD, id, apiKey);
    }

    public static String createPortalSearchUrl(String[] terms) {
        UrlBuilder builder = new UrlBuilder(URL_PORTAL_SEARCH);
        setSearchParams(builder, terms, -1, -1);
        return builder.toString();
    }

    public static String getPortalRecordUrl(String id) {
        return String.format(Locale.US, URL_PORTAL_RECORD, id);
    }

    private static UrlBuilder createSearchUrl(String apiKey, String[] terms, int page, int pageSize) {
        UrlBuilder builder = new UrlBuilder(URL_API_SEARCH);
        builder.overrideParam("profile", pageSize == 1 ? "minimal+facets" : "minimal");
        builder.overrideParam("wskey", apiKey);
        setSearchParams(builder, terms, page, pageSize);
        builder.disableEncoding();
        return builder;
    }

    private static void setSearchParams(UrlBuilder builder, String[] terms, int page, int pageSize) {
        if (pageSize > -1) {
            if (page > -1) {
                int start = 1 + ((page - 1) * pageSize);
                builder.overrideParam("start", String.valueOf(start));
            }
            builder.addParam("rows", String.valueOf(pageSize));
        }
        for (int i = 0; i < terms.length; i++) {
            if (i == 0) {
                builder.overrideParam("query", terms[i]);
            } else {
                builder.addParam("qf", terms[i]);
            }
        }
    }

    public static String getSuggestionUrl(String term, int pageSize) {
        try {
            String termEncoded = URLEncoder.encode(StringUtils.lowerCase(term), Config.JSON_CHARSET);
            return String.format(URL_API_SUGGESTIONS, pageSize, termEncoded);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

}
