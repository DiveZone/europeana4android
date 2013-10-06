package net.eledge.android.eu.europeana.gui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import net.eledge.android.eu.europeana.EuropeanaApplication;
import net.eledge.android.eu.europeana.search.RecordController;
import net.eledge.android.eu.europeana.search.model.record.abstracts.RecordView;

import java.util.List;

public class RecordViewAdapter extends ArrayAdapter<RecordView> {

	
	private final RecordController recordController = RecordController._instance;

	private final LayoutInflater inflater;
	private final EuropeanaApplication application;
	
	public RecordViewAdapter(EuropeanaApplication application, Context context, List<RecordView> items) {
		super(context, 0, items);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.application = application;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (recordController.record != null) {
			RecordView recordView = getItem(position);
			return recordView.getView(recordController.record, parent, inflater, application);
		}
		return null;
	}
	
	@Override
	public boolean isEnabled(int position) {
		return false;
	}
	
}
