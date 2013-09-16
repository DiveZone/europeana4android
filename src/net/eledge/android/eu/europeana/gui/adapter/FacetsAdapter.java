package net.eledge.android.eu.europeana.gui.adapter;

import java.util.List;

import net.eledge.android.eu.europeana.EuropeanaApplication;
import net.eledge.android.eu.europeana.search.model.facets.enums.FacetItemType;
import net.eledge.android.eu.europeana.search.model.searchresults.FacetItem;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FacetsAdapter extends ArrayAdapter<FacetItem> {

	private LayoutInflater inflater;
	private final Typeface europeanaFont;

	public FacetsAdapter(EuropeanaApplication application, Context context, List<FacetItem> facetItems) {
		super(context, 0, facetItems);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.europeanaFont = application.getEuropeanaFont();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FacetItem item = getItem(position);
		View view = inflater.inflate(item.itemType.getLayoutId(item.facetType), parent, false);
		try {
			TextView mTextView1 = (TextView) view.findViewById(android.R.id.text1);
			if (item.itemType == FacetItemType.SECTION) {
				mTextView1.setText(item.labelResource);
				if (item.last) {
					// enable progress bar
					ProgressBar mProgressBar = (ProgressBar) view.findViewById(android.R.id.progress);
					mProgressBar.setVisibility(View.VISIBLE);
				}
			} else if (item.itemType == FacetItemType.BREADCRUMB) {
				mTextView1.setText(item.description);
			} else if ((item.itemType == FacetItemType.CATEGORY) || (item.itemType == FacetItemType.CATEGORY_OPENED)) {
				mTextView1.setText(item.facetType.resId);
			} else {
				mTextView1.setText(item.description);
				TextView mTextView2 = (TextView) view.findViewById(android.R.id.text2);
				if ((mTextView2 != null) && (item.icon != null)) {
					mTextView2.setTypeface(europeanaFont);
					mTextView2.setText(item.icon);
				}
			}
		} catch (ClassCastException e) {
			// not gonna happen
		}
		return view;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		FacetItem item = getItem(position);
		return item.itemType != FacetItemType.SECTION;
	}

}
