/*
 * Copyright (c) 2014 eLedge.net and the original author or authors.
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

package net.eledge.android.eu.europeana.gui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.eledge.android.eu.europeana.EuropeanaApplication;
import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.search.SearchController;
import net.eledge.android.eu.europeana.search.model.searchresults.Item;
import net.eledge.android.toolkit.gui.ViewInjector;
import net.eledge.android.toolkit.gui.annotations.ViewResource;
import net.eledge.android.toolkit.net.ImageCacheManager;
import net.eledge.android.toolkit.net.abstracts.AsyncLoaderListener;

import java.util.List;

public class ResultAdapter extends ArrayAdapter<Item> {

    // Controller
    private final SearchController searchController = SearchController._instance;

    private final LayoutInflater inflater;

    private final ImageCacheManager manager;

    private final Typeface europeanaFont;

    public ResultAdapter(EuropeanaApplication application, Context context, List<Item> resultItems) {
        super(context, 0, resultItems);
        this.manager = application.getImageCacheManager();
        this.europeanaFont = application.getEuropeanaFont();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.manager.clearQueue();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ResultViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.griditem_searchresult, parent, false);
            holder = new ResultViewHolder();
            ViewInjector.inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ResultViewHolder) convertView.getTag();
        }
        int bg = (searchController.getItemSelected() == position ? R.drawable.background_card_selected : R.drawable.background_card);
        convertView.setBackgroundResource(bg);

        Item item = getItem(position);

        if ((item.edmPreview != null) && (item.edmPreview.length > 0)) {
            manager.displayImage(item.edmPreview[0], holder.image, -1, new AsyncLoaderListener<Bitmap>() {
                @Override
                public void onFinished(Bitmap result, int httpStatus) {
                    notifyDataSetChanged();
                }
            });
        }

        holder.textTitle.setText(item.title[0]);
        holder.icon.setText(item.type.icon);
        holder.icon.setTypeface(europeanaFont);
        return convertView;
    }

    public void add(List<Item> newData) {
        for (Item searchResult : newData) {
            add(searchResult);
        }
    }

    private class ResultViewHolder {
        @ViewResource(R.id.griditem_searchresult_textview_title)
        TextView textTitle = null;
        @ViewResource(R.id.griditem_searchresult_imageview_thumbnail)
        ImageView image = null;
        @ViewResource(R.id.griditem_searchresult_textview_type)
        TextView icon = null;
    }

}
