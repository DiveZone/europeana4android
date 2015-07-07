package net.eledge.android.europeana.search.event;

public class SearchStartedEvent {

    public final boolean facetSearch;

    public SearchStartedEvent(boolean facetSearch) {
        this.facetSearch = facetSearch;
    }
}
