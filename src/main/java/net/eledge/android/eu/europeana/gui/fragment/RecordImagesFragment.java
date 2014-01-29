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
            text1.setTypeface(((EuropeanaApplication) getActivity().getApplication()).getEuropeanaFont());
            TextView text2 = (TextView) root.findViewById(R.id.fragment_record_images_textview_copyright_text);
            text2.setText(right.getRightsText());
        }
        return root;
    }

}
