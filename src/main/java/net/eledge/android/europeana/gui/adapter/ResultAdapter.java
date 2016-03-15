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
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import net.eledge.android.europeana.EuropeanaApplication;
import net.eledge.android.europeana.R;
import net.eledge.android.europeana.search.model.searchresults.Item;
import net.eledge.android.toolkit.StringArrayUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {

    private final ResultAdaptorClickListener mListener;
    private final List<Item> resultItems;
    private final Context context;

    public ResultAdapter(Context context, List<Item> resultItems,
                         ResultAdaptorClickListener listener) {
        this.resultItems = resultItems;
        this.mListener = listener;
        this.context = context;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.griditem_searchresult, parent, false);

        ViewHolder vh = new ViewHolder(v);
        vh.icon.setTypeface(EuropeanaApplication.europeanaFont);
        return vh;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onBindViewHolder(final ResultAdapter.ViewHolder viewHolder, int i) {
        Item item = resultItems.get(i);
        viewHolder.textTitle.setText(item.title[0]);
        viewHolder.icon.setText(item.type.icon);
        viewHolder.position = i;
        if (StringArrayUtils.isNotBlank(item.edmPreview)) {
            Glide.with(context)
                    .load(item.edmPreview[0])
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            if ( (viewHolder.image != null) && (viewHolder.image.getDrawable() != null)) {
                                Bitmap bitmap = ((BitmapDrawable) viewHolder.image.getDrawable()).getBitmap();
                                new Palette.Builder(bitmap).generate(new Palette.PaletteAsyncListener() {
                                    public void onGenerated(Palette palette) {
                                        viewHolder.background.setBackgroundColor(
                                                palette.getLightMutedColor(context.getResources().getColor(R.color.emphasis_transparant)));
                                    }
                                });
                            }
                            return false;
                        }
                    })
                    .into(viewHolder.image);
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
        @Bind(R.id.griditem_searchresult_textview_title)
        TextView textTitle = null;
        @Bind(R.id.griditem_searchresult_imageview_thumbnail)
        ImageView image = null;
        @Bind(R.id.griditem_searchresult_textview_type)
        TextView icon = null;
        @Bind(R.id.griditem_searchresult_background)
        View background = null;

        public int position;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.click(position, resultItems.get(position));
        }
    }

}
