package net.eledge.android.eu.europeana.search.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.eledge.android.eu.europeana.search.model.suggestion.Item;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Suggestions {

    public Item[] items;

}
