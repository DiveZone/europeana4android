package net.eledge.android.eu.europeana.search.model.searchresults;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Facet {
	
	public String name;
	public List<Field> fields = new ArrayList<Field>();

}
