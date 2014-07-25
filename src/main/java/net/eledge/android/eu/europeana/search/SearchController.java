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

package net.eledge.android.eu.europeana.search;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask.Status;

import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.search.listeners.SearchTaskListener;
import net.eledge.android.eu.europeana.search.model.SearchFacets;
import net.eledge.android.eu.europeana.search.model.SearchItems;
import net.eledge.android.eu.europeana.search.model.facets.enums.FacetItemType;
import net.eledge.android.eu.europeana.search.model.facets.enums.FacetType;
import net.eledge.android.eu.europeana.search.model.searchresults.Facet;
import net.eledge.android.eu.europeana.search.model.searchresults.FacetItem;
import net.eledge.android.eu.europeana.search.model.searchresults.Field;
import net.eledge.android.eu.europeana.search.model.suggestion.Item;
import net.eledge.android.eu.europeana.search.task.SearchFacetTask;
import net.eledge.android.eu.europeana.search.task.SearchTask;
import net.eledge.android.eu.europeana.search.task.SuggestionTask;
import net.eledge.android.eu.europeana.tools.UriHelper;
import net.eledge.android.toolkit.async.listener.TaskListener;
import net.eledge.android.toolkit.gui.GuiUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchController {

    public final static SearchController _instance = new SearchController();

    public int searchPageSize = 12;
    public int suggestionPageSize = 12;

    public final Map<String, SearchTaskListener> listeners = new HashMap<>();
    private final List<String> terms = new ArrayList<>();

    private int pageLoad = 1;
    private long totalResults;
    private int itemSelected = -1;

    private final List<net.eledge.android.eu.europeana.search.model.searchresults.Item> searchItems = new ArrayList<>();
    private final List<Facet> facets = new ArrayList<>();
    private final Map<String, Item[]> suggestionCache = new HashMap<>();

    private FacetType selectedFacet = FacetType.TYPE;

    private SearchTask mSearchTask;
    private SearchFacetTask mSearchFacetTask;
    private SuggestionTask mSuggestionTask;

    private SearchController() {
        // Singleton
    }

    public void registerListener(Class<?> clazz, SearchTaskListener listener) {
        listeners.put(clazz.getName(), listener);
    }

    public void unregister(Class<?> clazz) {
        listeners.remove(clazz.getName());
    }

    public void suggestions(TaskListener<Item[]> listener, String query) {
        if (mSuggestionTask != null) {
            mSuggestionTask.cancel(true);
        }
        query = StringUtils.lowerCase(StringUtils.trim(query));
        if (suggestionCache.containsKey(query)) {
            listener.onTaskFinished(suggestionCache.get(query));
        } else {
            mSuggestionTask = new SuggestionTask(listener);
            mSuggestionTask.execute(query);
        }
    }

    @SuppressWarnings("ManualArrayToCollectionCopy")
    public void newSearch(Activity activity, String query, String... qf) {
        reset();
        terms.clear();
        terms.add(query);
        for (String s : qf) {
            terms.add(s);
        }
        search(activity);
    }

    @SuppressWarnings("ManualArrayToCollectionCopy")
    public void refineSearch(Activity activity, String... qf) {
        reset();
        for (String s : qf) {
            terms.add(s);
        }
        search(activity);
    }

    public boolean removeRefineSearch(Activity activity, String... qf) {
        boolean changed = false;
        for (String s : qf) {
            if (terms.contains(s)) {
                terms.remove(s);
                changed = true;
            }
        }
        if ((terms.size() > 0) && changed) {
            reset();
            search(activity);
        }
        return terms.size() > 0;
    }

    public void continueSearch(Activity activity) {
        if (hasMoreResults()) {
            search(activity);
        }
    }

    public boolean hasResults() {
        return totalResults > 0;
    }

    public boolean hasFacets() {
        return !facets.isEmpty();
    }

    public boolean hasMoreResults() {
        return totalResults > searchItems.size();
    }

    public boolean isSearching() {
        return mSearchTask != null && mSearchTask.getStatus() != Status.FINISHED;
    }

    public void cancelSearch() {
        if (mSearchTask != null) {
            mSearchTask.cancel(true);
        }
        if (mSearchFacetTask != null) {
            mSearchFacetTask.cancel(true);
        }
    }

    void reset() {
        cancelSearch();
        pageLoad = 1;
        totalResults = 0;
        itemSelected = -1;
        synchronized (searchItems) {
            searchItems.clear();
        }
        facets.clear();
    }

    private void search(Activity activity) {
        if (mSearchTask != null) {
            cancelSearch();
        }
        mSearchTask = new SearchTask(activity, pageLoad++);
        mSearchTask.execute(terms.toArray(new String[terms.size()]));
        if (facets.isEmpty()) {
            mSearchFacetTask = new SearchFacetTask(activity);
            mSearchFacetTask.execute(terms.toArray(new String[terms.size()]));
        }
    }

    public List<FacetItem> getBreadcrumbs(Context context) {
        List<FacetItem> breadcrumbs = new ArrayList<>();
        FacetItem crumb;
        for (String term : terms) {
            crumb = new FacetItem();
            crumb.itemType = FacetItemType.BREADCRUMB;
            crumb.facetType = FacetType.TEXT;
            crumb.description = term;
            crumb.facet = term;
            if (StringUtils.contains(term, ":")) {
                FacetType type = FacetType.safeValueOf(StringUtils.substringBefore(term, ":"));
                if (type != null) {
                    crumb.facetType = type;
                    crumb.description = StringUtils.capitalize(StringUtils.lowerCase(GuiUtils.getString(context, type.resId))) + ":" + type.createFacetLabel(context, StringUtils.substringAfter(term, ":"));
                }
            }
            breadcrumbs.add(crumb);
        }
        return breadcrumbs;
    }

    public List<FacetItem> getFacetList(Context context) {
        List<FacetItem> facetList = new ArrayList<>();
        if (!facets.isEmpty()) {
            for (Facet facet : facets) {
                FacetType type = FacetType.safeValueOf(facet.name);
                if (type != null) {
                    FacetItem item = new FacetItem();
                    item.itemType = type == selectedFacet ? FacetItemType.CATEGORY_OPENED : FacetItemType.CATEGORY;
                    item.facetType = type;
                    item.facet = facet.name;
                    facetList.add(item);
                    if (type == selectedFacet) {
                        for (Field field : facet.fields) {
                            item = new FacetItem();
                            item.facetType = type;
                            item.label = field.label;
                            item.facet = facet.name + ":" + field.label;
                            item.itemType = terms.contains(item.facet) ? FacetItemType.ITEM_SELECTED
                                    : FacetItemType.ITEM;
                            item.description = type.createFacetLabel(context, field.label) + " (" + field.count + ")";
                            item.icon = type.getFacetIcon(field.label);
                            facetList.add(item);
                        }
                        facetList.get(facetList.size() - 1).last = true;
                    }
                }
            }
            facetList.get(facetList.size() - 1).last = true;
        }
        return facetList;
    }

    public String getPortalUrl() {
        return UriHelper.createPortalSearchUrl(terms.toArray(new String[terms.size()]));
    }

    public boolean hasFacetsSelected() {
        return (terms.size() > 1);
    }

    public String getFacetString() {
        if (hasFacetsSelected()) {
            StringBuilder sb = new StringBuilder(256);
            for (int i = 1; i < terms.size(); i++) {
                // skipping 0 on purpose, is the search term 'query'
                if (sb.length() > 0) {
                    sb.append("&");
                }
                sb.append("qf=").append(terms.get(i));
            }
            return sb.toString();
        }
        return null;
    }

    public void cacheSuggestions(String term, Item[] suggestions) {
        suggestionCache.put(term, suggestions);
    }

    public synchronized void onSearchFinish(SearchItems results) {
        if (results != null) {
            totalResults = results.totalResults;
            searchItems.addAll(results.items);
        }
    }

    public synchronized void onSearchFacetFinish(SearchFacets results) {
        if (results != null) {
            facets.clear();
            facets.addAll(results.facets);
        }
    }

    public synchronized List<net.eledge.android.eu.europeana.search.model.searchresults.Item> getSearchItems() {
        return searchItems;
    }

    public int getItemSelected() {
        return itemSelected;
    }

    public void setItemSelected(int itemSelected) {
        this.itemSelected = itemSelected;
    }

    public boolean hasPrevious() {
        return this.itemSelected > 0;
    }

    public boolean hasNext() {
        return this.itemSelected < searchItems.size() - 1;
    }

    public Integer size() {
        return searchItems != null ? Integer.valueOf(searchItems.size()) : Integer.valueOf(0);
    }

    public void setCurrentFacetType(FacetType facetType) {
        selectedFacet = facetType;
    }

    public String getSearchTitle(Context context) {
        String title = GuiUtils.getString(context, R.string.app_name);
        if (!terms.isEmpty()) {
            title = terms.get(0);
            if (StringUtils.contains(title, ":")) {
                title = StringUtils.substringAfter(title, ":");
            }
            title = WordUtils.capitalizeFully(title);
        }
        return title;
    }

}
