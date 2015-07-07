package net.eledge.android.europeana.gui.adapter.events;

import net.eledge.android.europeana.search.model.suggestion.Suggestion;

public class SuggestionClicked {

    public final Suggestion suggestion;

    public SuggestionClicked(Suggestion suggestion) {
        this.suggestion = suggestion;
    }
}
