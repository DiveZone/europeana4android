package net.eledge.android.eu.europeana.search.listeners;

import net.eledge.android.eu.europeana.search.model.Suggestion;

public interface SuggestionTaskListener {

	public void onSuggestionFinish(Suggestion[] suggestions);
	
}
