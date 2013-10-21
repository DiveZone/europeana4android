package net.eledge.android.eu.europeana.gui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;

import net.eledge.android.eu.europeana.EuropeanaApplication;
import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.search.RecordController;
import net.eledge.android.eu.europeana.search.model.record.RecordObject;
import net.eledge.android.eu.europeana.search.model.record.abstracts.Resource;
import net.eledge.android.toolkit.async.listener.TaskListener;

import org.apache.commons.lang.StringUtils;

public class RecordMapFragment extends Fragment implements TaskListener<RecordObject> {
	
	// Controller
	private RecordController recordController = RecordController._instance;
	
	private MapView mMapView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		recordController.registerListener(RecordMapFragment.class, this);

		try {
			MapsInitializer.initialize(this.getActivity().getBaseContext());
		} catch (GooglePlayServicesNotAvailableException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_record_map, null);
		mMapView = (MapView) root.findViewById(R.id.fragment_record_map_mapview);
		mMapView.onCreate(savedInstanceState);
        RecordObject record = recordController.record;
        TextView text1 = (TextView) root.findViewById(R.id.fragment_record_map_textview_prefLabel);
        text1.setText(StringUtils.join(Resource.getPreferred(record.place.prefLabel, ((EuropeanaApplication)getActivity().getApplication()).getLocale()), ";"));
        TextView text2 = (TextView) root.findViewById(R.id.fragment_record_map_textview_coordinates);
        text2.setText(record.place.latitude + ";"  + record.place.longitude);
		return root;
	}

    @Override
    public void onTaskStart() {
        // ignore
    }

	@Override
	public void onTaskFinished(RecordObject record) {
		if (mMapView != null) {
			if ( (record.place.latitude != null) && (record.place.longitude != null)) {
				GoogleMap map = mMapView.getMap();
                if (map != null) {
                    LatLng pos = new LatLng(record.place.latitude, record.place.longitude);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 12));
                }
			}
		}
	}
	
	@Override
	public void onDestroy() {
		recordController.unregister(RecordMapFragment.class);
		if (mMapView != null) {
			mMapView.onDestroy();
		}
		super.onDestroy();
	}
	
	@Override
	public void onLowMemory() {
		if (mMapView != null) {
			mMapView.onLowMemory();
		}
		super.onLowMemory();
	}
	
	@Override
	public void onPause() {
		if (mMapView != null) {
			mMapView.onPause();
		}
		super.onPause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (mMapView != null) {
			mMapView.onResume();
		}
		if (recordController.record != null) {
			onTaskFinished(recordController.record);
		}
	}

}
