package net.eledge.android.eu.europeana.gui.fragments;

import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.gui.adaptor.ResultAdaptor;
import net.eledge.android.eu.europeana.search.SearchController;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

public class SearchResultsFragment extends Fragment {
	
	private ResultAdaptor mResultAdaptor;
	
	private GridView mGridview;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mResultAdaptor = new ResultAdaptor(this.getActivity(), SearchController.getInstance().getSearchItems());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = (ViewGroup) inflater.inflate(R.layout.fragment_search_results, null);
		mGridview = (GridView) root.findViewById(R.id.fragment_search_gridview);
		mGridview.setAdapter(mResultAdaptor);
		return root;
	}
	
	@Override
	public void onStart() {
		super.onStart();
	}


}
