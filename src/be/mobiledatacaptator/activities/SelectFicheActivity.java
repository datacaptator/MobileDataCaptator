package be.mobiledatacaptator.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import be.mobiledatacaptator.R;
import be.mobiledatacaptator.TakePictureActivity;
import be.mobiledatacaptator.model.Fiche;
import be.mobiledatacaptator.model.Project;
import be.mobiledatacaptator.model.UnitOfWork;
import be.mobiledatacaptator.utilities.MdcUtil;

import com.dropbox.sync.android.DbxPath.InvalidPathException;

public class SelectFicheActivity extends Activity implements OnClickListener {

	private Project project;
	private ListView listViewFiches;
	private UnitOfWork unitOfWork;

	// foto
	private Bitmap bitMap;
	final static int cameraData = 0;
	private Intent myFotoIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_fiche);

		unitOfWork = UnitOfWork.getInstance();
		project = unitOfWork.getActiveProject();

		setTitle(project.getName());

		listViewFiches = (ListView) findViewById(R.id.listViewFiches);

		Button buttonAddNumber = (Button) findViewById(R.id.buttonAddNumber);
		Button buttonOpenFiche = (Button) findViewById(R.id.buttonOpenFiche);
		Button buttonOpenFoto = (Button) findViewById(R.id.buttonOpenFoto);
		Button buttonOpenSchets = (Button) findViewById(R.id.buttonOpenSchets);

		buttonAddNumber.setOnClickListener(this);
		buttonOpenFiche.setOnClickListener(this);
		buttonOpenFoto.setOnClickListener(this);
		buttonOpenSchets.setOnClickListener(this);

		loadDataFiches();

		// foto
		InputStream inputStream = getResources().openRawResource(R.drawable.ic_launcher);
		bitMap = BitmapFactory.decodeStream(inputStream);

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
			List<String> listDataFicheNamen = unitOfWork.getDao().getAllFilesFromPathWithExtension(project.getDataLocation(), ".xml", false);

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, listDataFicheNamen);
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

					unitOfWork.setActiveFiche(fiche);

					setTitle(MdcUtil.setActivityTitle(unitOfWork, getApplicationContext()));
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
			// Toast.makeText(getApplicationContext(), "OpenFoto pressed", Toast.LENGTH_SHORT).show();
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
		String ficheName = unitOfWork.getActiveFiche().getName();
		String input = ficheName.substring(project.getFilePrefix().length());
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

			setTitle(project.getName() + " - " + getString(R.string.fiche) + " " + result);
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
					String builderMessage = getString(R.string.wil_u_fiche) + " " + ficheName + " " + getString(R.string.openen);
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
								startActivity(takePictureIntent);
								break;
							case DialogInterface.BUTTON_NEGATIVE:
								// No button clicked
								break;
							}
						}
					};

					AlertDialog.Builder builder = new AlertDialog.Builder(this);

					String ficheName = unitOfWork.getActiveFiche().getName();
					String builderMessage = getString(R.string.wil_u_aan_fiche) + " " + ficheName + " " + getString(R.string.een_foto_toevoegen);
					builder.setNegativeButton(R.string.no, dialogClickListener).setMessage(builderMessage)
							.setPositiveButton(R.string.yes, dialogClickListener).show();
				} else {
					//startActivity(intent);
				}

			} else {
				MdcUtil.showToastShort(getString(R.string.select_fiche_first), getApplicationContext());
			}
		} catch (Exception e) {
			MdcUtil.showToastShort(e.getLocalizedMessage(), getApplicationContext());
		}
	}

	
	// TODO - code verder te verfijnen -> upload to dropbox werkt!
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			Bundle extrasBundle = data.getExtras();

			bitMap = (Bitmap) extrasBundle.get("data");

			final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
			File newdir = new File(dir);
			newdir.mkdirs();

			String file = dir + "myFile.jpg";
			File newfile = new File(file);
			try {
				newfile.createNewFile();
			} catch (IOException e) {
			}

			FileOutputStream fos;
			try {
				fos = new FileOutputStream(newfile);
				bitMap.compress(Bitmap.CompressFormat.JPEG, 85, fos);
				fos.flush();
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			try {
				MdcUtil.showToastShort("net voor dao!", getApplicationContext());	
				String path = project.getDataLocation();
				MdcUtil.showToastShort(project.getDataLocation(), getApplicationContext());
								
				unitOfWork.getDao().uploadPicture(newfile, path);
				//unitOfWork.getDao().uploadPicture(newfile, "DataCaptator/AppData/Pidpa/Bonheiden");
				//unitOfWork.getDao().uploadPicture(newfile);
			} catch (InvalidPathException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			

		}

	}

}
