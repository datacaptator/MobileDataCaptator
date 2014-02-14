package be.mobiledatacaptator.activities;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
import be.mobiledatacaptator.model.DataField;
import be.mobiledatacaptator.model.Fiche;
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
		loadExistingData();
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
		toonValidatieFouten();
		saveFiche();
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

	private void loadExistingData() {
		try {
			Fiche fiche = UnitOfWork.getInstance().getActiveFiche();
			if (unitOfWork.getDao().existsFile(fiche.getPath())) {
				String xml = unitOfWork.getDao().getFilecontent(fiche.getPath());
				DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document dom = db.parse(new ByteArrayInputStream(xml.getBytes()));
				Element root = dom.getDocumentElement();
				fiche.loadExistingData(root);
			}
		} catch (Exception e) {
			MdcUtil.showToastShort(e.getMessage(), this);
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

	private void saveFiche() {
		try {
			Fiche fiche = unitOfWork.getActiveFiche();
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			fiche.appendXml(doc);

			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			String output = writer.getBuffer().toString();
			unitOfWork.getDao().saveStringToFile(fiche.getPath(), output);

		} catch (Exception e) {
			MdcUtil.showToastShort(e.getMessage(), this);
		}
	}

	private void toonValidatieFouten() {
		StringBuilder builder = new StringBuilder();
		for (Group group : unitOfWork.getActiveFiche().getGroups()) {
			for (Tab tab : group.getTabs()) {
				for (DataField dataField : tab.getDataFields()) {
					StringBuilder sb2 = new StringBuilder();
					if (!(dataField.isValide(sb2))) {
						if (builder.length() > 0)
							builder.append("\n");
						builder.append(dataField.getLabel() + ": " + sb2.toString());
					}
				}
			}
		}
		if (builder.length() > 0)
			MdcUtil.showToastLong(builder.toString(), this);
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
