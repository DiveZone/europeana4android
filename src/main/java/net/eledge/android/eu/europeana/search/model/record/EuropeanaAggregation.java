package net.eledge.android.eu.europeana.search.model.record;

import net.eledge.android.eu.europeana.search.model.record.abstracts.Resource;

import java.util.Map;

public class EuropeanaAggregation extends Resource {

    public String edmLandingPage;

    public String edmPreview;

    public Map<String, String[]> edmCountry;

    public Map<String, String[]> edmLanguage;

}
