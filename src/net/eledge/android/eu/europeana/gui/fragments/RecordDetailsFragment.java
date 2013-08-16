package net.eledge.android.eu.europeana.gui.fragments;

import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.search.RecordController;
import net.eledge.android.eu.europeana.search.model.record.Record;
import net.eledge.android.toolkit.async.listener.TaskListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RecordDetailsFragment extends Fragment implements TaskListener<Record> {

	// Controller
	private RecordController recordController = RecordController.instance;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		recordController.registerListener(RecordDetailsFragment.class.getName(), this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = (View) inflater.inflate(R.layout.fragment_record_details, null);

		return root;
	}

	@Override
	public void onTaskFinished(Record result) {
		// TODO Auto-generated method stub

	}

}
