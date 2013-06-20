package net.eledge.android.eu.europeana.gui.fragments;

import net.eledge.android.eu.europeana.EuropeanaApplication;
import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.gui.adaptor.ResultAdaptor;
import net.eledge.android.eu.europeana.search.SearchController;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;

public class SearchResultsFragment extends Fragment {
	
	private ResultAdaptor mResultAdaptor;
	
	private GridView mGridview;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mResultAdaptor = new ResultAdaptor((EuropeanaApplication)this.getActivity().getApplication(), this.getActivity(), SearchController.getInstance().getSearchItems());
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
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mGridview.setOnScrollListener(new OnScrollListener() {

			private int priorFirst = -1;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int first, int visible, int total) {
				if (visible < total && (first + visible == total)) {
					// see if we have more results
					if ((first != priorFirst) && (SearchController.getInstance().hasMoreResults())) {
						priorFirst = first;
						onLastListItemDisplayed(total, visible);
					}
				}
			}
		});
	}

	protected void onLastListItemDisplayed(int total, int visible) {
		SearchController searchController = SearchController.getInstance();
		if (searchController.hasMoreResults()) {
			// TODO: show search active indication
			searchController.continueSearch();
		}
	}

}
