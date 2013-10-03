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

import java.util.ArrayList;
import java.util.List;

public enum RecordDetails implements RecordView {
	
	TITLE {
		@Override
		public boolean isVisible(RecordObject record) {
			return true;
		}
		@Override
		public String getSeeMore() {
			return null;
		}
		@Override
		public View getView(RecordObject record, ViewGroup parent, LayoutInflater inflater, EuropeanaApplication application) {
			View view = inflater.inflate(R.layout.listitem_record_title, parent, false);
			
			TextView text1 = (TextView) view.findViewById(android.R.id.text1);
			text1.setText(R.string.record_field_title);
			TextView text2 = (TextView) view.findViewById(android.R.id.text2);
			text2.setText(StringUtils.join(record.title, ";"));
			
			TextView icon1 = (TextView) view.findViewById(android.R.id.icon1);
			icon1.setTypeface(application.getEuropeanaFont());
			icon1.setText(record.type.icon);
			return view;
		}
	},
	DCDESCRIPTION {
		@Override
		public boolean isVisible(RecordObject record) {
			return (record.proxy.dcDescription != null) && !record.proxy.dcDescription.isEmpty();
		}
		@Override
		public String getSeeMore() {
			return null;
		}
		@Override
		public View getView(RecordObject record, ViewGroup parent, LayoutInflater inflater, EuropeanaApplication application) {
			return drawDetailView(R.string.record_field_dc_description, Resource.getPreferred(record.proxy.dcDescription, application.getLocale()), parent, inflater);
		}
	},
	DCCREATOR {
		@Override
		public boolean isVisible(RecordObject record) {
            return (record.proxy.dcCreator != null) && !record.proxy.dcCreator.isEmpty();
		}
		@Override
		public String getSeeMore() {
			return "who";
		}
		@Override
		public View getView(RecordObject record, ViewGroup parent, LayoutInflater inflater, EuropeanaApplication application) {
			return drawDetailView(R.string.record_field_dc_creator, Resource.getPreferred(record.proxy.dcCreator, application.getLocale()), parent, inflater);
		}
	},
	DCTYPE {
		@Override
		public boolean isVisible(RecordObject record) {
            return (record.proxy.dcType != null) && !record.proxy.dcType.isEmpty();
		}
		@Override
		public String getSeeMore() {
			return null;
		}
		@Override
		public View getView(RecordObject record, ViewGroup parent, LayoutInflater inflater, EuropeanaApplication application) {
			return drawDetailView(R.string.record_field_dc_type, Resource.getPreferred(record.proxy.dcType, application.getLocale()), parent, inflater);
		}
	},
	DCSUBJECT {
		@Override
		public boolean isVisible(RecordObject record) {
            return (record.proxy.dcSubject != null) && !record.proxy.dcSubject.isEmpty();
		}
		@Override
		public String getSeeMore() {
			return null;
		}
		@Override
		public View getView(RecordObject record, ViewGroup parent, LayoutInflater inflater, EuropeanaApplication application) {
			return drawDetailView(R.string.record_field_dc_subject, Resource.getPreferred(record.proxy.dcSubject, application.getLocale()), parent, inflater);
		}
	},
	DCIDENTIFIER {
		@Override
		public boolean isVisible(RecordObject record) {
            return (record.proxy.dcIdentifier != null) && !record.proxy.dcIdentifier.isEmpty();
		}
		@Override
		public String getSeeMore() {
			return null;
		}
		@Override
		public View getView(RecordObject record, ViewGroup parent, LayoutInflater inflater, EuropeanaApplication application) {
			return drawDetailView(R.string.record_field_dc_identifier, Resource.getPreferred(record.proxy.dcIdentifier, application.getLocale()), parent, inflater);
		}
	},
	EDMDATAPROVIDER {
		@Override
		public boolean isVisible(RecordObject record) {
            return (record.aggregation.edmDataProvider != null) && !record.aggregation.edmDataProvider.isEmpty();
		}
		@Override
		public String getSeeMore() {
			return "DATA_PROVIDER";
		}
		@Override
		public View getView(RecordObject record, ViewGroup parent, LayoutInflater inflater, EuropeanaApplication application) {
            return drawDetailView(R.string.record_field_edm_dataprovider, Resource.getPreferred(record.aggregation.edmDataProvider, application.getLocale()), parent, inflater);
		}
	},
	EDMPROVIDER {
		@Override
		public boolean isVisible(RecordObject record) {
            return (record.aggregation.edmProvider != null) && !record.aggregation.edmProvider.isEmpty();
		}
		@Override
		public String getSeeMore() {
			return "PROVIDER";
		}
		@Override
		public View getView(RecordObject record, ViewGroup parent, LayoutInflater inflater, EuropeanaApplication application) {
            return drawDetailView(R.string.record_field_edm_provider, Resource.getPreferred(record.aggregation.edmProvider, application.getLocale()), parent, inflater);
		}
	},
    EDMCOUNTRY {
        @Override
        public boolean isVisible(RecordObject record) {
            return (record.europeanaAggregation.edmCountry != null) && !record.europeanaAggregation.edmCountry.isEmpty();
        }
        @Override
        public String getSeeMore() {
            return null;
        }
        @Override
        public View getView(RecordObject record, ViewGroup parent, LayoutInflater inflater, EuropeanaApplication application) {
            return drawDetailView(R.string.record_field_edm_country, Resource.getPreferred(record.europeanaAggregation.edmCountry, application.getLocale()), parent, inflater);
        }
    };
	
	protected int getDetailView() {
		return getSeeMore() != null ? R.layout.listitem_record_detail_seealso : R.layout.listitem_record_detail;
	}
	
	protected View drawDetailView(int titleResId, String[] values, ViewGroup parent, LayoutInflater inflater) {
		return drawDetailView(titleResId, StringUtils.join(values, ";"), parent, inflater);
	}
	
	protected View drawDetailView(int titleResId, String value, ViewGroup parent, LayoutInflater inflater) {
		View view = inflater.inflate(getDetailView(), parent, false);
		TextView textTitle = (TextView) view.findViewById(android.R.id.text1);
		TextView textValue = (TextView) view.findViewById(android.R.id.text2);
		textTitle.setText(titleResId);
		textValue.setText(value);
		return view;
	}
	
//	protected void addSeeAlsoFields(View view, String[] values, LayoutInflater inflater) {
//		LinearLayout holder = (LinearLayout) view.findViewById(R.id.listitem_record_detail_seealso_list);
//		if ( (getSeeMore() != null) && (holder != null)) {
//			for (String value: values) {
//				TextView tv = (TextView) inflater.inflate(R.layout.listitem_record_seealso, null);
//				tv.setText(value);
//				holder.addView(tv);
//			}
//		}
//	}
//	
	public static List<RecordDetails> getVisibles(RecordObject record) {
		List<RecordDetails> list = new ArrayList<RecordDetails>();
		if (record != null) {
			for (RecordDetails detail: RecordDetails.values()) {
				if (detail.isVisible(record)) {
					list.add(detail);
				}
			}
		}
		return list;
	}
	
}
