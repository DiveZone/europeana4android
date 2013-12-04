package net.eledge.android.eu.europeana.gui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.gui.activity.RecordActivity;
import net.eledge.android.eu.europeana.gui.fragment.RecordDetailsFragment;
import net.eledge.android.eu.europeana.gui.fragment.RecordImagesFragment;
import net.eledge.android.eu.europeana.gui.fragment.RecordMapFragment;
import net.eledge.android.eu.europeana.gui.fragment.RecordSeeAlsoFragment;
import net.eledge.android.eu.europeana.search.RecordController;
import net.eledge.android.eu.europeana.search.model.record.RecordObject;
import net.eledge.android.toolkit.async.listener.TaskListener;
import net.eledge.android.toolkit.gui.GuiUtils;

import java.util.ArrayList;
import java.util.List;

public class RecordPagerAdapter extends FragmentStatePagerAdapter implements TaskListener<RecordObject>  {
	
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
			// IMAGES
			labels.add(R.string.record_tab_images);
			fragments.add(new RecordImagesFragment());
            // MAP
			if ( (record.place.latitude != null) && (record.place.longitude != null)) {
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
 