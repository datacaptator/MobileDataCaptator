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
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
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

	final static int TAKE_PICTURE = 0;
	private Project project;
	private UnitOfWork unitOfWork;
	private ListView listViewPhotos;
	private Intent startCameraIntent;
	private String prefixFicheFotoName, photoNameToSave, tempFileName;

	private List<String> listFotoNames;
	private List<String> listThisFicheFotoNames;
	private TableLayout tableLayoutPhotoCategory;
	private Button buttonVrijeSuffix, buttonOpenPhoto, buttonDeletePhoto;
	private EditText editTextVrijeSuffix;
	private String textSelectedPhoto;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_take_photo);

		unitOfWork = UnitOfWork.getInstance();
		project = unitOfWork.getActiveProject();

		// fotoName = PUT3014
		prefixFicheFotoName = getIntent().getExtras().getString("fotoName");

		listViewPhotos = (ListView) findViewById(R.id.listViewPhotos);
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

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, listThisFicheFotoNames);
			listViewPhotos.setAdapter(adapter);
			listViewPhotos.setItemsCanFocus(true);
			listViewPhotos.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			listViewPhotos.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int indexListItem, long arg3) {
					textSelectedPhoto = (String) listViewPhotos.getItemAtPosition(indexListItem);

				}
			});

		} catch (Exception e) {
			MdcUtil.showToastShort(R.string.LoadFiches_error + e.getMessage(), getApplicationContext());
		}
	}

	private void addPhotoCategoriesToLayout(PhotoCategory photoCategorie, int index) {

		// First new_tag_view.xml gets inflated
		// Android provides a service
		// 'getSystemService(Context.LAYOUT_INFLATER_SERVICE)' that enables you
		// to inflate a layout
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View newPhotoCategoryView = inflater.inflate(R.layout.new_table_row_photo_category, null);

		// Get a reference to the newTagButton on the new_tag_view.xml and set
		// its text + register its onClickListener
		Button buttonNewPhotoCategory = (Button) newPhotoCategoryView.findViewById(R.id.buttonNewPhotoCategory);
		buttonNewPhotoCategory.setText(photoCategorie.getName());
		buttonNewPhotoCategory.setTag(photoCategorie.getSuffix());
		buttonNewPhotoCategory.setOnClickListener(photoCategoryListener);

		// Adds programmatically the new_tag_view.xml to the queryTableLayout at
		// the specified index)
		tableLayoutPhotoCategory.addView(newPhotoCategoryView, index);

	}

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

	// Annonymous Inner Class that implements Interface OnClickListener to
	// respond to the click event
	public OnClickListener photoCategoryListener = new OnClickListener() {

		// Implements the OnclickListener onClick method
		@Override
		public void onClick(View buttonClicked) {
			try {
				if (buttonClicked.getId() == R.id.buttonVrijeSuffix) {

					String suffix = editTextVrijeSuffix.getText().toString();

					if (suffix.length() > 0) {
						photoNameToSave = prefixFicheFotoName + "_" + suffix + "_";
						photoNameToSave = composePhotoName(photoNameToSave);

						File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

						File image = File.createTempFile(photoNameToSave, /* prefix */
								".jpg", /* suffix */
								storageDir /* directory */
						);

						tempFileName = image.getAbsolutePath();

						startCameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
						startCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));

						try {
							startActivityForResult(startCameraIntent, TAKE_PICTURE);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							Log.e("startActivityForResult", e.getLocalizedMessage());
						}

					} else {
						MdcUtil.showToastShort("Vul een suffix in!", getApplicationContext());
					}

				} else {
					TableRow buttonTableRow = (TableRow) buttonClicked.getParent();
					Button buttonNewPhotoCategory = (Button) buttonTableRow.findViewById(R.id.buttonNewPhotoCategory);

					photoNameToSave = prefixFicheFotoName + "_" + buttonNewPhotoCategory.getTag().toString() + "_";
					photoNameToSave = composePhotoName(photoNameToSave);

					File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

					File image = File.createTempFile(photoNameToSave, /* prefix */
							".jpg", /* suffix */
							storageDir /* directory */
					);

					tempFileName = image.getAbsolutePath();

					startCameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					startCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));

					startActivityForResult(startCameraIntent, TAKE_PICTURE);

				}
			} catch (IOException e) {
				MdcUtil.showToastShort(e.getMessage(), getApplicationContext());
			}

		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == TAKE_PICTURE) {
			if (resultCode == RESULT_OK) {
				Log.e("TOT HIER OK", "resultcode == RESULT_OK");
				savePhoto();
			} else {
				Log.e("TOT HIER OK", "else resultcode != RESULT_OK");
			}
		} else {
			Log.e("TOT HIER OK", "else requestCode != TAKE_PICTURE");
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void savePhoto() {
		try {
			Bitmap bitmap = BitmapFactory.decodeFile(tempFileName);

			// Groote aanpassen
			int origWidth = bitmap.getWidth();
			int origHeight = bitmap.getHeight();

			int destWidth = project.getPhotoWidth();
			int destHeight = project.getPhotoHeight();

			if (origHeight > destHeight || origWidth > destWidth) {
				bitmap = Bitmap.createScaledBitmap(bitmap, destWidth, origHeight / (origWidth / destWidth), false);
			}
			// Wegschrijven
			FileOutputStream fos = unitOfWork.getDao().getWriteStreamForNewFile(project.getDataLocation() + photoNameToSave + ".jpg");
			bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
			fos.flush();
			fos.close();

			// Tijdelijke file verwijderen
			File f = new File(tempFileName);
			f.delete();

			loadFotoNames();

		} catch (Exception e) {
			// TODO
			Log.e("FOUT: savePhoto", e.getLocalizedMessage());
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int indexListItem, long arg3) {

		// // Hides the soft keyboard - normaal zou ik deze code als laatste bij
		// onCreate zetten ... maar daar werkt het niet
		InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(editTextVrijeSuffix.getWindowToken(), 0);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonDeletePhoto:
			if (!textSelectedPhoto.equals("") || textSelectedPhoto.isEmpty()) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
				alertDialogBuilder.setTitle(getString(R.string.delete_photo_));
				alertDialogBuilder.setMessage(String.format(getString(R.string.click_yes_to_delete_photo), textSelectedPhoto)).setCancelable(false)
						.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								try {
									unitOfWork.getDao().delete(project.getDataLocation() + textSelectedPhoto + ".jpg");
									loadFotoNames();
								} catch (Exception e) {
									// TODO Auto-generated catch block

									e.printStackTrace();
								}
							}
						}).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();

			}

			break;

		default:
			break;
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}
