package net.eledge.android.eu.europeana.search.model.record;

import net.eledge.android.eu.europeana.search.model.enums.Right;
import net.eledge.android.eu.europeana.search.model.record.abstracts.Resource;
import net.eledge.android.toolkit.StringArrayUtils;

import java.util.Map;

public class EuropeanaAggregation extends Resource {

    public String edmLandingPage;

    public String edmPreview;

    public Map<String, String[]> edmCountry;

    public Map<String, String[]> edmLanguage;

    public Map<String, String[]> edmRights;

    public transient Right right;

    public static void normalize(RecordObject object) {
        String[] rights = Resource.getPreferred(object.europeanaAggregation.edmRights, "def");
        if (StringArrayUtils.isNotBlank(rights)) {
            object.europeanaAggregation.right = Right.safeValueByUrl(rights[0]);
        }
    }

}
