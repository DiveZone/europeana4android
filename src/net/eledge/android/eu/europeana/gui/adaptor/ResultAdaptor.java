package net.eledge.android.eu.europeana.gui.adaptor;

import java.util.List;

import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.search.model.searchresults.Item;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ResultAdaptor extends ArrayAdapter<Item> {

	private final static String TAG = "ResultAdaptor";
	private LayoutInflater inflater;

//	private ImageThreadLoader imageLoader = new ImageThreadLoader();

	public ResultAdaptor(Context context, List<Item> resultItems) {
		super(context, 0, resultItems);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		TextView textTitle;
//		TextView textDescription;
//		final ImageView image;

		View view = inflater.inflate(R.layout.griditem_searchresult, parent, false);

		try {
			textTitle = (TextView) view.findViewById(R.id.textview_searchItemTitle);
//			textDescription = (TextView) view.findViewById(R.id.);
//			image = (ImageView) view.findViewById(R.id.imageview_searchItemImage);
		} catch (ClassCastException e) {
			Log.e(TAG, "Your layout must provide an image and a text view with ID's icon and text.", e);
			throw e;
		}

		Item item = getItem(position);
//		Bitmap cachedImage = null;
//		try {
//			cachedImage = imageLoader.loadImage(item.thumbnail, new ImageThreadLoader.ImageLoadedListener() {
//				@Override
//				public void imageLoaded(Bitmap imageBitmap) {
//					image.setImageBitmap(imageBitmap);
//					notifyDataSetChanged();
//				}
//			});
//
//		} catch (MalformedURLException e) {
//			Log.e(TAG, "Bad remote image URL: " + item.thumbnail, e);
//		}

		textTitle.setText(item.title);
//		textDescription.setText(item.description);

/*		if (cachedImage != null) {
			image.setImageBitmap(cachedImage);
		}
*/
		return view;
	}

	public void add(List<Item> newData) {
		for (Item searchResult : newData) {
			add(searchResult);
		}
	}

}
