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

package net.eledge.android.europeana.service.search;

import android.content.Context;

import com.squareup.otto.Subscribe;

import net.eledge.android.europeana.EuropeanaApplication;
import net.eledge.android.europeana.R;
import net.eledge.android.europeana.service.search.event.SearchFacetsLoadedEvent;
import net.eledge.android.europeana.service.search.event.SearchItemsLoadedEvent;
import net.eledge.android.europeana.service.search.model.facets.enums.FacetType;
import net.eledge.android.europeana.service.search.model.searchresults.Facet;
import net.eledge.android.europeana.service.search.model.searchresults.FacetItem;
import net.eledge.android.europeana.service.search.model.searchresults.Field;
import net.eledge.android.europeana.service.search.model.searchresults.Item;
import net.eledge.android.europeana.tools.UriHelper;
import net.eledge.android.toolkit.gui.GuiUtils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.eledge.android.europeana.service.search.model.facets.enums.FacetItemType.BREADCRUMB;
import static net.eledge.android.europeana.service.search.model.facets.enums.FacetItemType.CATEGORY;
import static net.eledge.android.europeana.service.search.model.facets.enums.FacetItemType.CATEGORY_OPENED;
import static net.eledge.android.europeana.service.search.model.facets.enums.FacetItemType.ITEM;
import static net.eledge.android.europeana.service.search.model.facets.enums.FacetItemType.ITEM_SELECTED;
import static net.eledge.android.europeana.service.search.model.facets.enums.FacetType.TEXT;
import static net.eledge.android.europeana.service.search.model.facets.enums.FacetType.TYPE;
import static net.eledge.android.europeana.service.search.model.facets.enums.FacetType.safeValueOf;

public class SearchController {

  public final static SearchController _instance = new SearchController();
  public final static ApiTasks _tasks = ApiTasks.getInstance();

  public int searchPageSize = 12;

  private String[] terms = ArrayUtils.EMPTY_STRING_ARRAY;

  private int pageLoad = 1;
  private long totalResults;
  private int itemSelected = -1;

  private final List<Item> searchItems = new ArrayList<>();
  private final Map<String, Item> itemMap = new HashMap<>();
  private final List<Facet> facets = new ArrayList<>();

  private FacetType selectedFacet = TYPE;

  private SearchController() {
    EuropeanaApplication.bus.register(this);
    // Singleton
  }

  public void newSearch(String query, String... qf) {
    reset();
    terms = ArrayUtils.add(ArrayUtils.EMPTY_STRING_ARRAY, query);
    for (String s : qf) {
      terms = ArrayUtils.add(terms, s);
    }
    search();
  }

  public void refineSearch(String... qf) {
    reset();
    for (String s : qf) {
      terms = ArrayUtils.add(terms, s);
    }
    search();
  }

  public boolean removeRefineSearch(String... qf) {
    boolean changed = false;
    for (String s : qf) {
      if (ArrayUtils.contains(terms, s)) {
        terms = ArrayUtils.removeElement(terms, s);
        changed = true;
      }
    }
    if ((terms.length > 0) && changed) {
      reset();
      search();
    }
    return terms.length > 0;
  }

  public void continueSearch() {
    if (hasMoreResults()) {
      search();
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
    return _tasks.isSearching();
  }

  public void cancelSearch() {
    _tasks.cancelSearchTasks();
  }

  void reset() {
    cancelSearch();
    pageLoad = 1;
    totalResults = 0;
    itemSelected = -1;
    synchronized (searchItems) {
      searchItems.clear();
    }
    itemMap.clear();
    facets.clear();
  }

  private void search() {
    _tasks.cancelSearchTasks();
    _tasks.runSearch(terms, pageLoad++, searchPageSize);
    if (facets.isEmpty()) {
      _tasks.runSearchFacets(terms);
    }
  }

  public List<FacetItem> getBreadcrumbs(Context context) {
    List<FacetItem> breadcrumbs = new ArrayList<>();
    FacetItem crumb;
    for (String term : terms) {
      crumb = new FacetItem();
      crumb.itemType = BREADCRUMB;
      crumb.facetType = TEXT;
      crumb.description = term;
      crumb.facet = term;
      if (StringUtils.contains(term, ":")) {
        FacetType type = safeValueOf(StringUtils.substringBefore(term, ":"));
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
        FacetType type = safeValueOf(facet.name);
        if (type != null) {
          FacetItem item = new FacetItem();
          item.itemType = type == selectedFacet ? CATEGORY_OPENED : CATEGORY;
          item.facetType = type;
          item.facet = facet.name;
          facetList.add(item);
          if (type == selectedFacet) {
            for (Field field : facet.fields) {
              item = new FacetItem();
              item.facetType = type;
              item.label = field.label;
              item.facet = facet.name + ":" + field.label;
              item.itemType = ArrayUtils.contains(terms, item.facet) ? ITEM_SELECTED
                : ITEM;
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
    return UriHelper.createPortalSearchUrl(terms);
  }

  public boolean hasFacetsSelected() {
    return (terms.length > 1);
  }

  public String getFacetString() {
    if (hasFacetsSelected()) {
      StringBuilder sb = new StringBuilder(256);
      for (int i = 1; i < terms.length; i++) {
        // skipping 0 on purpose, is the search term 'query'
        if (sb.length() > 0) {
          sb.append("&");
        }
        sb.append("qf=").append(terms[i]);
      }
      return sb.toString();
    }
    return null;
  }

  @Subscribe
  public synchronized void onSearchItemsFinishedEvent(SearchItemsLoadedEvent event) {
    if (event.results != null) {
      totalResults = event.results.totalResults;
      for (Item item : event.results.items) {
        item.backgroundColor.set(0);
        searchItems.add(item);
        if ((item.edmPreview != null) && (item.edmPreview.length > 0)) {
          itemMap.put(item.edmPreview[0], item);
        }
      }
      searchItems.addAll(event.results.items);
    }
  }

  @Subscribe
  public synchronized void onSearchFacetFinish(SearchFacetsLoadedEvent event) {
    if (event.result != null) {
      facets.clear();
      facets.addAll(event.result.facets);
    }
  }

  public synchronized List<Item> getSearchItems() {
    return searchItems;
  }

  public synchronized Item getItemByImageUrl(String url) {
    if (itemMap.containsKey(url)) {
      return itemMap.get(url);
    }
    return null;
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
    if (terms.length > 0) {
      title = terms[0];
      if (StringUtils.contains(title, ":")) {
        title = StringUtils.substringAfter(title, ":");
      }
      title = WordUtils.capitalizeFully(title);
    }
    return title;
  }

}
