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
import be.mobiledatacaptator.R;
import be.mobiledatacaptator.dao.StartDropBoxApi;
import be.mobiledatacaptator.model.Project;
import be.mobiledatacaptator.model.UnitOfWork;
import be.mobiledatacaptator.utilities.MdcUtil;

public class SelectProjectActivity extends Activity {

	public final static int REQUEST_INITDROPBOX = 1;

	private UnitOfWork unitOfWork;
	private ListView listViewProjects;
	private Button buttonOpenProject = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Hier wordt de dropboxapi gestart.
		Intent intent = new Intent(this, StartDropBoxApi.class);
		startActivityForResult(intent, REQUEST_INITDROPBOX);

	}

	private void start() {
		unitOfWork = UnitOfWork.getInstance();
		// TODO: Nakijken of actief project op null moet worden gezet.
		setContentView(R.layout.activity_select_project);

		listViewProjects = (ListView) findViewById(R.id.listViewProjects);
		buttonOpenProject = (Button) findViewById(R.id.buttonOpenProject);

		loadProjects();

		listViewProjects.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int indexListItem, long arg3) {
				UnitOfWork.getInstance().setActiveProject((Project) listViewProjects.getItemAtPosition(indexListItem));
			}
		});

		buttonOpenProject.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (UnitOfWork.getInstance().getActiveProject() != null) {
					Intent intent = new Intent(v.getContext(), SelectFicheActivity.class);
					startActivity(intent);

				} else {
					MdcUtil.showToastShort(getString(R.string.select_project_first), getApplicationContext());
				}
			}
		});
	}

	private void loadProjects() {
		try {
			ArrayList<Project> projects = new ArrayList<Project>();

			String xml = unitOfWork.getDao().getFilecontent(getString(R.string.dropbox_location_projects));
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document dom = db.parse(new ByteArrayInputStream(xml.getBytes()));

			Element root = dom.getDocumentElement();
			NodeList forms = root.getElementsByTagName("Project");
			for (int i = 0; i < forms.getLength(); i++) {
				Project myProject = new Project();
				Node projectNode = forms.item(i);

				myProject.setName(projectNode.getAttributes().getNamedItem("Name").getNodeValue());
				myProject.setTemplate(projectNode.getAttributes().getNamedItem("Template").getNodeValue());

				projects.add(myProject);
			}

			ArrayAdapter<Project> myAdapter = new ArrayAdapter<Project>(this,
					android.R.layout.simple_list_item_single_choice, projects);

			listViewProjects.setAdapter(myAdapter);
			listViewProjects.setItemsCanFocus(true);
			listViewProjects.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		} catch (Exception e) {
			MdcUtil.showToastLong(R.string.LoadProjects_error + e.getMessage(), getApplicationContext());
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

}
