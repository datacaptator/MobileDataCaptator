package be.mobiledatacaptator.adapters;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class FichePagerAdapter extends FragmentPagerAdapter {

	private List<Fragment> fragments;

	public FichePagerAdapter(FragmentManager fm) {
		super(fm);
		fragments = new ArrayList<Fragment>();
	}

	@Override
	public Fragment getItem(int arg0) {
		return fragments.get(arg0);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	public void addItem(Fragment fragment) {
		fragments.add(fragment);
		notifyDataSetChanged();
	}

}
