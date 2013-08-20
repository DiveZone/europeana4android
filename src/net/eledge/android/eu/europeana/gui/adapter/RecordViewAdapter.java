package net.eledge.android.eu.europeana.gui.adapter;

import java.util.List;

import net.eledge.android.eu.europeana.EuropeanaApplication;
import net.eledge.android.eu.europeana.search.RecordController;
import net.eledge.android.eu.europeana.search.model.record.abstracts.RecordView;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class RecordViewAdapter extends ArrayAdapter<RecordView> {

	private final LayoutInflater inflater;
	
	private final Typeface europeanaFont;
	
	private final RecordController recordController = RecordController._instance;
	
	public RecordViewAdapter(EuropeanaApplication application, Context context, List<RecordView> items) {
		super(context, 0, items);
		this.europeanaFont = application.getEuropeanaFont();
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (recordController.record != null) {
			RecordView recordView = getItem(position);
			return recordView.getView(recordController.record, parent, inflater);
		}
		return null;
	}
	
}
