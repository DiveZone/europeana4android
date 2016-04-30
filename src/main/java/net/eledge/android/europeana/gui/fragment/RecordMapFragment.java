/*
 * Copyright (c) 2013-2015 eLedge.net and the original author or authors.
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
import com.squareup.otto.Subscribe;

import net.eledge.android.europeana.EuropeanaApplication;
import net.eledge.android.europeana.R;
import net.eledge.android.europeana.search.RecordController;
import net.eledge.android.europeana.search.event.RecordLoadedEvent;
import net.eledge.android.europeana.search.model.record.RecordObject;
import net.eledge.android.europeana.search.model.record.abstracts.Resource;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RecordMapFragment extends Fragment {

    // Controller
    private final RecordController recordController = RecordController._instance;

    @BindView(R.id.fragment_record_map_mapview)
    MapView mMapView;

    @BindView(R.id.fragment_record_map_textview_prefLabel)
    TextView text1;

    @BindView(R.id.fragment_record_map_textview_coordinates)
    TextView text2;

    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EuropeanaApplication.bus.register(this);
        MapsInitializer.initialize(this.getActivity().getBaseContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_record_map, container, false);
        unbinder = ButterKnife.bind(this, root);
        mMapView.onCreate(savedInstanceState);
        RecordObject record = recordController.record;
        text1.setText(StringUtils.join(Resource.getPreferred(record.place.prefLabel, ((EuropeanaApplication) getActivity().getApplication()).getLocale()), ";"));
        text2.setText(record.place.latitude + ";" + record.place.longitude);
        return root;
    }

    @Subscribe
    public void OnRecordLoadedEvent(RecordLoadedEvent event) {
        redrawRecordView(event.result);
    }

    private void redrawRecordView(RecordObject record) {
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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        EuropeanaApplication.bus.unregister(this);
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
            redrawRecordView(recordController.record);
        }
    }

}
