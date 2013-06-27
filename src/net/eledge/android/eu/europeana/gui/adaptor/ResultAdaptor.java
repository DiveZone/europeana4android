package net.eledge.android.eu.europeana.gui.adaptor;

import java.util.List;

import net.eledge.android.eu.europeana.EuropeanaApplication;
import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.search.model.searchresults.Item;
import net.eledge.android.toolkit.net.ImageCacheManager;
import net.eledge.android.toolkit.net.abstracts.AsyncLoaderListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ResultAdaptor extends ArrayAdapter<Item> {

	private LayoutInflater inflater;

	private ImageCacheManager manager;

	public ResultAdaptor(EuropeanaApplication application, Context context,
			List<Item> resultItems) {
		super(context, 0, resultItems);
		this.manager = application.getImageCacheManager();
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.manager.clearQueue();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ResultViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.griditem_searchresult, parent, false);
			holder = new ResultViewHolder();
			holder.textTitle = (TextView) convertView.findViewById(R.id.textview_searchItemTitle);
			holder.image = (ImageView) convertView.findViewById(R.id.imageview_searchItemImage);
			convertView.setTag(holder);
		} else {
			holder = (ResultViewHolder) convertView.getTag();
		}
		

		Item item = getItem(position);

		if (item.thumbnail != null) {
			manager.displayImage(item.thumbnail, holder.image, -1, new AsyncLoaderListener<Bitmap>() {
				@Override
				public void onFinished(Bitmap result, int httpStatus) {
					notifyDataSetChanged();
				}
			});
		}
		
		holder.textTitle.setText(item.title);
		return convertView;
	}

	public void add(List<Item> newData) {
		for (Item searchResult : newData) {
			add(searchResult);
		}
	}

	private class ResultViewHolder {
		TextView textTitle = null;
		ImageView image = null;
	}
	
}
