package net.eledge.android.eu.europeana.search.model;

import net.eledge.android.eu.europeana.search.model.searchresults.Item;

import java.util.ArrayList;
import java.util.List;

public class SearchItems {

    public boolean success;

	public Integer totalResults;

	public List<Item> items = new ArrayList<Item>();

}
