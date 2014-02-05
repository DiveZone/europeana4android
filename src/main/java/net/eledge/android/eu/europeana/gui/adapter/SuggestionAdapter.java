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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.search.model.suggestion.Item;
import net.eledge.android.toolkit.gui.ViewInjector;
import net.eledge.android.toolkit.gui.annotations.ViewResource;

import java.util.List;

public class SuggestionAdapter extends ArrayAdapter<Item> {

    private final LayoutInflater inflater;

    public SuggestionAdapter(Context context, List<Item> suggestions) {
        super(context, 0, suggestions);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SuggestionViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.griditem_suggestion, parent, false);
            holder = new SuggestionViewHolder();
            ViewInjector.inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (SuggestionViewHolder) convertView.getTag();
        }
        Item suggestion = getItem(position);
        holder.suggestion.setText(suggestion.term);
        holder.scope.setText(suggestion.field);
        holder.count.setText(String.valueOf(suggestion.frequency));
        return convertView;
    }

    private class SuggestionViewHolder {
        @ViewResource(R.id.griditem_suggestion_textview_suggestion)
        TextView suggestion = null;
        @ViewResource(R.id.griditem_suggestion_textview_scope)
        TextView scope = null;
        @ViewResource(R.id.griditem_suggestion_textview_count)
        TextView count = null;
    }

}
