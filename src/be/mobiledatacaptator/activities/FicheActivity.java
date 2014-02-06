package be.mobiledatacaptator.activities;

import java.io.ByteArrayInputStream;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
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
import android.widget.Toast;
import be.mobiledatacaptator.R;
import be.mobiledatacaptator.adapters.FichePagerAdapter;
import be.mobiledatacaptator.fragments.AddTabFragment;
import be.mobiledatacaptator.fragments.TabFragment;
import be.mobiledatacaptator.model.ChoiceItem;
import be.mobiledatacaptator.model.DataField;
import be.mobiledatacaptator.model.Group;
import be.mobiledatacaptator.model.Tab;
import be.mobiledatacaptator.model.TabTemplate;
import be.mobiledatacaptator.model.UIField;
import be.mobiledatacaptator.model.UnitOfWork;
import be.mobiledatacaptator.model.VeldType;
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

		String data = "";

		try {
			for (Group g : unitOfWork.getActiveFiche().getGroups()) {
				for (Tab t : g.getTabs()) {
					for (DataField d : t.getDataFields()) {
						data += d.getName() + ": " + d.getUiField().getValue() + "\r\n";
					}
				}
			}
			Toast.makeText(this, data, Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			MdcUtil.showToastLong(e.getMessage(), this);
		}
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
				Element groupNode = (Element) groups.item(i);
				Group group = new Group(groupNode.getAttribute("Name"));
				if (groupNode.hasAttribute("Expandable")
						&& groupNode.getAttribute("Expandable").toLowerCase(Locale.getDefault()).equals("true"))
					group.setExpandable(true);

				// Tabs toevoegen
				NodeList tabs = groupNode.getElementsByTagName("Tab");
				for (int j = 0; j < tabs.getLength(); j++) {
					Node tabNode = tabs.item(j);
					Tab tab = new Tab(tabNode.getAttributes().getNamedItem("Name").getNodeValue());

					// Velden toevoegen
					NodeList fields = ((Element) tabNode).getElementsByTagName("Field");
					for (int k = 0; k < fields.getLength(); k++) {
						Node fieldNode = fields.item(k);
						NamedNodeMap attr = fieldNode.getAttributes();
						DataField dataField = new DataField();
						if (attr.getNamedItem("Name") != null)
							dataField.setName(attr.getNamedItem("Name").getNodeValue());
						if (attr.getNamedItem("Label") != null)
							dataField.setLabel(attr.getNamedItem("Label").getNodeValue());
						if (attr.getNamedItem("DefaultValue") != null)
							dataField.setDefaultValue(attr.getNamedItem("DefaultValue").getNodeValue());
						if (attr.getNamedItem("Required") != null)
							if (attr.getNamedItem("Required").getNodeValue().equals("Y"))
								dataField.setRequired(true);

						if (attr.getNamedItem("Type") != null) {
							String strType = attr.getNamedItem("Type").getNodeValue();
							if (strType.equals("Text"))
								dataField.setType(VeldType.TEXT);
							if (strType.equals("Choice"))
								dataField.setType(VeldType.CHOICE);
							if (strType.equals("Int"))
								dataField.setType(VeldType.INT);
							if (strType.equals("Double"))
								dataField.setType(VeldType.DOUBLE);
						}

						NodeList temp = ((Element) fieldNode).getElementsByTagName("Choices");
						if (temp.getLength() > 0) {
							NodeList keuzes = ((Element) temp.item(0)).getElementsByTagName("Choice");
							for (int l = 0; l < keuzes.getLength(); l++) {
								Element keuzeNode = (Element) keuzes.item(l);
								dataField.getChoiceItems().add(
										new ChoiceItem(Integer.parseInt(keuzeNode.getAttribute("Idn")), keuzeNode
												.getAttribute("Text")));
							}
						}
						dataField.setUiField(new UIField(this, dataField));
						tab.getDataFields().add(dataField);
					}
					group.getTabs().add(tab);
				}

				// TabTemplate toevoegen
				NodeList tabTemplates = groupNode.getElementsByTagName("TabTemplate");
				if (tabTemplates.getLength() > 0) {
					Node tabTemplateNode = tabTemplates.item(0);
					TabTemplate tabTemplate = new TabTemplate(tabTemplateNode.getAttributes().getNamedItem("Name")
							.getNodeValue());

					// Velden toevoegen
					NodeList fields = ((Element) tabTemplateNode).getElementsByTagName("Field");
					for (int k = 0; k < fields.getLength(); k++) {
						Node fieldNode = fields.item(k);
						NamedNodeMap attr = fieldNode.getAttributes();
						DataField dataField = new DataField();
						if (attr.getNamedItem("Name") != null)
							dataField.setName(attr.getNamedItem("Name").getNodeValue());
						if (attr.getNamedItem("Label") != null)
							dataField.setLabel(attr.getNamedItem("Label").getNodeValue());
						if (attr.getNamedItem("DefaultValue") != null)
							dataField.setDefaultValue(attr.getNamedItem("DefaultValue").getNodeValue());
						if (attr.getNamedItem("Required") != null)
							if (attr.getNamedItem("Required").getNodeValue().equals("Y"))
								dataField.setRequired(true);

						if (attr.getNamedItem("Type") != null) {
							String strType = attr.getNamedItem("Type").getNodeValue();
							if (strType.equals("Text"))
								dataField.setType(VeldType.TEXT);
							if (strType.equals("Choice"))
								dataField.setType(VeldType.CHOICE);
							if (strType.equals("Int"))
								dataField.setType(VeldType.INT);
							if (strType.equals("Double"))
								dataField.setType(VeldType.DOUBLE);
						}

						NodeList temp = ((Element) fieldNode).getElementsByTagName("Choices");
						if (temp.getLength() > 0) {
							NodeList keuzes = ((Element) temp.item(0)).getElementsByTagName("Choice");
							for (int l = 0; l < keuzes.getLength(); l++) {
								Element keuzeNode = (Element) keuzes.item(l);
								dataField.getChoiceItems().add(
										new ChoiceItem(Integer.parseInt(keuzeNode.getAttribute("Idn")), keuzeNode
												.getAttribute("Text")));
							}
						}
						dataField.setUiField(new UIField(this, dataField));
						tabTemplate.getDataFields().add(dataField);
					}
					group.setTabTemplate(tabTemplate);
				}

				unitOfWork.getActiveFiche().getGroups().add(group);
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

		for (final Group group : unitOfWork.getActiveFiche().getGroups()) {
			TabSpec spec = tabHost.newTabSpec(group.getName());
			spec.setIndicator(group.getName());
			spec.setContent(new TabContentFactory() {

				@Override
				public View createTabContent(String tag) {

					ViewPager viewPager = new ViewPager(context);
					viewPager.setId(getUniqueId());

					FichePagerAdapter fichePagerAdapter = new FichePagerAdapter(getSupportFragmentManager());
					viewPager.setAdapter(fichePagerAdapter);

					PagerTitleStrip strip = new PagerTitleStrip(context);
					ViewPager.LayoutParams layoutParams = new ViewPager.LayoutParams();
					layoutParams.height = ViewPager.LayoutParams.WRAP_CONTENT;
					layoutParams.width = ViewPager.LayoutParams.MATCH_PARENT;
					layoutParams.gravity = Gravity.TOP;
					strip.setBackgroundResource(color.darker_gray);
					viewPager.addView(strip, layoutParams);

					for (Tab tab : group.getTabs()) {
						TabFragment fragment = new TabFragment();
						fragment.setTab(tab);
						fichePagerAdapter.addItem(fragment);
					}

					if (group.isExpandable()) {
						AddTabFragment addTabFragment = new AddTabFragment();
						addTabFragment.setFichePagerAdapter(fichePagerAdapter);
						addTabFragment.setGroup(group);
						fichePagerAdapter.addItem(addTabFragment);
					}

					return viewPager;
				}
			});
			tabHost.addTab(spec);
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
