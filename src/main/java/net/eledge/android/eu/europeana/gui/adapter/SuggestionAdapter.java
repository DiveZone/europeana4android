package net.eledge.android.eu.europeana.gui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.search.model.suggestion.Item;

import java.util.List;

public class SuggestionAdapter extends ArrayAdapter<Item> {

	private final LayoutInflater inflater;
	
	public SuggestionAdapter(Context context, List<Item> suggestions) {
		super(context, 0, suggestions);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SuggestionViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.griditem_suggestion, parent, false);
			holder = new SuggestionViewHolder();
			holder.suggestion = (TextView) convertView.findViewById(R.id.griditem_suggestion_textview_suggestion);
			holder.scope = (TextView) convertView.findViewById(R.id.griditem_suggestion_textview_scope);
			holder.count = (TextView) convertView.findViewById(R.id.griditem_suggestion_textview_count);
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
		TextView suggestion = null;
		TextView scope = null;
		TextView count = null;
	}

}
