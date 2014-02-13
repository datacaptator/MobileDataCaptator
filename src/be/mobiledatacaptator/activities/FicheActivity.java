package be.mobiledatacaptator.activities;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.widget.TabHost;
import be.mobiledatacaptator.R;
import be.mobiledatacaptator.adapters.FichePagerAdapter;
import be.mobiledatacaptator.model.Group;
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
		// final Context context = this;

		TabHost tabHost = (TabHost) findViewById(R.id.tabHost_Fiche);
		tabHost.setup();

		for (Group group : unitOfWork.getActiveFiche().getGroups()) {
			group.setId(getUniqueId());
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
