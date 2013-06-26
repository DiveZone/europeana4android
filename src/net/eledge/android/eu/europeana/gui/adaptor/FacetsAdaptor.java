package net.eledge.android.eu.europeana.gui.adaptor;

import java.util.List;

import net.eledge.android.eu.europeana.search.model.enums.FacetItemType;
import net.eledge.android.eu.europeana.search.model.searchresults.FacetItem;
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
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FacetItem item = getItem(position);
		int resId = item.itemType.resId;
		if (item.last && (item.itemType.lastResId != -1)) {
			resId = item.itemType.lastResId;
		}
		View view = inflater.inflate(resId, parent, false);
		try {
			if ((item.itemType == FacetItemType.CATEGORY)
					|| (item.itemType == FacetItemType.CATEGORY_OPENED)) {
				TextView textTitle = (TextView) view
						.findViewById(android.R.id.text1);
				textTitle.setText(item.facetType.resId);
			} else {
				TextView textTitle = (TextView) view
						.findViewById(android.R.id.text1);
				textTitle.setText(item.description);
			}
		} catch (ClassCastException e) {
			Log.e(TAG,
					"Your layout must provide an image and a text view with ID's icon and text.",
					e);
			throw e;
		}
		return view;
	}

}
