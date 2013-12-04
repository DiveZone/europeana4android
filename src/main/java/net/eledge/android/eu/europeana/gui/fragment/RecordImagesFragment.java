package net.eledge.android.eu.europeana.gui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.eledge.android.eu.europeana.EuropeanaApplication;
import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.search.RecordController;
import net.eledge.android.eu.europeana.search.model.enums.Right;
import net.eledge.android.eu.europeana.search.model.record.RecordObject;

public class RecordImagesFragment extends Fragment {

    // Controller
    private final RecordController recordController = RecordController._instance;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_record_images, null);
        RecordObject record = recordController.record;
        Right right = record.aggregation.right != null ? record.aggregation.right : record.europeanaAggregation.right;
        if (right != null) {
            TextView text1 = (TextView) root.findViewById(R.id.fragment_record_images_textview_copyright_icons);
            text1.setText(right.getFontIcon());
            text1.setTypeface(((EuropeanaApplication)getActivity().getApplication()).getEuropeanaFont());
            TextView text2 = (TextView) root.findViewById(R.id.fragment_record_images_textview_copyright_text);
            text2.setText(right.getRightsText());
        }
		return root;
	}

}
