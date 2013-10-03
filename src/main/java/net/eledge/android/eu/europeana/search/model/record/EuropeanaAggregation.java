package net.eledge.android.eu.europeana.search.model.record;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.eledge.android.eu.europeana.search.model.record.abstracts.Resource;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EuropeanaAggregation extends Resource {

    public String edmLandingPage;

    public String edmPreview;

    public Map<String, String[]> edmCountry;

    public Map<String, String[]> edmLanguage;

}
