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

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.eledge.android.europeana.EuropeanaApplication;
import net.eledge.android.europeana.R;
import net.eledge.android.europeana.search.SearchController;
import net.eledge.android.europeana.search.model.searchresults.Item;
import net.eledge.android.toolkit.gui.annotations.ViewResource;
import net.eledge.android.toolkit.net.ImageCacheManager;
import net.eledge.android.toolkit.net.abstracts.AsyncLoaderListener;

import java.util.List;

import static net.eledge.android.toolkit.gui.ViewInjector.inject;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {

    private final ImageCacheManager manager;
    private final Typeface europeanaFont;
    private final ResultAdaptorClickListener mListener;
    private final SearchController searchController = SearchController._instance;
    private List<Item> resultItems;

    public ResultAdapter(EuropeanaApplication application, List<Item> resultItems,
                         ResultAdaptorClickListener listener) {
        this.resultItems = resultItems;
        this.manager = application.getImageCacheManager();
        this.europeanaFont = application.getEuropeanaFont();
        this.manager.clearQueue();
        this.mListener = listener;
    }

    @Override
    public ResultAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.griditem_searchresult, parent, false);

        ViewHolder vh = new ViewHolder(v);
        inject(vh, v);
        vh.icon.setTypeface(europeanaFont);
        return vh;
    }

    @Override
    public void onBindViewHolder(ResultAdapter.ViewHolder viewHolder, int i) {
        Item item = resultItems.get(i);
        viewHolder.textTitle.setText(item.title[0]);
        viewHolder.icon.setText(item.type.icon);
        viewHolder.position = i;
        if ((item.edmPreview != null) && (item.edmPreview.length > 0)) {
            manager.displayImage(item.edmPreview[0], viewHolder.image, -1, new AsyncLoaderListener<Bitmap>() {
                @Override
                public void onFinished(Bitmap result, int httpStatus) {
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return resultItems == null ? 0 : resultItems.size();
    }

    public void add(List<Item> newData) {
        for (Item searchResult : newData) {
            resultItems.add(searchResult);
        }
    }

    public interface ResultAdaptorClickListener {
        void click(int position, Item item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @ViewResource(R.id.griditem_searchresult_textview_title)
        public TextView textTitle = null;
        @ViewResource(R.id.griditem_searchresult_imageview_thumbnail)
        public ImageView image = null;
        @ViewResource(R.id.griditem_searchresult_textview_type)
        public TextView icon = null;

        public int position;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.click(position, resultItems.get(position));
        }
    }

}
