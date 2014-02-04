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
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import be.mobiledatacaptator.R;
import be.mobiledatacaptator.model.PhotoCategory;
import be.mobiledatacaptator.model.Project;
import be.mobiledatacaptator.model.UnitOfWork;
import be.mobiledatacaptator.utilities.MdcUtil;

public class TakePhotoActivity extends Activity implements OnClickListener, OnItemSelectedListener {

	private Project project;
	private UnitOfWork unitOfWork;
	private ListView listViewFotos;
	private Bitmap bitMap;
	final static int cameraData = 0;
	private Intent startCameraIntent;
	private String prefixFicheFotoName, photoNameToSave;
	private List<String> listFotoNames;
	private List<String> listThisFicheFotoNames;
	private TableLayout tableLayoutPhotoCategory;
	private Button buttonVrijeSuffix, buttonOpenPhoto, buttonDeletePhoto;
	private EditText editTextVrijeSuffix;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_take_photo);

		unitOfWork = UnitOfWork.getInstance();
		project = unitOfWork.getActiveProject();

		// fotoName = PUT3014
		prefixFicheFotoName = getIntent().getExtras().getString("fotoName");

		listViewFotos = (ListView) findViewById(R.id.listViewFotos);
		tableLayoutPhotoCategory = (TableLayout) findViewById(R.id.tableLayoutPhotoCategory);
		buttonVrijeSuffix = (Button) findViewById(R.id.buttonVrijeSuffix);
		buttonVrijeSuffix.setOnClickListener(photoCategoryListener);

		buttonOpenPhoto = (Button) findViewById(R.id.buttonOpenPhoto);
		buttonDeletePhoto = (Button) findViewById(R.id.buttonDeletePhoto);

		editTextVrijeSuffix = (EditText) findViewById(R.id.editTextVrijeSuffix);

		buttonOpenPhoto.setOnClickListener(this);
		buttonDeletePhoto.setOnClickListener(this);

		setTitle(MdcUtil.setActivityTitle(unitOfWork, getApplicationContext()));

		int index = 0;
		for (PhotoCategory photoCat : project.getPhotoCategories()) {
			addPhotoCategoriesToLayout(photoCat, index);
			index++;
		}

		loadFotoNames();

	}

	private void loadFotoNames() {
		try {
			listFotoNames = unitOfWork.getDao().getAllFilesFromPathWithExtension(project.getDataLocation(), ".jpg", false);

			listThisFicheFotoNames = new ArrayList<String>();

			for (String myFotoName : listFotoNames) {
				if (myFotoName.startsWith(prefixFicheFotoName)) {
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

	private void addPhotoCategoriesToLayout(PhotoCategory photoCategorie, int index) {

		// First new_tag_view.xml gets inflated
		// Android provides a service 'getSystemService(Context.LAYOUT_INFLATER_SERVICE)' that enables you to inflate a layout
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View newPhotoCategoryView = inflater.inflate(R.layout.new_table_row_photo_category, null);

		// Get a reference to the newTagButton on the new_tag_view.xml and set its text + register its onClickListener
		Button buttonNewPhotoCategory = (Button) newPhotoCategoryView.findViewById(R.id.buttonNewPhotoCategory);
		buttonNewPhotoCategory.setText(photoCategorie.getName());
		buttonNewPhotoCategory.setTag(photoCategorie.getSuffix());
		buttonNewPhotoCategory.setOnClickListener(photoCategoryListener);

		// Adds programmatically the new_tag_view.xml to the queryTableLayout at the specified index)
		tableLayoutPhotoCategory.addView(newPhotoCategoryView, index);

	}

	// Annonymous Inner Class that implements Interface OnClickListener to respond to the click event
	public OnClickListener photoCategoryListener = new OnClickListener() {

		// Implements the OnclickListener onClick method
		@Override
		public void onClick(View buttonClicked) {

			if (buttonClicked.getId() == R.id.buttonVrijeSuffix) {

				String suffix = editTextVrijeSuffix.getText().toString();
				
				if (suffix.length() > 0) {
					photoNameToSave = prefixFicheFotoName + "_" + suffix + "_";
					photoNameToSave = composePhotoName(photoNameToSave);
			
					startCameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(startCameraIntent, cameraData);
				}
				else
				{
					MdcUtil.showToastShort("Vul een suffix in!", getApplicationContext());
				}
				
			} else {
				TableRow buttonTableRow = (TableRow) buttonClicked.getParent();
				Button buttonNewPhotoCategory = (Button) buttonTableRow.findViewById(R.id.buttonNewPhotoCategory);

				photoNameToSave = prefixFicheFotoName + "_" + buttonNewPhotoCategory.getTag().toString() + "_";
				photoNameToSave = composePhotoName(photoNameToSave);
				startCameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(startCameraIntent, cameraData);
				
			}

			

		}
	};

	private String composePhotoName(String photoNameToSave) {
		int number = 0;
		List<Integer> numbers = new ArrayList<Integer>();
		for (String thisFotoName : listThisFicheFotoNames) {
			if (thisFotoName.startsWith(photoNameToSave)) {
				String numberFoto = thisFotoName.substring(photoNameToSave.length());

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

		photoNameToSave = photoNameToSave + number;

		return photoNameToSave;
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

			String file = dir + photoNameToSave + ".jpg";
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
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// // Hides the soft keyboard - normaal zou ik deze code als laatste bij onCreate zetten ... maar daar werkt het niet
		InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(editTextVrijeSuffix.getWindowToken(), 0);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}
