package be.mobiledatacaptator.activities;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import be.mobiledatacaptator.R;
import be.mobiledatacaptator.adapters.FichePagerAdapter;
import be.mobiledatacaptator.model.UnitOfWork;

public class FicheActivity extends FragmentActivity implements TabListener {

	private ViewPager viewPager;
	private FichePagerAdapter fichePagerAdapter;
	private ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fiche);

		setTitle(UnitOfWork.getInstance().getActiveFiche().getNaam());

		viewPager = (ViewPager) findViewById(R.id.pager);
		fichePagerAdapter = new FichePagerAdapter(getSupportFragmentManager());
		viewPager.setAdapter(fichePagerAdapter);

		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		test();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return (true);
		}

		return (super.onOptionsItemSelected(item));
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {

	}

	// test
	private void test() {
		String[] testData = { "eerste", "tweede", "derde", "Vierde", "vijfde",
				"zesde" };

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				actionBar.setSelectedNavigationItem(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		for (final String s : testData) {
			actionBar
					.addTab(actionBar.newTab().setText(s).setTabListener(this));

			TestFragment testFragment = new TestFragment();

			Bundle args = new Bundle();
			args.putCharSequence(TestFragment.ARG_OBJECT, s);
			testFragment.setArguments(args);

			fichePagerAdapter.addItem(testFragment);
		}

	}

	public static class TestFragment extends Fragment {

		public static final String ARG_OBJECT = "object";

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			Bundle args = getArguments();
			TextView textView = new TextView(getActivity());
			textView.setText(args.getCharSequence(ARG_OBJECT) + " Fiche");
			return textView;
		}

	}

}
