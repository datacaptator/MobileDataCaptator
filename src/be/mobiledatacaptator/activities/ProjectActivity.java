package be.mobiledatacaptator.activities;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import be.mobiledatacaptator.model.Project;
import be.mobiledatacaptator.model.UnitOfWork;

public class ProjectActivity extends Activity implements OnClickListener {

	private Project project;

	private ListView listViewFiches;
	private EditText editTextActieveFiche;
	private Button buttonAddNumber;
	private Button buttonOpenFiche;
	private Button buttonOpenFoto;
	private Button buttonOpenSchets;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project);

		project = UnitOfWork.getInstance().getActiveProject();
		setTitle(project.getNaam());

		listViewFiches = (ListView) findViewById(R.id.listViewFiches);
		editTextActieveFiche = (EditText) findViewById(R.id.editTextActieveFiche);
		buttonAddNumber = (Button) findViewById(R.id.buttonAddNumber);
		buttonOpenFiche = (Button) findViewById(R.id.buttonOpenFiche);
		buttonOpenFoto = (Button) findViewById(R.id.buttonOpenFoto);
		buttonOpenSchets = (Button) findViewById(R.id.buttonOpenSchets);
		buttonAddNumber.setOnClickListener(this);
		buttonOpenFiche.setOnClickListener(this);
		buttonOpenFoto.setOnClickListener(this);
		buttonOpenSchets.setOnClickListener(this);

		laadDataFiches();
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

	private void laadDataFiches() {
		try {
			List<String> listDataFicheNamen = UnitOfWork.getInstance().getDao()
					.getAllFilesFromPathWithExtension(project.getDatalocatie(), ".xml", false);

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_activated_1, listDataFicheNamen);
			listViewFiches.setAdapter(adapter);
			listViewFiches.setItemsCanFocus(true);
			listViewFiches.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

			listViewFiches.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					String tekst = (String) listViewFiches.getItemAtPosition(arg2);

					Fiche fiche = new Fiche();
					fiche.setNaam(tekst);
					fiche.setPath(project.getDatalocatie() + tekst + ".xml");
					UnitOfWork.getInstance().setActiveFiche(fiche);

					tekst = tekst.substring(project.getFilePrefix().length());
					editTextActieveFiche.setText(tekst);
				}
			});

		} catch (Exception e) {
			toonBoodschap(e.getMessage());
		}
	}

	private void toonBoodschap(String boodschap) {

		if (boodschap == null || boodschap == "") {
			boodschap = "Niet nader omschreven fout";
		}
		Toast.makeText(getApplicationContext(), boodschap, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.buttonAddNumber:
			verhoogFicheNummer();
			break;

		case R.id.buttonOpenFiche:
			openFiche();
			break;

		case R.id.buttonOpenFoto:
			Toast.makeText(getApplicationContext(), "OpenFoto pressed", Toast.LENGTH_SHORT).show();
			break;

		case R.id.buttonOpenSchets:
			Toast.makeText(getApplicationContext(), "OpenSchets pressed", Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}
	}

	private void verhoogFicheNummer() {
		String input = editTextActieveFiche.getText().toString();
		String result = input;
		Pattern p = Pattern.compile("[0-9]+$");
		Matcher m = p.matcher(input);
		if (m.find()) {
			result = m.group();
			int t = Integer.parseInt(result);
			result = input.substring(0, input.length() - result.length()) + ++t;

			Fiche fiche = new Fiche();
			fiche.setNaam(project.getFilePrefix() + result);
			fiche.setPath(project.getDatalocatie() + fiche.getNaam() + ".xml");
			UnitOfWork.getInstance().setActiveFiche(fiche);

			editTextActieveFiche.setText(result);
		}
	}

	private void openFiche() {
		try {
			if (UnitOfWork.getInstance().getActiveFiche() != null) {

				final Intent intent = new Intent(this, FicheActivity.class);

				if (UnitOfWork.getInstance().getDao().existsFile(UnitOfWork.getInstance().getActiveFiche().getPath())) {
					DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
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
					builder.setMessage(R.string.BestaandeFicheOpenen)
							.setPositiveButton(R.string.Yes, dialogClickListener)
							.setNegativeButton(R.string.No, dialogClickListener).show();
				} else {
					startActivity(intent);
				}

			} else {
				Toast.makeText(getApplicationContext(), R.string.NoSelectedFiche, Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			toonBoodschap(e.getMessage());
		}
	}

}
