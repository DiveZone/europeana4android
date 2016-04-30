package net.eledge.android.europeana.gui.adapter.events;

import net.eledge.android.europeana.search.model.searchresults.Item;

public class SearchItemClicked {

    public int position;

    public final Item item;

    public SearchItemClicked(int position, Item item) {
        this.position = position;
        this.item = item;
    }
}
