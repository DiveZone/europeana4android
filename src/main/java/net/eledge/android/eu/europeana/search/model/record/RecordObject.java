package net.eledge.android.eu.europeana.search.model.record;

import net.eledge.android.eu.europeana.search.model.enums.DocType;
import net.eledge.android.eu.europeana.search.model.record.abstracts.Resource;

public class RecordObject extends Resource {

    public DocType type;

    public String[] year;

    public String[] title;

    public transient Proxy proxy;

    public Proxy[] proxies;

    public transient Aggregation aggregation;

    public Aggregation[] aggregations;

    public transient Place place;

    public Place[] places;

    public EuropeanaAggregation europeanaAggregation;


    public void setType(String type) {
        this.type = DocType.safeValueOf(type);
    }

    public static RecordObject normalize(RecordObject object) {
        if (object != null) {
            Proxy.normalize(object);
            Aggregation.normalize(object);
            Place.normalize(object);
            EuropeanaAggregation.normalize(object);
        }
        return object;
    }

}
