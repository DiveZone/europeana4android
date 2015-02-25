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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import net.eledge.android.europeana.EuropeanaApplication;
import net.eledge.android.europeana.R;
import net.eledge.android.europeana.search.model.searchresults.Item;
import net.eledge.android.toolkit.StringArrayUtils;
import net.eledge.android.toolkit.gui.annotations.ViewResource;

import java.util.List;

import static net.eledge.android.toolkit.gui.ViewInjector.inject;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {

    private final Typeface europeanaFont;
    private final ResultAdaptorClickListener mListener;
    private List<Item> resultItems;
    private Context context;

    public ResultAdapter(EuropeanaApplication application, List<Item> resultItems,
                         ResultAdaptorClickListener listener) {
        this.resultItems = resultItems;
        this.europeanaFont = application.getEuropeanaFont();
        this.mListener = listener;
        this.context = application.getApplicationContext();
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
    public void onBindViewHolder(final ResultAdapter.ViewHolder viewHolder, int i) {
        Item item = resultItems.get(i);
        viewHolder.textTitle.setText(item.title[0]);
        viewHolder.icon.setText(item.type.icon);
        viewHolder.position = i;
        if (StringArrayUtils.isNotBlank(item.edmPreview)) {
            Picasso.with(context)
                    .load(item.edmPreview[0])
                    .stableKey(item.id)
                    .into(viewHolder.image, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap bitmap = ((BitmapDrawable) viewHolder.image.getDrawable()).getBitmap();
                            Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                                public void onGenerated(Palette palette) {
                                    viewHolder.background.setBackgroundColor(
                                            palette.getLightMutedColor(R.color.emphasis_transparant));
                                }
                            });
                        }

                        @Override
                        public void onError() {

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
        @ViewResource(R.id.griditem_searchresult_background)
        public View background = null;

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
