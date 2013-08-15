package net.eledge.android.eu.europeana.gui.fragments;

import net.eledge.android.eu.europeana.EuropeanaApplication;
import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.gui.activity.RecordActivity;
import net.eledge.android.eu.europeana.gui.adaptor.ResultAdaptor;
import net.eledge.android.eu.europeana.search.SearchController;
import net.eledge.android.eu.europeana.search.listeners.SearchTaskListener;
import net.eledge.android.eu.europeana.search.model.SearchResult;
import net.eledge.android.eu.europeana.search.model.searchresults.Item;
import net.eledge.android.toolkit.gui.GuiUtils;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

public class SearchResultsFragment extends Fragment implements
		SearchTaskListener {

	private final String TAG_LISTENER = this.getClass().getSimpleName();

	private ResultAdaptor mResultAdaptor;

	private GridView mGridview;

	private TextView mStatusTextView;

	private SearchController searchController = SearchController.instance;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mResultAdaptor = new ResultAdaptor((EuropeanaApplication) this
				.getActivity().getApplication(), this.getActivity(),
				searchController.getSearchItems());
		searchController.registerListener(TAG_LISTENER, this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = (ViewGroup) inflater.inflate(
				R.layout.fragment_search_results, null);
		mStatusTextView = (TextView) root
				.findViewById(R.id.fragment_search_textview_status);
		mGridview = (GridView) root.findViewById(R.id.fragment_search_gridview);
		mGridview.setAdapter(mResultAdaptor);
		mGridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final Item selected = searchController.getSearchItems().get(position);
				final Intent intent = new Intent(SearchResultsFragment.this.getActivity(), RecordActivity.class);
				intent.setAction(Intent.ACTION_VIEW);
				intent.putExtra(RecordActivity.RECORD_ID, selected.id);
				SearchResultsFragment.this.getActivity().startActivity(intent);
			}
		});
		return root;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onDestroy() {
		searchController.cancelSearch();
		searchController.unregister(TAG_LISTENER);
		super.onDestroy();
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
			public void onScroll(AbsListView view, int first, int visible,
					int total) {
				if (visible < total && (first + visible == total)) {
					// see if we have more results
					if ((first != priorFirst)
							&& (searchController.hasMoreResults())) {
						priorFirst = first;
						onLastListItemDisplayed(total, visible);
					}
				}
			}
		});
		mGridview.setOnTouchListener(new OnTouchListener() {

			private static final int MIN_MOVE = 150;
			private float downY = -1;
			private boolean down = false;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					down = true;
					downY = event.getY();
					break;
				case MotionEvent.ACTION_MOVE:
					if (mStatusTextView.isShown()) {
						if (down && (event.getY() - downY < -MIN_MOVE)) {
							hideStatusText();
						}
					}
					if (!mStatusTextView.isShown()) {
						if (down && (event.getY() - downY > MIN_MOVE)) {
							showStatusText();
						}
					}
					break;
				case MotionEvent.ACTION_UP:
					down = false;
					break;
				}
				return false;
			}
		});
	}

	protected void onLastListItemDisplayed(int total, int visible) {
		if (searchController.hasMoreResults()) {
			searchController.continueSearch();
		}
	}

	@Override
	public void onSearchStart() {
		mStatusTextView.setText(R.string.msg_searching);
		showStatusText();
		if (mResultAdaptor != null) {
			mResultAdaptor.notifyDataSetChanged();
		}
	}

	@Override
	public void onSearchFinish(SearchResult results) {
		if (mResultAdaptor != null) {
			mResultAdaptor.notifyDataSetChanged();
		}
		mStatusTextView.setText(GuiUtils.format(this.getActivity(),
				R.string.msg_searchresults, searchController.size(),
				results.totalResults));
		showStatusText();
	}

	@Override
	public void onSearchError(String message) {
		mStatusTextView.setText(message);
		showStatusText();
	}

	private void hideStatusText() {
		if (!mStatusTextView.isShown()) {
			return;
		}
		Animation fadeOut = new AlphaAnimation(1, 0);
		fadeOut.setDuration(400);
		fadeOut.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mStatusTextView.setVisibility(View.GONE);
			}
		});
		mStatusTextView.startAnimation(fadeOut);
	}

	private void showStatusText() {
		if (mStatusTextView.isShown()) {
			return;
		}
		Animation fadeOut = new AlphaAnimation(0, 1);
		fadeOut.setDuration(400);
		fadeOut.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				mStatusTextView.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
			}
		});
		mStatusTextView.startAnimation(fadeOut);
	}

}
