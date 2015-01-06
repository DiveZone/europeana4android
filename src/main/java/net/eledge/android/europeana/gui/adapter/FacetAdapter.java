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

package net.eledge.android.europeana.gui.adapter;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.eledge.android.europeana.EuropeanaApplication;
import net.eledge.android.europeana.search.model.facets.enums.FacetItemType;
import net.eledge.android.europeana.search.model.searchresults.FacetItem;
import net.eledge.android.toolkit.gui.annotations.ViewResource;

import java.util.ArrayList;
import java.util.List;

import static net.eledge.android.toolkit.gui.ViewInjector.inject;

public class FacetAdapter extends RecyclerView.Adapter<FacetAdapter.ViewHolder> {

    private final Typeface europeanaFont;
    private final List<FacetItem> facetItems = new ArrayList<>(100);
    private final FacetAdaptorClickListener mListener;

    public FacetAdapter(EuropeanaApplication application, FacetAdaptorClickListener listener) {
        this.europeanaFont = application.getEuropeanaFont();
        this.mListener = listener;
    }

    @Override
    public int getItemCount() {
        return facetItems.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FacetItem item = facetItems.get(position);
        if (item.itemType == FacetItemType.SECTION) {
            holder.mLabelTextView.setText(item.labelResource);
            if (item.last) {
                // enable progress bar
                holder.mProgressBar.setVisibility(View.VISIBLE);
            }
        } else if (item.itemType == FacetItemType.BREADCRUMB) {
            holder.mLabelTextView.setText(item.description);
        } else if ((item.itemType == FacetItemType.CATEGORY) || (item.itemType == FacetItemType.CATEGORY_OPENED)) {
            holder.mLabelTextView.setText(item.facetType.resId);
        } else {
            holder.mLabelTextView.setText(item.description);
            if ((holder.mIconTextView != null) && (item.icon != null)) {
                holder.mIconTextView.setText(item.icon);
            }
        }
        holder.position = position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        FacetItem item = facetItems.get(position);
        View v = LayoutInflater.from(parent.getContext())
                .inflate(item.itemType.getLayoutId(item.facetType), parent, false);
        ViewHolder vh = new ViewHolder(v);
        inject(vh, v);
        if (vh.mIconTextView != null) {
            vh.mIconTextView.setTypeface(europeanaFont);
        }
        return vh;
    }

    public boolean areAllItemsEnabled() {
        return false;
    }

    public boolean isEnabled(int position) {
        FacetItem item = facetItems.get(position);
        return item.itemType != FacetItemType.SECTION;
    }

    public void add(FacetItem item) {
        facetItems.add(item);
    }

    public void clear() {
        facetItems.clear();
    }

    public boolean isEmpty() {
        return facetItems.isEmpty();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @ViewResource(android.R.id.text1)
        TextView mLabelTextView;

        @ViewResource(android.R.id.text2)
        TextView mIconTextView;

        @ViewResource(android.R.id.progress)
        ProgressBar mProgressBar;

        public int position;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onClick(View v) {
            mListener.click(facetItems.get(position));
        }
    }

    public interface FacetAdaptorClickListener {
        void click(FacetItem item);
    }

}
