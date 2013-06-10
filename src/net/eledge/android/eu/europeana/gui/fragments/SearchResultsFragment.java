package net.eledge.android.eu.europeana.gui.fragments;

import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.gui.adaptor.ResultAdaptor;
import net.eledge.android.eu.europeana.search.SearchController;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SearchResultsFragment extends ListFragment {
	
	private ResultAdaptor mResultAdaptor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mResultAdaptor = new ResultAdaptor(this.getActivity(), SearchController.getInstance().getSearchItems());
		setListAdapter(mResultAdaptor);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = (ViewGroup) inflater.inflate(R.layout.fragment_search_results, null);
		return root;
	}
	
	

}
