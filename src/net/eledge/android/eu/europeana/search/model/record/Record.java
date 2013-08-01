package net.eledge.android.eu.europeana.search.model.record;

import net.eledge.android.eu.europeana.search.model.enums.DocType;
import net.eledge.android.toolkit.json.annotations.JsonField;

public class Record {
	
	@JsonField
	public String title = "Invalid title...";
	
	@JsonField
	public DocType type;
	
	@JsonField("proxies[].dcCreator.def")
	public String[] dcCreator;
	
	@JsonField("proxies[].dcSubject.def")
	public String[] dcSubject;
	
	@JsonField("europeanaAggregation[].edmPreview")
	public String edmPreview;
	
	@JsonField("aggregations[].edmIsShownBy")
	public String edmIsShownBy;
	
	@JsonField("aggregations[].edmIsShownAt")
	public String edmIsShownAt;

}
