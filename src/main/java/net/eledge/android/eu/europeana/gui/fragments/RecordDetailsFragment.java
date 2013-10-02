package net.eledge.android.eu.europeana.gui.fragments;

import java.util.ArrayList;

import net.eledge.android.eu.europeana.EuropeanaApplication;
import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.gui.adapter.RecordViewAdapter;
import net.eledge.android.eu.europeana.search.RecordController;
import net.eledge.android.eu.europeana.search.model.record.Record;
import net.eledge.android.eu.europeana.search.model.record.abstracts.RecordView;
import net.eledge.android.eu.europeana.search.model.record.enums.RecordDetails;
import net.eledge.android.toolkit.async.listener.TaskListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class RecordDetailsFragment extends Fragment implements TaskListener<Record> {

	// Controller
	private RecordController recordController = RecordController._instance;

	private ListView mListView;
	
	private RecordViewAdapter mRecordViewAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		recordController.registerListener(RecordDetailsFragment.class, this);
		mRecordViewAdapter = new RecordViewAdapter((EuropeanaApplication) this.getActivity().getApplication(),
				this.getActivity(), new ArrayList<RecordView>());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = (View) inflater.inflate(R.layout.fragment_record_details, null);
		mListView = (ListView) root.findViewById(R.id.fragment_record_details_listview);
		mListView.setAdapter(mRecordViewAdapter);
		return root;
	}
	
	@Override
	public void onResume() {
		if (recordController.record != null) {
			onTaskFinished(recordController.record);
		}
		super.onResume();
	}
	
	@Override
	public void onDestroy() {
		recordController.unregister(RecordDetailsFragment.class);
		super.onDestroy();
	}
	
	@Override
	public void onTaskFinished(final Record record) {
		mRecordViewAdapter.clear();
		for (RecordDetails detail: RecordDetails.getVisibles(record)) {
			mRecordViewAdapter.add(detail);
		}
		mRecordViewAdapter.notifyDataSetChanged();
	}

}
