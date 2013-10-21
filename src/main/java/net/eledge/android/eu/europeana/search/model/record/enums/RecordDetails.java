package net.eledge.android.eu.europeana.search.model.record.enums;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.eledge.android.eu.europeana.EuropeanaApplication;
import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.search.model.record.RecordObject;
import net.eledge.android.eu.europeana.search.model.record.abstracts.RecordView;
import net.eledge.android.eu.europeana.search.model.record.abstracts.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public enum RecordDetails implements RecordView {


    TITLE {
        @Override
        public boolean isVisible(RecordObject record) {
            return true;
        }

        @Override
        public View getView(RecordObject record, ViewGroup parent, LayoutInflater inflater, EuropeanaApplication application) {
            View view = inflater.inflate(R.layout.listitem_record_title, parent, false);

            TextView text1 = (TextView) view.findViewById(android.R.id.text1);
            text1.setText(getLabel());
            TextView text2 = (TextView) view.findViewById(android.R.id.text2);
            text2.setText(cleanCombineResults(getValues(record, application)));

            TextView icon1 = (TextView) view.findViewById(android.R.id.icon1);
            icon1.setTypeface(application.getEuropeanaFont());
            icon1.setText(record.type.icon);
            return view;
        }

        @Override
        public int getLabel() {
            return R.string.record_field_title;
        }

        @Override
        public String[] getValues(RecordObject record, EuropeanaApplication application) {
            return record.title;
        }
    },
    //  TODO?  addFieldMap(fieldMap, Field.DCTERMS_ALTERNATIVE, shortcut.getList("DctermsAlternative"));

    //    addFieldMap(fieldMap, Field.DC_DESCRIPTION,
    //                shortcut.getList("DcDescription"),
    //       TODO       map(Field.DCTERMS_TABLEOFCONTENTS, shortcut.getList("DctermsTableOfContents"))
    //            );
    DCDESCRIPTION {
        @Override
        public boolean isVisible(RecordObject record) {
            return !areAllBlank(record.proxy.dcDescription);
        }

        @Override
        public int getLabel() {
            return R.string.record_field_dc_description;
        }

        @Override
        public String[] getValues(RecordObject record, EuropeanaApplication application) {
            return Resource.getPreferred(record.proxy.dcDescription, application.getLocale());
        }
    },
    DCCREATOR {
        @Override
        public boolean isVisible(RecordObject record) {
            return !areAllBlank(record.proxy.dcCreator);
        }

        @Override
        public int getLabel() {
            return R.string.record_field_dc_creator;
        }

        @Override
        public String[] getValues(RecordObject record, EuropeanaApplication application) {
            return Resource.getPreferred(record.proxy.dcCreator, application.getLocale());
        }
    },
    DCCONTRIBUTOR {
        @Override
        public boolean isVisible(RecordObject record) {
            return !areAllBlank(record.proxy.dcContributor);
        }

        @Override
        public int getLabel() {
            return R.string.record_field_dc_contributor;
        }

        @Override
        public String[] getValues(RecordObject record, EuropeanaApplication application) {
            return Resource.getPreferred(record.proxy.dcContributor, application.getLocale());
        }
    },
    // TODO   addFieldMap(fieldMap, Field.DC_COVERAGE, shortcut.getList("DcCoverage"));
    // TODO   addFieldMap(fieldMap, Field.DCTERMS_SPATIAL, shortcut.getList("DctermsSpatial"));
    DCDATE {
        @Override
        public boolean isVisible(RecordObject record) {
            return !areAllBlank(record.proxy.dcDate);
        }

        @Override
        public int getLabel() {
            return R.string.record_field_dc_date;
        }

        @Override
        public String[] getValues(RecordObject record, EuropeanaApplication application) {
            return Resource.getPreferred(record.proxy.dcDate, application.getLocale());
        }
    },
    // TODO   addFieldMap(fieldMap, Field.DCTERMS_TEMPORAL, shortcut.getList("DctermsTemporal"));
    DCTERMSISSUED {
        @Override
        public boolean isVisible(RecordObject record) {
            return !areAllBlank(record.proxy.dctermsIssued);
        }

        @Override
        public int getLabel() {
            return R.string.record_field_dcterms_issued;
        }

        @Override
        public String[] getValues(RecordObject record, EuropeanaApplication application) {
            return Resource.getPreferred(record.proxy.dctermsIssued, application.getLocale());
        }
    },
    DCTERMSCREATED {
        @Override
        public boolean isVisible(RecordObject record) {
            return !areAllBlank(record.proxy.dctermsCreated);
        }

        @Override
        public int getLabel() {
            return R.string.record_field_dcterms_created;
        }

        @Override
        public String[] getValues(RecordObject record, EuropeanaApplication application) {
            return Resource.getPreferred(record.proxy.dctermsCreated, application.getLocale());
        }
    },
    DCTYPE {
        @Override
        public boolean isVisible(RecordObject record) {
            return !areAllBlank(record.proxy.dcType);
        }

        @Override
        public int getLabel() {
            return R.string.record_field_dc_type;
        }

        @Override
        public String[] getValues(RecordObject record, EuropeanaApplication application) {
            return Resource.getPreferred(record.proxy.dcType, application.getLocale());
        }
    },
    DCFORMAT {
        @Override
        public boolean isVisible(RecordObject record) {
            return !areAllBlank(record.proxy.dcFormat, record.proxy.dctermsExtent, record.proxy.dctermsMedium);
        }

        @Override
        public int getLabel() {
            return R.string.record_field_dc_format;
        }

        @Override
        public String[] getValues(RecordObject record, EuropeanaApplication application) {
            String[] items = Resource.mergeArray(Resource.getPreferred(record.proxy.dcFormat, application.getLocale()), Resource.getPreferred(record.proxy.dctermsExtent, application.getLocale()));
            items = Resource.mergeArray(items, Resource.getPreferred(record.proxy.dctermsMedium, application.getLocale()));
            return items;
        }
    },
    DCSUBJECT {
        @Override
        public boolean isVisible(RecordObject record) {
            return !areAllBlank(record.proxy.dcSubject);
        }

        @Override
        public int getLabel() {
            return R.string.record_field_dc_subject;
        }

        @Override
        public String[] getValues(RecordObject record, EuropeanaApplication application) {
            return Resource.getPreferred(record.proxy.dcSubject, application.getLocale());
        }
    },
    DCIDENTIFIER {
        @Override
        public boolean isVisible(RecordObject record) {
            return !areAllBlank(record.proxy.dcIdentifier);
        }

        @Override
        public int getLabel() {
            return R.string.record_field_dc_identifier;
        }

        @Override
        public String[] getValues(RecordObject record, EuropeanaApplication application) {
            return Resource.getPreferred(record.proxy.dcIdentifier, application.getLocale());
        }
    },
    //  TODO  addFieldMap(fieldMap, Field.DC_RELATION,
    //                shortcut.getList("DcRelation"),
    //    map(Field.DCTERMS_REFERENCES, shortcut.getList("DctermsReferences")),
    //    map(Field.DCTERMS_ISREFERENCEDBY, shortcut.getList("DctermsIsReferencedBy")),
    //    map(Field.DCTERMS_ISREPLACEDBY, shortcut.getList("DctermsIsReplacedBy")),
    //    map(Field.DCTERMS_ISREQUIREDBY, shortcut.getList("DctermsIsRequiredBy")),
    //    map(Field.DCTERMS_REPLACES, shortcut.getList("DctermsReplaces")),
    //    map(Field.DCTERMS_REQUIRES, shortcut.getList("DctermsRequires")),
    //    map(Field.DCTERMS_ISVERSIONOF, shortcut.getList("DctermsIsVersionOf")),
    //    map(Field.DCTERMS_HASVERSION, shortcut.getList("DctermsHasVersion")),
    //    map(Field.DCTERMS_CONFORMSTO, shortcut.getList("DctermsConformsTo")),
    //    map(Field.DCTERMS_HASFORMAT, shortcut.getList("DctermsHasFormat")),
    //    map(Field.DCTERMS_ISFORMATOF, shortcut.getList("DctermsIsFormatOf")),
    //    map(Field.EDM_CURRENTLOCATION, shortcut.getList("edm:currentLocation")),
    //    map(Field.EDM_HASMET, shortcut.getList("EdmHasMet")),
    //    map(Field.EDM_HASTYPE, shortcut.getList("EdmHasType")),
    //    map(Field.EDM_INCORPORATES, shortcut.getList("EdmIncorporates")),
    //    map(Field.EDM_ISDERIVATIVEOF, shortcut.getList("EdmIsDerivativeOf")),
    //    map(Field.EDM_ISRELATEDTO, shortcut.getList("EdmIsRelatedTo")),
    //    map(Field.EDM_ISREPRESENTATIONOF, shortcut.getList("EdmIsRepresentationOf")),
    //    map(Field.EDM_ISSIMILARTO, shortcut.getList("EdmIsSimilarTo")),
    //    map(Field.EDM_ISSUCCESSOROF, shortcut.getList("EdmIsSuccessorOf")),
    //    map(Field.EDM_REALIZES, shortcut.getList("EdmRealizes"))
    //            );
    DCTERMSISPARTOF {
        @Override
        public boolean isVisible(RecordObject record) {
            return !areAllBlank(record.proxy.dctermsIsPartOf);
        }

        @Override
        public int getLabel() {
            return R.string.record_field_dcterms_ispartof;
        }

        @Override
        public String[] getValues(RecordObject record, EuropeanaApplication application) {
            return Resource.getPreferred(record.proxy.dctermsIsPartOf, application.getLocale());
        }
    },
    //  TODO  addFieldMap(fieldMap, Field.DCTERMS_HASPART, shortcut.getList("DctermsHasPart"));
    //  TODO  addFieldMap(fieldMap, Field.EDM_ISNEXTINSEQUENCE, shortcut.getList("EdmIsNextInSequence"));
    //  TODO  addFieldMap(fieldMap, Field.DC_LANGUAGE, shortcut.getList("DcLanguage"));
    DCRIGHTS {
        @Override
        public boolean isVisible(RecordObject record) {
            return !areAllBlank(record.proxy.dcRights);
        }

        @Override
        public int getLabel() {
            return R.string.record_field_dc_rights;
        }

        @Override
        public String[] getValues(RecordObject record, EuropeanaApplication application) {
            return Resource.getPreferred(record.proxy.dcRights, application.getLocale());
        }
    },
    //  TODO  addFieldMap(fieldMap, Field.DCTERMS_PROVENANCE, shortcut.getList("DctermsProvenance"));
    //  TODO  addFieldMap(fieldMap, Field.DC_PUBLISHER, shortcut.getList("DcPublisher"));
    DCSOURCE {
        @Override
        public boolean isVisible(RecordObject record) {
            return !areAllBlank(record.proxy.dcSource);
        }

        @Override
        public int getLabel() {
            return R.string.record_field_dc_source;
        }

        @Override
        public String[] getValues(RecordObject record, EuropeanaApplication application) {
            return Resource.getPreferred(record.proxy.dcSource, application.getLocale());
        }
    },
    EDMDATAPROVIDER {
        @Override
        public boolean isVisible(RecordObject record) {
            return !areAllBlank(record.aggregation.edmDataProvider);
        }

        @Override
        public int getLabel() {
            return R.string.record_field_edm_dataprovider;
        }

        @Override
        public String[] getValues(RecordObject record, EuropeanaApplication application) {
            return Resource.getPreferred(record.aggregation.edmDataProvider, application.getLocale());
        }
    },
    EDMPROVIDER {
        @Override
        public boolean isVisible(RecordObject record) {
            return !areAllBlank(record.aggregation.edmProvider);
        }

        @Override
        public int getLabel() {
            return R.string.record_field_edm_provider;
        }

        @Override
        public String[] getValues(RecordObject record, EuropeanaApplication application) {
            return Resource.getPreferred(record.aggregation.edmProvider, application.getLocale());
        }
    },
    EDMCOUNTRY {
        @Override
        public boolean isVisible(RecordObject record) {
            return !areAllBlank(record.europeanaAggregation.edmCountry);
        }

        @Override
        public int getLabel() {
            return R.string.record_field_edm_country;
        }

        @Override
        public String[] getValues(RecordObject record, EuropeanaApplication application) {
            return Resource.getPreferred(record.europeanaAggregation.edmCountry, application.getLocale());
        }
    };

    @Override
    public View getView(RecordObject record, ViewGroup parent, LayoutInflater inflater, EuropeanaApplication application) {
        return drawDetailView(getLabel(), getValues(record, application), parent, inflater);
    }

    protected View drawDetailView(int titleResId, String[] values, ViewGroup parent, LayoutInflater inflater) {
        return drawDetailView(titleResId, cleanCombineResults(values), parent, inflater);
    }

    protected View drawDetailView(int titleResId, String value, ViewGroup parent, LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.listitem_record_detail, parent, false);
        TextView textTitle = (TextView) view.findViewById(android.R.id.text1);
        TextView textValue = (TextView) view.findViewById(android.R.id.text2);
        textTitle.setText(titleResId);
        textValue.setText(value);
        return view;
    }

    private static boolean areAllBlank(Map<?, ?>... maps) {
        boolean allBlank = true;
        for (Map<?, ?> map : maps) {
            allBlank &= CollectionUtils.isEmpty(map);
        }
        return allBlank;
    }

    private static String cleanCombineResults(String[] array) {
        List<String> result = new ArrayList<String>();
        for (String s : array) {
            if (StringUtils.isNotBlank(s) && !StringUtils.startsWithIgnoreCase(s, "http://")
                    && !StringUtils.startsWithIgnoreCase(s, "https://")) {
                result.add(StringUtils.trim(s));
            }
        }
        return StringUtils.join(result, "; ");
    }

    public static List<RecordDetails> getVisibles(RecordObject record) {
        List<RecordDetails> list = new ArrayList<RecordDetails>();
        if (record != null) {
            for (RecordDetails detail : RecordDetails.values()) {
                if (detail.isVisible(record)) {
                    list.add(detail);
                }
            }
        }
        return list;
    }

}
