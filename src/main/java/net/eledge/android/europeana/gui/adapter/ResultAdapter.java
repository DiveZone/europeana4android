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

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.eledge.android.europeana.BR;
import net.eledge.android.europeana.EuropeanaApplication;
import net.eledge.android.europeana.R;
import net.eledge.android.europeana.gui.adapter.events.SearchItemClicked;
import net.eledge.android.europeana.search.model.searchresults.Item;

import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {

    private final List<Item> resultItems;

    public ResultAdapter(List<Item> resultItems) {
        this.resultItems = resultItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        ViewDataBinding viewDataBinding =
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.getContext()),
                        R.layout.griditem_searchresult, parent, false
                );
        return new ViewHolder(viewDataBinding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = resultItems.get(position);
        holder.binding.setVariable(BR.item, holder.item);
        holder.binding.executePendingBindings();
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        @Bind(R.id.griditem_searchresult_background)
//        View background = null;

        private Item item;
        private ViewDataBinding binding;

        public ViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            EuropeanaApplication.bus.post(new SearchItemClicked(getAdapterPosition(), item));
        }
    }

}
