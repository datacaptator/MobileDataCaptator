package be.mobiledatacaptator.activities;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.R.color;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import be.mobiledatacaptator.R;
import be.mobiledatacaptator.adapters.FichePagerAdapter;
import be.mobiledatacaptator.fragments.AddTabFragment;
import be.mobiledatacaptator.model.Group;
import be.mobiledatacaptator.model.Tab;
import be.mobiledatacaptator.model.UnitOfWork;
import be.mobiledatacaptator.utilities.MdcUtil;

public class FicheActivity extends FragmentActivity {

	private UnitOfWork unitOfWork;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		unitOfWork = UnitOfWork.getInstance();

		setTitle(MdcUtil.setActivityTitle(unitOfWork, getApplicationContext()));

		LoadTemplate();
		toonFiche();
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
	protected void onPause() {
		super.onPause();
		// TEST

		// String data = "";

		// try {
		// for (Group g : unitOfWork.getActiveFiche().getGroups()) {
		// for (Tab t : g.getTabs()) {
		// for (DataField d : t.getDataFields()) {
		// data += d.getName() + ": " + d.getUiField().getValue() + "\r\n";
		// }
		// }
		// }
		// Toast.makeText(this, data, Toast.LENGTH_LONG).show();
		// } catch (Exception e) {
		// MdcUtil.showToastLong(e.getMessage(), this);
		// }
	}

	@SuppressLint("DefaultLocale")
	private void LoadTemplate() {
		try {
			String xml = unitOfWork.getDao().getFilecontent(unitOfWork.getActiveProject().getTemplate());

			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document dom = db.parse(new ByteArrayInputStream(xml.getBytes()));

			Element root = dom.getDocumentElement();

			// Groepen toevoegen
			NodeList groups = root.getElementsByTagName("Group");
			for (int i = 0; i < groups.getLength(); i++) {
				Element groupEle = (Element) groups.item(i);
				unitOfWork.getActiveFiche().getGroups()
						.add(new Group(this, groupEle, new FichePagerAdapter(getSupportFragmentManager())));
			}

		} catch (Exception e) {
			MdcUtil.showToastLong(e.getMessage(), this);
		}

	}

	private void toonFiche() {

		setContentView(R.layout.activity_fiche);
		final Context context = this;

		TabHost tabHost = (TabHost) findViewById(R.id.tabHost_Fiche);
		tabHost.setup();

		for (Group group : unitOfWork.getActiveFiche().getGroups()) {
			group.setId(getUniqueId());
//			TabSpec spec = tabHost.newTabSpec(group.getName());
//			spec.setIndicator(group.getName());
//			spec.setContent(new TabContentFactory() {
//
//				@Override
//				public View createTabContent(String tag) {
//					group.setId(getUniqueId());
//					FichePagerAdapter fichePagerAdapter = new FichePagerAdapter(getSupportFragmentManager());
//					group.setAdapter(fichePagerAdapter);
//
//					PagerTitleStrip strip = new PagerTitleStrip(context);
//					ViewPager.LayoutParams layoutParams = new ViewPager.LayoutParams();
//					layoutParams.height = ViewPager.LayoutParams.WRAP_CONTENT;
//					layoutParams.width = ViewPager.LayoutParams.MATCH_PARENT;
//					layoutParams.gravity = Gravity.TOP;
//					strip.setBackgroundResource(color.darker_gray);
//					group.addView(strip, layoutParams);
//
//					for (Tab tab : group.getTabs()) {
//						fichePagerAdapter.addItem(tab);
//					}
//
//					if (group.isExpandable()) {
//						AddTabFragment addTabFragment = new AddTabFragment();
//						addTabFragment.setFichePagerAdapter(fichePagerAdapter);
//						addTabFragment.setGroup(group);
//						fichePagerAdapter.addItem(addTabFragment);
//					}
//
//					return group;
//				}
//			});
			tabHost.addTab(group.getTabSpec(tabHost));
		}

	}

	private int getUniqueId() {
		int i = 0;
		Boolean isUnique = false;
		do {
			i++;
			if (findViewById(i) == null)
				isUnique = true;
		} while (!(isUnique));

		return i;
	}

}
