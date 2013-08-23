package net.eledge.android.eu.europeana.gui.adapter;

import java.util.ArrayList;
import java.util.List;

import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.gui.fragments.RecordDetailsFragment;
import net.eledge.android.eu.europeana.gui.fragments.RecordImagesFragment;
import net.eledge.android.eu.europeana.gui.fragments.RecordMapFragment;
import net.eledge.android.eu.europeana.gui.fragments.RecordSeeAlsoFragment;
import net.eledge.android.eu.europeana.search.RecordController;
import net.eledge.android.eu.europeana.search.model.record.Record;
import net.eledge.android.toolkit.async.listener.TaskListener;
import net.eledge.android.toolkit.gui.GuiUtils;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class RecordPagerAdapter extends FragmentStatePagerAdapter implements TaskListener<Record>  {
	
	private Context mContext;
	
	private List<Fragment> fragments = new ArrayList<Fragment>();
	private List<Integer> labels = new ArrayList<Integer>();
	
	public RecordPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		mContext = context;
		RecordController._instance.registerListener(getClass(), this);
	}
	
	@Override
	public void onTaskFinished(Record record) {
		// DETAIL INFO
		labels.add(Integer.valueOf(R.string.record_tab_details));
		fragments.add(new RecordDetailsFragment());
		// IMAGES
		labels.add(Integer.valueOf(R.string.record_tab_images));
		fragments.add(new RecordImagesFragment());
		// MAP
		if ( (record.latitude != null) && (record.longitude != null)) {
			labels.add(Integer.valueOf(R.string.record_tab_map));
			fragments.add(new RecordMapFragment());
		}
		// SEE ALSO
		labels.add(Integer.valueOf(R.string.record_tab_also));
		fragments.add(new RecordSeeAlsoFragment());
		
		notifyDataSetChanged();
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
		return GuiUtils.getString(mContext, labels.get(page).intValue());
	}
	
}
 