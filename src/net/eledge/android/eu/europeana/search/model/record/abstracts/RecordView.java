package net.eledge.android.eu.europeana.search.model.record.abstracts;

import net.eledge.android.eu.europeana.search.model.record.Record;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface RecordView {

	boolean isVisible(Record record);
	View getView(Record record, ViewGroup parent, LayoutInflater inflater);
	
}