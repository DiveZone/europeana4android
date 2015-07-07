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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.eledge.android.europeana.EuropeanaApplication;
import net.eledge.android.europeana.R;
import net.eledge.android.europeana.gui.adapter.events.SuggestionClicked;
import net.eledge.android.europeana.search.model.suggestion.Suggestion;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.ViewHolder> {

    public final List<Suggestion> suggestions = new ArrayList<>();

    public SuggestionAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.griditem_suggestion, parent, false);
        return new ViewHolder(v);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Suggestion suggestion = suggestions.get(position);
        holder.suggestion = suggestion;
        holder.suggestionText.setText(suggestion.term);
        holder.scope.setText(suggestion.field);
        holder.count.setText(String.valueOf(suggestion.frequency));
    }

    @Override
    public int getItemCount() {
        return suggestions == null ? 0 : suggestions.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Suggestion suggestion;

        @Bind(R.id.griditem_suggestion_textview_suggestion)
        TextView suggestionText = null;

        @Bind(R.id.griditem_suggestion_textview_scope)
        TextView scope = null;

        @Bind(R.id.griditem_suggestion_textview_count)
        TextView count = null;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View v) {
            EuropeanaApplication.bus.post(new SuggestionClicked(suggestion));
        }
    }

}
