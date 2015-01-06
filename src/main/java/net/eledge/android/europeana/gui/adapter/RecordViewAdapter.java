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

package net.eledge.android.europeana.gui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import net.eledge.android.europeana.EuropeanaApplication;
import net.eledge.android.europeana.search.RecordController;
import net.eledge.android.europeana.search.model.record.abstracts.RecordView;

import java.util.List;

public class RecordViewAdapter extends ArrayAdapter<RecordView> {


    private final RecordController recordController = RecordController._instance;

    private final LayoutInflater inflater;
    private final EuropeanaApplication application;

    public RecordViewAdapter(EuropeanaApplication application, Context context, List<RecordView> items) {
        super(context, 0, items);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.application = application;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (recordController.record != null) {
            RecordView recordView = getItem(position);
            return recordView.getView(recordController.record, parent, inflater, application);
        }
        return null;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

}
