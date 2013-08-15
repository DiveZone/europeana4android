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
	
	@JsonField
	public String[] year;
	
	@RecordDisplay(R.string.record_field_dc_creator)
	@JsonField("proxies[].dcCreator.def")
	public String[] dcCreator;
	
	@JsonField("proxies[].dcSubject.def")
	public String[] dcSubject;
	
	@JsonField
	public String edmPreview;
	
	@JsonField
	public String edmIsShownBy;
	
	@JsonField
	public String edmIsShownAt;
	
	@JsonField("aggregations[].edmDataProvider.def")
	public String edmDataProvider;
	
	@JsonField("aggregations[].edmProvider.def")
	public String edmProvider;
	
//	@JsonField("aggregations[].webResources[].about")
//	public String[] webResources;
	
	@JsonField
	public long europeanaCompleteness;
	
	@JsonField
	public double longitude;

	@JsonField
	public double latitude;

}
