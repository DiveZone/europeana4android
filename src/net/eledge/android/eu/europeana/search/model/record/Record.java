package net.eledge.android.eu.europeana.search.model.record;

import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.search.model.enums.DocType;
import net.eledge.android.eu.europeana.search.model.record.annotation.RecordDisplay;
import net.eledge.android.toolkit.json.annotations.JsonField;

public class Record {
	
	@RecordDisplay(R.string.record_field_title)
	@JsonField
	public String[] title;
	
	@JsonField(enumMethod="safeValueOf")
	public DocType type;
	
	@RecordDisplay(R.string.record_field_dc_creator)
	@JsonField("proxies[].dcCreator.def")
	public String[] dcCreator;
	
	@JsonField("proxies[].dcSubject.def")
	public String[] dcSubject;
	
	@JsonField("europeanaAggregation.edmPreview")
	public String edmPreview;
	
	@JsonField("aggregations[].edmIsShownBy")
	public String edmIsShownBy;
	
	@JsonField("aggregations[].edmIsShownAt")
	public String edmIsShownAt;

}
