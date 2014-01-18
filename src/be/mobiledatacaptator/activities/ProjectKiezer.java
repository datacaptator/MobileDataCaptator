package be.mobiledatacaptator.activities;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import be.mobiledatacaptator.R;
import be.mobiledatacaptator.dao.StartDropBoxApi;
import be.mobiledatacaptator.model.Project;
import be.mobiledatacaptator.model.UnitOfWork;

public class ProjectKiezer extends Activity {

	public final static int REQUEST_INITDROPBOX = 1;

	private UnitOfWork unitOfWork;
	private ListView listViewProjecten;
	private Button btnOpenProject;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Hier wordt de dropboxapi gestart.
		Intent intent = new Intent(this, StartDropBoxApi.class);
		startActivityForResult(intent, REQUEST_INITDROPBOX);

	}

	private void start() {
		unitOfWork = UnitOfWork.getInstance();

		setContentView(R.layout.activity_project_kiezer);

		listViewProjecten = (ListView) findViewById(R.id.listViewProjecten);
		btnOpenProject = (Button) findViewById(R.id.buttonOpenProject);

		laadProjecten();

		listViewProjecten.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				UnitOfWork.getInstance().setActiveProject((Project) listViewProjecten.getItemAtPosition(arg2));
			}
		});

		btnOpenProject.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UnitOfWork.getInstance().getActiveProject() != null) {
					Intent intent = new Intent(v.getContext(), ProjectActivity.class);
					startActivity(intent);
				} else {
					toonBoodschap(v.getContext().getString(R.string.EerstProjectSelecteren));
				}
			}
		});
	}

	private void laadProjecten() {
		try {
			ArrayList<Project> projecten = new ArrayList<Project>();

			String xml = unitOfWork.getDao().getFilecontent("DataCaptator/AppData/Projects.xml");

			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document dom = db.parse(new ByteArrayInputStream(xml.getBytes()));

			Element root = dom.getDocumentElement();
			NodeList forms = root.getElementsByTagName("Project");
			for (int i = 0; i < forms.getLength(); i++) {
				Project myProject = new Project();
				Node n = forms.item(i);

				myProject.setNaam(n.getAttributes().getNamedItem("Naam").getNodeValue());

				NodeList listChilds = n.getChildNodes();
				for (int j = 0; j < listChilds.getLength(); j++) {
					Node child = listChilds.item(j);
					if (child.getNodeType() == Node.ELEMENT_NODE) {
						String childName = child.getNodeName();
						String childValue = ((Element) child).getTextContent();
						if (childName.equalsIgnoreCase("FilePrefix")) {
							myProject.setFilePrefix(childValue);
						}
						if (childName.equalsIgnoreCase("DataLocatie")) {
							if (!(childValue.endsWith("/")))
								childValue += "/";
							myProject.setDatalocatie(childValue);
						}
						if (childName.equalsIgnoreCase("Template")) {
							myProject.setTemplate(childValue);
						}
					}
				}
				projecten.add(myProject);
			}

			ArrayAdapter<Project> myAdapter = new ArrayAdapter<Project>(this,
					android.R.layout.simple_list_item_single_choice, projecten);

			listViewProjecten.setAdapter(myAdapter);
			listViewProjecten.setItemsCanFocus(true);
			listViewProjecten.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		} catch (Exception e) {
			e.printStackTrace();
			toonBoodschap(e.getLocalizedMessage());
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_INITDROPBOX) {
			start();
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void toonBoodschap(String boodschap) {
		if (boodschap == null || boodschap == "") {
			boodschap = "Niet nader omschreven fout";
		}
		Toast.makeText(getApplicationContext(), boodschap, Toast.LENGTH_SHORT).show();
	}
	
	

}
