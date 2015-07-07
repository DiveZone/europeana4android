package net.eledge.android.europeana.search.event;

import net.eledge.android.europeana.search.model.SearchItems;

public class SearchItemsLoadedEvent {

    public final SearchItems results;

    public SearchItemsLoadedEvent(SearchItems results) {
        this.results = results;
    }

}
