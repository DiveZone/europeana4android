package net.eledge.android.eu.europeana.search.model.record.abstracts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.eledge.android.eu.europeana.EuropeanaApplication;
import net.eledge.android.eu.europeana.search.model.record.Record;

public interface RecordView {

	boolean isVisible(Record record);
	String getSeeMore();
	View getView(Record record, ViewGroup parent, LayoutInflater inflater, EuropeanaApplication application);
	
}