package net.eledge.android.eu.europeana.search.model.record;

import net.eledge.android.eu.europeana.search.model.record.abstracts.Resource;

import java.util.Map;

public class Place extends Resource {

    public Map<String, String[]> prefLabel;

    public Double latitude;

    public Double longitude;

    public static void normalize(RecordObject object) {
        Place merged = new Place();
        if (object.places != null) {
            for (Place source: object.places) {
                merged.prefLabel = mergeMapArrays(merged.prefLabel, source.prefLabel);
                merged.latitude = defaultValue(merged.latitude, source.latitude);
                merged.longitude = defaultValue(merged.longitude, source.longitude);
            }
        }
        object.place = merged;
    }

}
