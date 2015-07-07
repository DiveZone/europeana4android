package net.eledge.android.europeana.search.event;

import net.eledge.android.europeana.search.model.SearchFacets;

public class SearchFacetsLoadedEvent {

    public final SearchFacets result;

    public SearchFacetsLoadedEvent(SearchFacets result) {
        this.result = result;
    }
}
