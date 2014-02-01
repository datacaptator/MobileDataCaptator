package be.mobiledatacaptator.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
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
	private Button buttonTakePicture;
	private Spinner spinnerFotoCategories;
	private ListView listViewFotos;
	private Bitmap bitMap;
	final static int cameraData = 0;
	private Intent startCameraIntent;
	private String fotoName;
	private List<String> listFotoNames;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_take_picture);

		
		unitOfWork = UnitOfWork.getInstance();
		project = unitOfWork.getActiveProject();
		
		fotoName =  getIntent().getExtras().getString("fotoName");
		
		editTextFotoName = (EditText) findViewById(R.id.editTextFotoName);
		spinnerFotoCategories = (Spinner) findViewById(R.id.spinnerPictureCategories);
		buttonTakePicture = (Button) findViewById(R.id.buttonTakePicture);
		listViewFotos = (ListView) findViewById(R.id.listViewFotos);
		
		buttonTakePicture.setOnClickListener(this);
		
		setTitle(MdcUtil.setActivityTitle(unitOfWork, getApplicationContext()));
		
		//extra code om 'select foto type' aan de spinner te kunnen toevoegen
		FotoCategorie selectTypeFoto = new FotoCategorie(getString(R.string.select_foto_type), "_");
		if (!project.getFotoCategories().contains(selectTypeFoto)) {
			project.getFotoCategories().add(selectTypeFoto);
		} 
				
		ArrayAdapter<FotoCategorie> adapter = new ArrayAdapter<FotoCategorie>(this,
				android.R.layout.simple_spinner_dropdown_item, project.getFotoCategories());
		
		spinnerFotoCategories.setAdapter(adapter);
		
		int spinnerPosition = adapter.getPosition(selectTypeFoto);
		spinnerFotoCategories.setSelection(spinnerPosition);
		spinnerFotoCategories.setOnItemSelectedListener(this);

		MdcUtil.showToastShort(fotoName, getApplicationContext());
		
		loadFotoNames();

		editTextFotoName.setText(fotoName);
	
	}

	private void loadFotoNames() {
		try {
			listFotoNames = unitOfWork.getDao().getAllFilesFromPathWithExtension(
					project.getDataLocation(), ".jpg", false);

			List<String> startsWithFotoNameList = new ArrayList<String>();
			
			for (String myFotoName : listFotoNames) {
				if (myFotoName.startsWith(fotoName)) {
					startsWithFotoNameList.add(myFotoName);
				}
			}
					
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
					startsWithFotoNameList);
			listViewFotos.setAdapter(adapter);
			listViewFotos.setItemsCanFocus(true);
			listViewFotos.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			listViewFotos.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int indexListItem, long arg3) {
				//String textListItem = (String) listViewFotos.getItemAtPosition(indexListItem);
				//editTextFotoName.setText(fotoName + "_" + textListItem);
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

			String file = dir + editTextFotoName.getText().toString()+ ".jpg";
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

				MdcUtil.showToastShort("net voor dao!", getApplicationContext());
				String path = project.getDataLocation();
				MdcUtil.showToastShort(project.getDataLocation(), getApplicationContext());

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
			startCameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(startCameraIntent, cameraData);
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
			if (!fotoName.substring(fotoName.length()-1, fotoName.length()).equals("_")) {
				tempFotoName = fotoName + selectedFotoCategorie.getSuffix();
			}
		}
		else
		{
			tempFotoName = fotoName + "_" + selectedFotoCategorie.getSuffix() + "_";
		}
		
		
		
		editTextFotoName.setText(tempFotoName);
		
		//Hides the soft keyboard - normaal zou ik deze code als laatste bij onCreate zetten ... maar daar werkt het niet
		InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(editTextFotoName.getWindowToken(), 0);
		
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

}
