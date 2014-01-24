package be.mobiledatacaptator.activities;

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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import be.mobiledatacaptator.R;
import be.mobiledatacaptator.model.Fiche;
import be.mobiledatacaptator.model.Project;
import be.mobiledatacaptator.model.UnitOfWork;
import be.mobiledatacaptator.utilities.MdcUtil;

public class ProjectActivity extends Activity implements OnClickListener {

	private Project project;

	private ListView listViewFiches;
	private EditText editTextActiveFiche;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project);

		project = UnitOfWork.getInstance().getActiveProject();
		setTitle(project.getName());

		listViewFiches = (ListView) findViewById(R.id.listViewFiches);
		editTextActiveFiche = (EditText) findViewById(R.id.editTextActiveFiche);
		Button buttonAddNumber = (Button) findViewById(R.id.buttonAddNumber);
		Button buttonOpenFiche = (Button) findViewById(R.id.buttonOpenFiche);
		Button buttonOpenFoto = (Button) findViewById(R.id.buttonOpenFoto);
		Button buttonOpenSchets = (Button) findViewById(R.id.buttonOpenSchets);

		buttonAddNumber.setOnClickListener(this);
		buttonOpenFiche.setOnClickListener(this);
		buttonOpenFoto.setOnClickListener(this);
		buttonOpenSchets.setOnClickListener(this);

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

	private void loadDataFiches() {
		try {
			List<String> listDataFicheNamen = UnitOfWork.getInstance().getDao()
					.getAllFilesFromPathWithExtension(project.getDataLocation(), ".xml", false);

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, listDataFicheNamen);
			listViewFiches.setAdapter(adapter);
			listViewFiches.setItemsCanFocus(true);
			listViewFiches.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			listViewFiches.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int indexListItem, long arg3) {
					String textListItem = (String) listViewFiches.getItemAtPosition(indexListItem);

					Fiche fiche = new Fiche();
					fiche.setName(textListItem);
					fiche.setPath(project.getDataLocation() + textListItem + ".xml");
					UnitOfWork.getInstance().setActiveFiche(fiche);

					textListItem = textListItem.substring(project.getFilePrefix().length());
					editTextActiveFiche.setText(textListItem);
				}
			});

		} catch (Exception e) {
			MdcUtil.showToastShort(e.getMessage(), getApplicationContext());
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.buttonAddNumber:
			increaseFicheNumber();
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

	private void increaseFicheNumber() {
		String input = editTextActiveFiche.getText().toString();
		String result = input;
		Pattern p = Pattern.compile("[0-9]+$");
		Matcher m = p.matcher(input);
		if (m.find()) {
			result = m.group();
			int t = Integer.parseInt(result);
			result = input.substring(0, input.length() - result.length()) + ++t;

			Fiche fiche = new Fiche();
			fiche.setName(project.getFilePrefix() + result);
			fiche.setPath(project.getDataLocation() + fiche.getName() + ".xml");
			UnitOfWork.getInstance().setActiveFiche(fiche);

			editTextActiveFiche.setText(result);
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
					builder.setMessage(R.string.open_existing_fiche).setPositiveButton(R.string.Yes, dialogClickListener)
							.setNegativeButton(R.string.No, dialogClickListener).show();
				} else {
					startActivity(intent);
				}

			} else {
				MdcUtil.showToastShort(getString(R.string.no_fiche_selected), getApplicationContext());
			}
		} catch (Exception e) {
			MdcUtil.showToastShort(e.getLocalizedMessage(), getApplicationContext());
		}
	}

}
