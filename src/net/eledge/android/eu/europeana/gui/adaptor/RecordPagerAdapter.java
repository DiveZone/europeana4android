package net.eledge.android.eu.europeana.gui.adaptor;

import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.gui.fragments.RecordDetailsFragment;
import net.eledge.android.eu.europeana.gui.fragments.RecordImagesFragment;
import net.eledge.android.eu.europeana.gui.fragments.RecordSeeAlsoFragment;
import net.eledge.android.toolkit.gui.GuiUtils;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class RecordPagerAdapter extends FragmentStatePagerAdapter {
	
	private Context mContext;
	
	public RecordPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		mContext = context;
	}

	@Override
	public Fragment getItem(int page) {
		switch (page) {
		case 0:
			return new RecordDetailsFragment();
		case 1:
			return new RecordImagesFragment();
		case 2:
			return new RecordSeeAlsoFragment();
		}
		return null;
	}

	@Override
	public int getCount() {
		return 3;
	}
	
	@Override
	public CharSequence getPageTitle(int page) {
		int resourceId = -1;
		switch (page) {
		case 0:
			resourceId = R.string.record_tab_details;
			break;
		case 1:
			resourceId = R.string.record_tab_images;
			break;
		case 2:
			resourceId = R.string.record_tab_also;
			break;
		}
		return GuiUtils.getString(mContext, resourceId);
	}
	
}
