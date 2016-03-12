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

import com.squareup.otto.Subscribe;

import net.eledge.android.europeana.EuropeanaApplication;
import net.eledge.android.europeana.R;
import net.eledge.android.europeana.gui.activity.RecordActivity;
import net.eledge.android.europeana.gui.adapter.ResultAdapter;
import net.eledge.android.europeana.search.SearchController;
import net.eledge.android.europeana.search.event.SearchItemsLoadedEvent;
import net.eledge.android.europeana.search.event.SearchStartedEvent;
import net.eledge.android.europeana.search.model.searchresults.Item;
import net.eledge.android.toolkit.gui.GuiUtils;
import net.eledge.android.toolkit.gui.listener.EndlessRecyclerOnScrollListener;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchResultsFragment extends Fragment {

    private ResultAdapter mResultAdaptor;
    private StaggeredGridLayoutManager mLayoutManager;

    @Bind(R.id.fragment_search_gridview)
    RecyclerView mGridView;

    @Bind(R.id.fragment_search_textview_status)
    TextView mStatusTextView;

    private final SearchController searchController = SearchController._instance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EuropeanaApplication.bus.register(this);
        mResultAdaptor = new ResultAdapter(this.getActivity(),
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search_results, container, false);
        ButterKnife.bind(this, root);
        mGridView.setAdapter(mResultAdaptor);

        mLayoutManager = new StaggeredGridLayoutManager(
                GuiUtils.getInteger(getActivity(), R.integer.search_results_columns),
                StaggeredGridLayoutManager.VERTICAL);
        mGridView.setLayoutManager(mLayoutManager);
        if (searchController.isSearching()) {
            mStatusTextView.setText(R.string.msg_searching);
            showStatusText();
        }
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        searchController.cancelSearch();
        super.onDestroy();
        EuropeanaApplication.bus.unregister(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mGridView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager, 5) {
            @Override
            public void onLoadMore(int current_page) {
                onLastListItemDisplayed();
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
            searchController.continueSearch();
        }
    }

    @Subscribe
    public void onSearchStartedEvent(SearchStartedEvent event) {
        if (!event.facetSearch) {
            if (mStatusTextView != null) {
                mStatusTextView.setText(R.string.msg_searching);
                showStatusText();
            }
            if (mResultAdaptor != null) {
                mResultAdaptor.notifyDataSetChanged();
            }
        }
    }

    @Subscribe
    public void onSearchItemsFinish(SearchItemsLoadedEvent event) {
        if (mResultAdaptor != null) {
            mResultAdaptor.notifyDataSetChanged();
        }
        mStatusTextView.setText(GuiUtils.format(this.getActivity(), R.string.msg_searchresults,
                                                                                                                                           searchController.size(), event.results.totalResults));
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
