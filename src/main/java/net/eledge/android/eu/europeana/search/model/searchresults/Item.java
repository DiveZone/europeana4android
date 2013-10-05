package net.eledge.android.eu.europeana.search.model.searchresults;

import net.eledge.android.eu.europeana.search.model.enums.DocType;

public class Item {

	public String id;

	public String[] title = new String[]{"Invalid title..."};

	public DocType type;

	public String[] edmPreview;

    public void setType(String type) {
        this.type = DocType.safeValueOf(type);
    }
}
