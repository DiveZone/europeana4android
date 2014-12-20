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

package net.eledge.android.europeana.gui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import net.eledge.android.europeana.R;
import net.eledge.android.europeana.gui.activity.RecordActivity;
import net.eledge.android.europeana.gui.fragment.RecordDetailsFragment;
import net.eledge.android.europeana.gui.fragment.RecordMapFragment;
import net.eledge.android.europeana.gui.fragment.RecordSeeAlsoFragment;
import net.eledge.android.europeana.search.RecordController;
import net.eledge.android.europeana.search.model.record.RecordObject;
import net.eledge.android.toolkit.async.listener.TaskListener;
import net.eledge.android.toolkit.gui.GuiUtils;

import java.util.ArrayList;
import java.util.List;

public class RecordPagerAdapter extends FragmentStatePagerAdapter implements TaskListener<RecordObject> {

    private final Context mContext;
    private final RecordActivity mRecordActivity;

    private final List<Fragment> fragments = new ArrayList<>();
    public final List<Integer> labels = new ArrayList<>();

    public RecordPagerAdapter(RecordActivity activity, FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
        mRecordActivity = activity;
        RecordController._instance.registerListener(getClass(), this);
    }

    @Override
    public void onTaskStart() {
        // left empty
    }

    @Override
    public void onTaskFinished(RecordObject record) {
        if (record != null) {
            fragments.clear();
            labels.clear();
            if (!mRecordActivity.mTwoColumns) {
                // DETAIL INFO
                labels.add(R.string.record_tab_details);
                fragments.add(new RecordDetailsFragment());
            }
            // MAP
            if ((record.place.latitude != null) && (record.place.longitude != null)) {
                labels.add(R.string.record_tab_map);
                fragments.add(new RecordMapFragment());
            }
            // SEE ALSO
            labels.add(R.string.record_tab_seealso);
            fragments.add(new RecordSeeAlsoFragment());

            notifyDataSetChanged();
            mRecordActivity.updateTabs();
        }
    }

    @Override
    public Fragment getItem(int page) {
        return fragments.get(page);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int page) {
        return GuiUtils.getString(mContext, labels.get(page));
    }

}
 