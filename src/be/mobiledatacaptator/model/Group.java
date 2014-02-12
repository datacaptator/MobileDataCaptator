package be.mobiledatacaptator.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.R.color;
import android.content.Context;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import be.mobiledatacaptator.adapters.FichePagerAdapter;

public class Group extends ViewPager {

	private String name;
	private boolean expandable;
	private List<Tab> tabs = new ArrayList<Tab>();

	private FichePagerAdapter fichePagerAdapter;

	private Element xmlTemplate;

	public Group(Context context, Element xml, FichePagerAdapter adapter) {
		super(context);
		xmlTemplate = xml;
		fichePagerAdapter = adapter;
		setAdapter(fichePagerAdapter);
		loadTemplate();
	}

	private void loadTemplate() {
		// Velden invullen aan hand van xml element.
		if (xmlTemplate.hasAttribute("Name"))
			name = xmlTemplate.getAttribute("Name");
		if (xmlTemplate.hasAttribute("Expandable")
				&& xmlTemplate.getAttribute("Expandable").toLowerCase(Locale.getDefault()).equals("true"))
			expandable = true;
		NodeList tabsNodes = xmlTemplate.getElementsByTagName("Tab");
		for (int i = 0; i < tabsNodes.getLength(); i++) {
			Tab tab = new Tab();
			tab.setContext(getContext());
			tab.setXmlTemplate((Element) tabsNodes.item(i));
			tabs.add(tab);
		}
	}

	public TabSpec getTabSpec(TabHost tabHost) {
		TabSpec spec = tabHost.newTabSpec(name);
		spec.setIndicator(name);
		final ViewPager viewPager = this;
		spec.setContent(new TabContentFactory() {

			@Override
			public View createTabContent(String tag) {

				PagerTitleStrip strip = new PagerTitleStrip(getContext());
				ViewPager.LayoutParams layoutParams = new ViewPager.LayoutParams();
				layoutParams.height = ViewPager.LayoutParams.WRAP_CONTENT;
				layoutParams.width = ViewPager.LayoutParams.MATCH_PARENT;
				layoutParams.gravity = Gravity.TOP;
				strip.setBackgroundResource(color.darker_gray);
				addView(strip, layoutParams);

				for (Tab tab : tabs) {
					fichePagerAdapter.addItem(tab);
				}

				// if (group.isExpandable()) {
				// AddTabFragment addTabFragment = new AddTabFragment();
				// addTabFragment.setFichePagerAdapter(fichePagerAdapter);
				// addTabFragment.setGroup(group);
				// fichePagerAdapter.addItem(addTabFragment);
				// }
				return viewPager;
			}
		});
		return spec;
	}

}
