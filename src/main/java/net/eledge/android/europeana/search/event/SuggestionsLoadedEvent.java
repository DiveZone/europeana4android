package net.eledge.android.europeana.search.event;

import net.eledge.android.europeana.search.model.suggestion.Suggestion;

public class SuggestionsLoadedEvent {

    public final Suggestion[] suggestions;

    public SuggestionsLoadedEvent(Suggestion[] suggestions) {
        this.suggestions = suggestions;
    }
}
