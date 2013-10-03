package net.eledge.android.eu.europeana.search.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.eledge.android.eu.europeana.search.model.searchresults.Item;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchItems {

    public boolean success;

	public Integer totalResults;

	public List<Item> items = new ArrayList<Item>();

}
