package net.eledge.android.eu.europeana.search.model.record;

import net.eledge.android.eu.europeana.search.model.enums.DocType;
import net.eledge.android.toolkit.json.annotations.JsonField;

public class Record {
	
	@JsonField
	public String[] title;
	
	@JsonField(enumMethod="safeValueOf")
	public DocType type;
	
	@JsonField
	public String[] year;
	
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
	public Long europeanaCompleteness;
	
	@JsonField
	public Double longitude;

	@JsonField
	public Double latitude;

}
