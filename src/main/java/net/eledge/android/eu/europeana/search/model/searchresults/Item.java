package net.eledge.android.eu.europeana.search.model.searchresults;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.eledge.android.eu.europeana.search.model.enums.DocType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Item {

	public String id;

	public String[] title = new String[]{"Invalid title..."};

	public DocType type;

	public String[] edmPreview;

    public void setType(String type) {
        this.type = DocType.safeValueOf(type);
    }
}
