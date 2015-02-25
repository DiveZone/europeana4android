/*
 * Copyright (c) 2013-2015 eLedge.net and the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.eledge.android.europeana.gui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.TextView;

import net.eledge.android.europeana.EuropeanaApplication;
import net.eledge.android.europeana.R;
import net.eledge.android.europeana.gui.activity.RecordActivity;
import net.eledge.android.europeana.gui.adapter.ResultAdapter;
import net.eledge.android.europeana.search.SearchController;
import net.eledge.android.europeana.search.listeners.SearchTaskListener;
import net.eledge.android.europeana.search.model.SearchItems;
import net.eledge.android.europeana.search.model.searchresults.Item;
import net.eledge.android.toolkit.gui.GuiUtils;
import net.eledge.android.toolkit.gui.annotations.ViewResource;

import static net.eledge.android.toolkit.gui.ViewInjector.inject;

public class SearchResultsFragment extends Fragment implements SearchTaskListener {

    private ResultAdapter mResultAdaptor;

    private RecyclerView.LayoutManager mLayoutManager;

    @ViewResource(R.id.fragment_search_gridview)
    private RecyclerView mGridView;

    @ViewResource(R.id.fragment_search_textview_status)
    private TextView mStatusTextView;

    private final SearchController searchController = SearchController._instance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResultAdaptor = new ResultAdapter((EuropeanaApplication) this.getActivity().getApplication(),
                searchController.getSearchItems(), new ResultAdapter.ResultAdaptorClickListener() {
            @Override
            public void click(int position, Item item) {
                searchController.setItemSelected(position);
                final Intent intent = new Intent(SearchResultsFragment.this.getActivity(), RecordActivity.class);
                intent.setAction(Intent.ACTION_VIEW);
                intent.putExtra(RecordActivity.RECORD_ID, item.id);
                SearchResultsFragment.this.getActivity().startActivity(intent);
            }
        });
        searchController.registerListener(SearchResultsFragment.class, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search_results, null);
        inject(this, root);
        mGridView.setAdapter(mResultAdaptor);
        mLayoutManager = new StaggeredGridLayoutManager(
                GuiUtils.getInteger(getActivity().getApplicationContext(), R.integer.search_results_columns),
                StaggeredGridLayoutManager.VERTICAL);
        mGridView.setLayoutManager(mLayoutManager);
        if (searchController.isSearching()) {
            mStatusTextView.setText(R.string.msg_searching);
            showStatusText();
        }
        return root;
    }

    @Override
    public void onDestroy() {
        searchController.cancelSearch();
        searchController.unregister(SearchResultsFragment.class);
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mGridView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            private int priorFirst = -1;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // TODO: fix scroll
            }

            public void onScroll(RecyclerView view, int first, int visible, int total) {
                if (visible < total && (first + visible == total)) {
                    // see if we have more results
                    if ((first != priorFirst) && (searchController.hasMoreResults())) {
                        priorFirst = first;
                        onLastListItemDisplayed();
                    }
                }
            }
        });
        mGridView.setOnTouchListener(new OnTouchListener() {

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

    void onLastListItemDisplayed() {
        if (searchController.hasMoreResults()) {
            searchController.continueSearch(this.getActivity());
        }
    }

    @Override
    public void onSearchStart(boolean isFacetSearch) {
        if (!isFacetSearch) {
            if (mStatusTextView != null) {
                mStatusTextView.setText(R.string.msg_searching);
                showStatusText();
            }
            if (mResultAdaptor != null) {
                mResultAdaptor.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onSearchFacetFinish() {
        // ignore
    }

    @Override
    public void onSearchItemsFinish(SearchItems results) {
        if (mResultAdaptor != null) {
            mResultAdaptor.notifyDataSetChanged();
        }
        mStatusTextView.setText(GuiUtils.format(this.getActivity(), R.string.msg_searchresults,
                searchController.size(), results.totalResults));
        showStatusText();
    }

    private void hideStatusText() {
        if ((mStatusTextView == null) || !mStatusTextView.isShown()) {
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
