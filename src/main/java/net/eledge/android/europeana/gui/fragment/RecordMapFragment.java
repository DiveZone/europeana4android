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

package net.eledge.android.europeana.gui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;

import net.eledge.android.europeana.EuropeanaApplication;
import net.eledge.android.europeana.R;
import net.eledge.android.europeana.search.RecordController;
import net.eledge.android.europeana.search.model.record.RecordObject;
import net.eledge.android.europeana.search.model.record.abstracts.Resource;
import net.eledge.android.toolkit.async.listener.TaskListener;
import net.eledge.android.toolkit.gui.annotations.ViewResource;

import org.apache.commons.lang3.StringUtils;

import static net.eledge.android.toolkit.gui.ViewInjector.inject;

public class RecordMapFragment extends Fragment implements TaskListener<RecordObject> {

    // Controller
    private final RecordController recordController = RecordController._instance;

    @ViewResource(R.id.fragment_record_map_mapview)
    private MapView mMapView;

    @ViewResource(R.id.fragment_record_map_textview_prefLabel)
    private TextView text1;

    @ViewResource(R.id.fragment_record_map_textview_coordinates)
    private TextView text2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recordController.registerListener(RecordMapFragment.class, this);
        MapsInitializer.initialize(this.getActivity().getBaseContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_record_map, null);
        inject(this, root);
        mMapView.onCreate(savedInstanceState);
        RecordObject record = recordController.record;
        text1.setText(StringUtils.join(Resource.getPreferred(record.place.prefLabel, ((EuropeanaApplication) getActivity().getApplication()).getLocale()), ";"));
        text2.setText(record.place.latitude + ";" + record.place.longitude);
        return root;
    }

    @Override
    public void onTaskStart() {
        // ignore
    }

    @Override
    public void onTaskFinished(RecordObject record) {
        if (mMapView != null) {
            if ((record.place.latitude != null) && (record.place.longitude != null)) {
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
