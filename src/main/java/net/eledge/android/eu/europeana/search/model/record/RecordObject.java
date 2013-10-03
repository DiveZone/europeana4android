package net.eledge.android.eu.europeana.search.model.record;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.eledge.android.eu.europeana.search.model.enums.DocType;
import net.eledge.android.eu.europeana.search.model.record.abstracts.Resource;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RecordObject extends Resource {

    public DocType type;

    public String[] year;

    public String[] title;

    @JsonIgnore
    public Proxy proxy;

    public Proxy[] proxies;

    @JsonIgnore
    public Aggregation aggregation;

    public Aggregation[] aggregations;

    @JsonIgnore
    public Place place;

    public Place[] places;

    public EuropeanaAggregation europeanaAggregation;


    public void setType(String type) {
        this.type = DocType.safeValueOf(type);
    }

    public static RecordObject normalize(RecordObject object) {
        Proxy.normalize(object);
        Aggregation.normalize(object);
        Place.normalize(object);
        return object;
    }

}
