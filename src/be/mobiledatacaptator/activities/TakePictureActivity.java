package be.mobiledatacaptator.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import be.mobiledatacaptator.R;
import be.mobiledatacaptator.model.FotoCategorie;
import be.mobiledatacaptator.model.Project;
import be.mobiledatacaptator.model.UnitOfWork;
import be.mobiledatacaptator.utilities.MdcUtil;

public class TakePictureActivity extends Activity implements OnClickListener, OnItemSelectedListener {

	private Project project;
	private UnitOfWork unitOfWork;
	private EditText editTextFotoName;
	private Button buttonTakePicture, buttonEditFotoName;
	private Spinner spinnerFotoCategories;
	private ListView listViewFotos;
	private Bitmap bitMap;
	final static int cameraData = 0;
	private Intent startCameraIntent;
	private String fotoName;
	private List<String> listFotoNames;
	private List<String> listThisFicheFotoNames;
	private FotoCategorie selectTypeFoto;
	private int spinnerPosition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_take_picture);

		unitOfWork = UnitOfWork.getInstance();
		project = unitOfWork.getActiveProject();

		//fotoName = PUT_3014
		fotoName = getIntent().getExtras().getString("fotoName");

		editTextFotoName = (EditText) findViewById(R.id.editTextFotoName);
		editTextFotoName.setEnabled(false);
		buttonEditFotoName = (Button) findViewById(R.id.buttonEditFotoName);
		buttonEditFotoName.setOnClickListener(this);
		spinnerFotoCategories = (Spinner) findViewById(R.id.spinnerPictureCategories);

		buttonTakePicture = (Button) findViewById(R.id.buttonTakePicture);
		buttonTakePicture.setOnClickListener(this);
		listViewFotos = (ListView) findViewById(R.id.listViewFotos);

		setTitle(MdcUtil.setActivityTitle(unitOfWork, getApplicationContext()));

		// extra code om 'select foto type' aan de spinner te kunnen toevoegen
		selectTypeFoto = new FotoCategorie(getString(R.string.select_foto_type), "_");
		if (!project.getFotoCategories().contains(selectTypeFoto)) {
			project.getFotoCategories().add(selectTypeFoto);
		}

		ArrayAdapter<FotoCategorie> adapter = new ArrayAdapter<FotoCategorie>(this, android.R.layout.simple_spinner_dropdown_item,
				project.getFotoCategories());

		spinnerFotoCategories.setAdapter(adapter);

		spinnerPosition = adapter.getPosition(selectTypeFoto);
		spinnerFotoCategories.setSelection(spinnerPosition);
		spinnerFotoCategories.setOnItemSelectedListener(this);

		loadFotoNames();

		editTextFotoName.setText(fotoName);

	}

	private void loadFotoNames() {
		try {
			listFotoNames = unitOfWork.getDao().getAllFilesFromPathWithExtension(project.getDataLocation(), ".jpg", false);

			listThisFicheFotoNames = new ArrayList<String>();

			for (String myFotoName : listFotoNames) {
				if (myFotoName.startsWith(fotoName)) {
					listThisFicheFotoNames.add(myFotoName);
				}
			}

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listThisFicheFotoNames);
			listViewFotos.setAdapter(adapter);
			listViewFotos.setItemsCanFocus(true);
			listViewFotos.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			listViewFotos.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int indexListItem, long arg3) {
					// String textListItem = (String) listViewFotos.getItemAtPosition(indexListItem);
					// editTextFotoName.setText(fotoName + "_" + textListItem);
				}
			});

		} catch (Exception e) {
			MdcUtil.showToastShort(R.string.LoadFiches_error + e.getMessage(), getApplicationContext());
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

			String myFotoName = editTextFotoName.getText().toString();
			editTextFotoName.setText("");

			myFotoName = myFotoName.startsWith(fotoName) ? myFotoName : fotoName + "_" + myFotoName;

			String file = dir + myFotoName + ".jpg";
			File newfile = new File(file);
			try {
				newfile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

			FileOutputStream fos;

			try {
				fos = new FileOutputStream(newfile);
				bitMap.compress(Bitmap.CompressFormat.JPEG, 85, fos);
				fos.flush();
				fos.close();

				String path = project.getDataLocation();
				
				unitOfWork.getDao().uploadPicture(newfile, path);

				loadFotoNames();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.buttonTakePicture:
			if (editTextFotoName.getText().length() == 0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				String builderMessage = getString(R.string.name_photo_required);
				builder.setMessage(builderMessage).setPositiveButton(R.string.ok, null).show();
			} else {
				startCameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(startCameraIntent, cameraData);
			}
			break;

		case R.id.buttonEditFotoName:
			if (buttonEditFotoName.getText().toString().equalsIgnoreCase(getString(R.string.auto))) {
				buttonEditFotoName.setText(R.string.manual);
				editTextFotoName.setEnabled(false);
				spinnerFotoCategories.setVisibility(View.VISIBLE);
				spinnerFotoCategories.setSelection(spinnerPosition);

			} else {
				buttonEditFotoName.setText(R.string.auto);

				editTextFotoName.setEnabled(true);
				editTextFotoName.setText("");
				editTextFotoName.requestFocus();
				spinnerFotoCategories.setVisibility(View.GONE);
				// show soft keyboard
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

			}
			break;
		default:
			break;
		}

	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View arg1, int itemPosition, long arg3) {

		String tempFotoName = fotoName;
		FotoCategorie selectedFotoCategorie = (FotoCategorie) parent.getItemAtPosition(itemPosition);

		if (selectedFotoCategorie.getSuffix().equals("_")) {
			if (!fotoName.substring(fotoName.length() - 1, fotoName.length()).equals("_")) {
				tempFotoName = fotoName + selectedFotoCategorie.getSuffix();
			}
		} else {
			tempFotoName = fotoName + "_" + selectedFotoCategorie.getSuffix() + "_";
		}

		if (listThisFicheFotoNames.size() == 0) {
			tempFotoName = tempFotoName + "1";
		} else {
			int number = 0;

			if (!selectedFotoCategorie.getSuffix().equals("_")) {
				List<Integer> numbers = new ArrayList<Integer>();
				for (String thisFotoName : listThisFicheFotoNames) {
					if (thisFotoName.startsWith(tempFotoName)) {
						String numberFoto = thisFotoName.substring(tempFotoName.length());

						Pattern p = Pattern.compile("\\d+");
						Matcher m = p.matcher(numberFoto);
						while (m.find()) {
							numbers.add(Integer.valueOf(m.group()));
						}
					}
				}

				try {
					number = Collections.max(numbers) + 1;
				} catch (Exception e) {
					number = 1;
				}
			} else // geen categorie selected
			{
				List<Integer> numbers = new ArrayList<Integer>();
				for (String thisFotoName : listThisFicheFotoNames) {
					if (thisFotoName.startsWith(tempFotoName)) {
						String numberFoto = thisFotoName.substring(tempFotoName.length());
						if (!numberFoto.contains("_")) {
							Pattern p = Pattern.compile("\\d+");
							Matcher m = p.matcher(numberFoto);
							while (m.find()) {
								numbers.add(Integer.valueOf(m.group()));
							}
						}
					}
				}

				try {
					number = Collections.max(numbers) + 1;
				} catch (Exception e) {
					number = 1;
				}
			}

			tempFotoName = tempFotoName + number;

		}

		editTextFotoName.setText(tempFotoName);

		// Hides the soft keyboard - normaal zou ik deze code als laatste bij onCreate zetten ... maar daar werkt het niet
		InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(editTextFotoName.getWindowToken(), 0);

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}
