package net.eledge.android.eu.europeana.search.model.record.enums;

import java.util.ArrayList;
import java.util.List;

import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.search.model.record.Record;
import net.eledge.android.eu.europeana.search.model.record.abstracts.RecordView;
import net.eledge.android.toolkit.StringArrayUtils;
import net.eledge.android.toolkit.StringUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public enum RecordDetails implements RecordView {
	
	TITLE {
		@Override
		public boolean draw(Record record) {
			return true;
		}
		@Override
		public View getView(Record record, ViewGroup parent, LayoutInflater inflater) {
			View view = inflater.inflate(R.layout.listitem_record_title, parent, false);
			TextView textTitle = (TextView) view.findViewById(android.R.id.text1);
			textTitle.setText(StringUtils.joinWithSeperator(";", record.title));
			return view;
		}
	},
	DCCREATOR {
		@Override
		public boolean draw(Record record) {
			return StringArrayUtils.isNotBlank(record.dcCreator);
		}
		@Override
		public View getView(Record record, ViewGroup parent, LayoutInflater inflater) {
			return drawDetailView(R.string.record_field_dc_creator, record.dcCreator, parent, inflater);
		}
	};
	
	protected View drawDetailView(int titleResId, String[] values, ViewGroup parent, LayoutInflater inflater) {
		return drawDetailView(titleResId, StringUtils.joinWithSeperator(";", values), parent, inflater);
	}
	
	protected View drawDetailView(int titleResId, String value, ViewGroup parent, LayoutInflater inflater) {
		View view = inflater.inflate(R.layout.listitem_record_detail, parent, false);
		TextView textTitle = (TextView) view.findViewById(android.R.id.text1);
		TextView textValue = (TextView) view.findViewById(android.R.id.text2);
		textTitle.setText(titleResId);
		textValue.setText(value);
		return view;
	}
	
	public static List<RecordDetails> getVisibles(Record record) {
		List<RecordDetails> list = new ArrayList<RecordDetails>();
		if (record != null) {
			for (RecordDetails detail: RecordDetails.values()) {
				list.add(detail);
			}
		}
		return list;
	}
	
}
