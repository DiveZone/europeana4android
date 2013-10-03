package net.eledge.android.eu.europeana.search.model.record;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.eledge.android.eu.europeana.search.model.record.abstracts.Resource;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Aggregation extends Resource {

    public Map<String, String[]> edmDataProvider;

    public String edmIsShownBy;

    public String edmIsShownAt;

    public String edmObject;

    public Map<String, String[]> edmProvider;

    public Map<String, String[]> edmRights;

    public Resource[] webResources;


    public static void normalize(RecordObject object) {
        Aggregation merged = new Aggregation();
        if (object.aggregations != null) {
            for (Aggregation source: object.aggregations) {
                merged.edmIsShownBy = defaultValue(merged.edmIsShownBy, source.edmIsShownBy);
                merged.edmIsShownAt = defaultValue(merged.edmIsShownAt, source.edmIsShownAt);
                merged.edmObject = defaultValue(merged.edmObject, source.edmObject);
                merged.edmDataProvider = mergeMapArrays(merged.edmDataProvider, source.edmDataProvider);
                merged.edmProvider = mergeMapArrays(merged.edmProvider, source.edmProvider);
                merged.edmRights = mergeMapArrays(merged.edmRights, source.edmRights);
                if (merged.webResources == null) {
                    merged.webResources = source.webResources;
                } else if (source.webResources != null) {
                    merged.webResources = mergeArray(merged.webResources, source.webResources);
                }
            }
        }
        object.aggregation = merged;
    }

}
