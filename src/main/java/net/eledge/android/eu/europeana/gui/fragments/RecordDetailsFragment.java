package net.eledge.android.eu.europeana.gui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import net.eledge.android.eu.europeana.EuropeanaApplication;
import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.gui.adapter.RecordViewAdapter;
import net.eledge.android.eu.europeana.search.RecordController;
import net.eledge.android.eu.europeana.search.model.record.RecordObject;
import net.eledge.android.eu.europeana.search.model.record.abstracts.RecordView;
import net.eledge.android.eu.europeana.search.model.record.enums.RecordDetails;
import net.eledge.android.toolkit.async.listener.TaskListener;

import java.util.ArrayList;

public class RecordDetailsFragment extends Fragment implements TaskListener<RecordObject> {

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
		View root = inflater.inflate(R.layout.fragment_record_details, null);
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
    public void onTaskStart() {
        // ignore
    }

	@Override
	public void onTaskFinished(final RecordObject record) {
		mRecordViewAdapter.clear();
		for (RecordDetails detail: RecordDetails.getVisibles(record)) {
			mRecordViewAdapter.add(detail);
		}
		mRecordViewAdapter.notifyDataSetChanged();
	}

}
