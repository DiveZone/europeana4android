package net.eledge.android.eu.europeana.gui.adaptor;

import java.util.List;

import net.eledge.android.eu.europeana.gui.model.FacetItem;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class FacetsAdaptor extends ArrayAdapter<FacetItem> {
	private final static String TAG = "FacetsAdaptor";
	
	private LayoutInflater inflater;

	public FacetsAdaptor(Context context, List<FacetItem> facetItems) {
		super(context, 0, facetItems);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FacetItem item = getItem(position);
		View view = inflater.inflate(item.type.resId, parent, false);
		try {
			TextView textTitle = (TextView) view.findViewById(android.R.id.text1);
			textTitle.setText(item.description);
		} catch (ClassCastException e) {
			Log.e(TAG, "Your layout must provide an image and a text view with ID's icon and text.", e);
			throw e;
		}
		return view;
	}
	
}
