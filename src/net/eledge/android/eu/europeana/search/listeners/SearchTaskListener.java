package net.eledge.android.eu.europeana.search.listeners;

import net.eledge.android.eu.europeana.search.model.SearchResult;

public interface SearchTaskListener {
	
	public void onSearchStart();
	
	public void onSearchFinish(SearchResult results);
	
	public void onSearchError(String message);
	
}