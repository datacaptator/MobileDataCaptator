package be.mobiledatacaptator.activities;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import be.mobiledatacaptator.R;
import be.mobiledatacaptator.model.Fiche;
import be.mobiledatacaptator.model.FotoCategorie;
import be.mobiledatacaptator.model.Project;
import be.mobiledatacaptator.model.UnitOfWork;
import be.mobiledatacaptator.utilities.MdcUtil;

public class SelectFicheActivity extends Activity implements OnClickListener {

	private Project project;
	private ListView listViewFiches;
	private UnitOfWork unitOfWork;
	private String ficheName = "";
	Button buttonAddNumber;
	Button buttonOpenFiche;
	Button buttonOpenFoto;
	Button buttonOpenSchets;
	EditText editTextFicheName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_fiche);

		unitOfWork = UnitOfWork.getInstance();
		project = unitOfWork.getActiveProject();

		setTitle(project.getName());

		listViewFiches = (ListView) findViewById(R.id.listViewFiches);

		buttonAddNumber = (Button) findViewById(R.id.buttonAddNumber);
		buttonOpenFiche = (Button) findViewById(R.id.buttonOpenFiche);
		buttonOpenFoto = (Button) findViewById(R.id.buttonOpenFoto);
		buttonOpenSchets = (Button) findViewById(R.id.buttonOpenSchets);
		editTextFicheName = (EditText) findViewById(R.id.editTextFicheName);

		buttonAddNumber.setOnClickListener(this);
		buttonOpenFiche.setOnClickListener(this);
		buttonOpenFoto.setOnClickListener(this);
		buttonOpenSchets.setOnClickListener(this);

		loadProjectData();
		loadDataFiches();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			UnitOfWork.getInstance().setActiveFiche(null);
			finish();
			return (true);
		}

		return (super.onOptionsItemSelected(item));
	}

	private void loadProjectData() {
		try {
			String xml = unitOfWork.getDao().getFilecontent(unitOfWork.getActiveProject().getTemplate());

			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document dom = db.parse(new ByteArrayInputStream(xml.getBytes()));

			Element root = dom.getDocumentElement();

			project.setDataLocation(root.getAttribute("DataLocatie"));
			project.setFilePrefix(root.getAttribute("FilePrefix"));

			project.setLoadFotoActivity(true);
			if (!(root.getAttribute("LoadFotoActivity").equals("true"))) {
				project.setLoadFotoActivity(false);
				buttonOpenFoto.setVisibility(View.INVISIBLE);
			}

			project.setLoadSchetsActivity(true);
			if (!(root.getAttribute("LoadSchetsActivity").equals("true"))) {
				project.setLoadSchetsActivity(false);
				buttonOpenSchets.setVisibility(View.INVISIBLE);
			}

			//TODO - Robrecht zou je onderstaande code altijd uitvoeren of enkel als loadFotoActivity = true
			NodeList nodes = root.getElementsByTagName("FotoCategorie");
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				project.getFotoCategories().add(
						new FotoCategorie(((Element) node).getAttribute("Name"), ((Element) node)
								.getAttribute("Suffix")));
			}

		} catch (Exception e) {
			MdcUtil.showToastShort(e.getMessage(), this);
		}
	}

	private void loadDataFiches() {
		try {
			List<String> listDataFicheNamen = unitOfWork.getDao().getAllFilesFromPathWithExtension(
					project.getDataLocation(), ".xml", false);

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
					listDataFicheNamen);
			listViewFiches.setAdapter(adapter);
			listViewFiches.setItemsCanFocus(true);
			listViewFiches.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			listViewFiches.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int indexListItem, long arg3) {
					String textListItem = (String) listViewFiches.getItemAtPosition(indexListItem);
					editTextFicheName.setText(textListItem.substring(project.getFilePrefix().length()));
				}
			});

		} catch (Exception e) {
			MdcUtil.showToastShort(R.string.LoadFiches_error + e.getMessage(), getApplicationContext());
		}
	}

	@Override
	public void onClick(View v) {
		ficheName = editTextFicheName.getText().toString();
		
		unitOfWork.setActiveFiche(null);

		if (ficheName != null && !(ficheName.equals(""))) {
			Fiche fiche = new Fiche();
			fiche.setName(project.getFilePrefix() + ficheName);
			fiche.setPath(project.getDataLocation() + fiche.getName() + ".xml");
			unitOfWork.setActiveFiche(fiche);
		
			//TODO - Moet er steeds fichenaam opgegeven worden? - indien ja, hier het switch statement 
		}
		else{
			//TODO -  Hier AlertDialog indien geen fiche geselecteerd!
			
		}

		switch (v.getId()) {

		case R.id.buttonAddNumber:
			increaseFicheNumber();
			break;

		case R.id.buttonOpenFiche:
			openFiche();
			break;

		case R.id.buttonOpenFoto:
			openFoto();
			break;

		case R.id.buttonOpenSchets:
			Toast.makeText(getApplicationContext(), "OpenSchets pressed", Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}
	}

	private void increaseFicheNumber() {
		if (ficheName != null && !(ficheName.equals(""))) {
			
			String input = ficheName;
			String result = input;
			Pattern p = Pattern.compile("[0-9]+$");
			Matcher m = p.matcher(input);
			if (m.find()) {
				result = m.group();
				int t = Integer.parseInt(result);
				result = input.substring(0, input.length() - result.length()) + ++t;
				editTextFicheName.setText(result);
			} else {
				editTextFicheName.setText(result + "1");
			}

		}
	}

	private void openFiche() {
		try {
			if (UnitOfWork.getInstance().getActiveFiche() != null) {

				final Intent intent = new Intent(this, FicheActivity.class);
				if (unitOfWork.getDao().existsFile(UnitOfWork.getInstance().getActiveFiche().getPath())) {
					DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							// TODO : programma crasht als je bij terugkeer uit
							// een fiche, en je deze fiche onmiddellijk opnieuw
							// tracht te openen
							case DialogInterface.BUTTON_POSITIVE:
								startActivity(intent);
								break;
							case DialogInterface.BUTTON_NEGATIVE:
								// No button clicked
								break;
							}
						}
					};

					AlertDialog.Builder builder = new AlertDialog.Builder(this);

					String ficheName = unitOfWork.getActiveFiche().getName();
					String builderMessage = getString(R.string.wil_u_fiche) + " " + ficheName + " "
							+ getString(R.string.openen);
					builder.setNegativeButton(R.string.no, dialogClickListener).setMessage(builderMessage)
							.setPositiveButton(R.string.yes, dialogClickListener).show();
				} else {
					startActivity(intent);
				}

			} else {
				MdcUtil.showToastShort(getString(R.string.select_fiche_first), getApplicationContext());
			}
		} catch (Exception e) {
			MdcUtil.showToastShort(e.getLocalizedMessage(), getApplicationContext());
		}
	}

	private void openFoto() {
		try {
			if (UnitOfWork.getInstance().getActiveFiche() != null) {

				
				final Intent takePictureIntent = new Intent(this, TakePictureActivity.class);
				if (unitOfWork.getDao().existsFile(UnitOfWork.getInstance().getActiveFiche().getPath())) {
					DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							// TODO : programma crasht als je bij terugkeer uit
							// een fiche, en je deze fiche onmiddellijk opnieuw
							// tracht te openen
							case DialogInterface.BUTTON_POSITIVE:
								takePictureIntent.putExtra("fotoName", project.getFilePrefix() + "_" + ficheName);
								startActivity(takePictureIntent);
								
								break;
							case DialogInterface.BUTTON_NEGATIVE:
								// No button clicked
								break;
							}
						}
					};

					AlertDialog.Builder builder = new AlertDialog.Builder(this);

					
					String builderMessage = getString(R.string.wil_u_aan_fiche) + " " + ficheName + " "
							+ getString(R.string.een_foto_toevoegen);
					builder.setNegativeButton(R.string.no, dialogClickListener).setMessage(builderMessage)
							.setPositiveButton(R.string.yes, dialogClickListener).show();
				} else {
					// startActivity(intent);
				}

			} else {
				MdcUtil.showToastShort(getString(R.string.select_fiche_first), getApplicationContext());
			}
		} catch (Exception e) {
			MdcUtil.showToastShort(e.getLocalizedMessage(), getApplicationContext());
		}
	}

}
