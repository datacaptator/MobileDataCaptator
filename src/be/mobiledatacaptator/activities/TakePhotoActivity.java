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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
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

public class TakePhotoActivity extends Activity implements OnClickListener, OnItemLongClickListener,
		OnItemClickListener {

	final static int TAKE_PICTURE = 0;
	private Project project;
	private UnitOfWork unitOfWork;
	private ListView listViewPhotos;
	private Intent startCameraIntent;
	private String prefixFichePhotoName, photoNameToSave, tempFileName, textSelectedPhoto;

	private List<String> listFotoNames;
	private List<String> listThisFicheFotoNames;
	private TableLayout tableLayoutPhotoCategory;
	private Button buttonFreeSuffix, buttonDisplayPhoto, buttonDeletePhoto;
	private EditText editTextFreeSuffix;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_take_photo);

		unitOfWork = UnitOfWork.getInstance();
		project = unitOfWork.getActiveProject();

		// format prefixFichePhotoName = PUT3014
		prefixFichePhotoName = getIntent().getExtras().getString("prefixFichePhotoName");

		listViewPhotos = (ListView) findViewById(R.id.listViewPhotos);
		tableLayoutPhotoCategory = (TableLayout) findViewById(R.id.tableLayoutPhotoCategory);
		editTextFreeSuffix = (EditText) findViewById(R.id.editTextFreeSuffix);
		buttonFreeSuffix = (Button) findViewById(R.id.buttonFreeSuffix);
		buttonDisplayPhoto = (Button) findViewById(R.id.buttonDisplayPhoto);
		buttonDeletePhoto = (Button) findViewById(R.id.buttonDeletePhoto);

		listViewPhotos.setOnItemClickListener(this);
		listViewPhotos.setOnItemLongClickListener(this);

		buttonFreeSuffix.setOnClickListener(photoCategoryListener);
		buttonDisplayPhoto.setOnClickListener(this);
		buttonDeletePhoto.setOnClickListener(this);

		setTitle(MdcUtil.setActivityTitle(unitOfWork, getApplicationContext()));

		int index = 0;
		for (PhotoCategory photoCat : project.getPhotoCategories()) {
			addPhotoCategoriesToLayout(photoCat, index);
			index++;
		}

		loadPhotoNames();

		// hide the keyboard
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

	}

	private void loadPhotoNames() {
		try {
			listFotoNames = unitOfWork.getDao().getAllFilesFromPathWithExtension(project.getDataLocation(), ".jpg",
					false);

			listThisFicheFotoNames = new ArrayList<String>();

			for (String myFotoName : listFotoNames) {
				if (myFotoName.startsWith(prefixFichePhotoName)) {
					listThisFicheFotoNames.add(myFotoName);
				}
			}

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_activated_1, listThisFicheFotoNames);
			listViewPhotos.setAdapter(adapter);
			listViewPhotos.setItemsCanFocus(true);
			listViewPhotos.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		} catch (Exception e) {
			MdcUtil.showToastShort(R.string.LoadFiches_error + e.getMessage(), getApplicationContext());
		}
	}

	private void addPhotoCategoriesToLayout(PhotoCategory photoCategorie, int index) {
		// Android provides a service
		// getSystemService(Context.LAYOUT_INFLATER_SERVICE)' to inflate a
		// layout
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View newPhotoCategoryView = inflater.inflate(R.layout.new_table_row_photo_category, null);

		// Get a reference to the buttonNewPhotoCategory on the
		// new_table_row_photo_category.xml and set
		// its text + register its onClickListener
		Button buttonNewPhotoCategory = (Button) newPhotoCategoryView.findViewById(R.id.buttonNewPhotoCategory);
		buttonNewPhotoCategory.setText(photoCategorie.getName());
		buttonNewPhotoCategory.setTag(photoCategorie.getSuffix());
		buttonNewPhotoCategory.setOnClickListener(photoCategoryListener);

		// Adds programmatically the new_tag_view.xml to the
		// tableLayoutPhotoCategory at the specified index)
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

	private void startCamera(String photoNameToSave) {
		File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File image;
		try {
			image = File.createTempFile(photoNameToSave, /* prefix */
					".jpg", /* suffix */
					storageDir /* directory */
			);

			tempFileName = image.getAbsolutePath();

			startCameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));

			startActivityForResult(startCameraIntent, TAKE_PICTURE);

		} catch (IOException IOexception) {
			// TODO Auto-generated catch block
			Log.e("startActivityForResult", IOexception.getLocalizedMessage());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("startActivityForResult", e.getLocalizedMessage());
		}

	}

	// Annonymous Inner Class that implements Interface OnClickListener to
	// respond to the click event
	public OnClickListener photoCategoryListener = new OnClickListener() {

		@Override
		public void onClick(View buttonClicked) {
			try {
				if (buttonClicked.getId() == R.id.buttonFreeSuffix) {

					String suffix = editTextFreeSuffix.getText().toString();

					if (suffix.length() > 0) {
						photoNameToSave = prefixFichePhotoName + "_" + suffix + "_";
						photoNameToSave = composePhotoName(photoNameToSave);

						startCamera(photoNameToSave);

					} else {
						MdcUtil.showToastShort(getString(R.string.enter_suffix), getApplicationContext());
					}

				} else {
					TableRow buttonTableRow = (TableRow) buttonClicked.getParent();
					Button buttonNewPhotoCategory = (Button) buttonTableRow.findViewById(R.id.buttonNewPhotoCategory);

					photoNameToSave = prefixFichePhotoName + "_" + buttonNewPhotoCategory.getTag().toString() + "_";
					photoNameToSave = composePhotoName(photoNameToSave);

					startCamera(photoNameToSave);

				}
			} catch (Exception e) {
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

			File tempFile = new File(tempFileName);
			Bitmap bitmap = BitmapFactory.decodeFile(tempFileName);

			// Groote aanpassen
			int origWidth = bitmap.getWidth();
			int origHeight = bitmap.getHeight();

			int destWidth = project.getPhotoWidth();
			int destHeight = project.getPhotoHeight();

			// Hoogte en breedte aanpassen naar gelang portrait of landscape
			if (origHeight > origWidth) {
				if (destWidth > destHeight) {
					int temp = destWidth;
					destWidth = destHeight;
					destHeight = temp;
				}
			} else {
				if (destWidth < destHeight) {
					int temp = destWidth;
					destWidth = destHeight;
					destHeight = temp;
				}
			}

			// Hoogte aanpassen
			if (origHeight > destHeight) {
				bitmap = Bitmap.createScaledBitmap(bitmap, origWidth / (origHeight / destHeight), destHeight, false);
			}
			origWidth = (origWidth / (origHeight / destHeight));
			origHeight = destHeight;

			// Breedte aanpassen
			if (origWidth > destWidth) {
				bitmap = Bitmap.createScaledBitmap(bitmap, destWidth, origHeight / (origWidth / destWidth), false);
			}

			// Wegschrijven
			try {
				bitmap.compress(Bitmap.CompressFormat.JPEG, 80, new FileOutputStream(tempFile));
				unitOfWork.getDao().saveFile(project.getDataLocation() + photoNameToSave + ".jpg", tempFile);
			} catch (Exception e) {
				MdcUtil.showToastShort(getString(R.string.errorSavePhoto), this);
			}

			// Tijdelijke file verwijderen
			tempFile.delete();

			loadPhotoNames();

		} catch (Exception e) {
			// TODO
			Log.e("FOUT: savePhoto", e.getLocalizedMessage());
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonDeletePhoto:
			if (textSelectedPhoto != null && !textSelectedPhoto.isEmpty()) {
				deleteSelectedPhoto(textSelectedPhoto);
			} else {
				MdcUtil.showToastShort(getString(R.string.select_photo_first), getApplicationContext());
			}
			break;

		case R.id.buttonDisplayPhoto:
			try {
				if (textSelectedPhoto != null && !textSelectedPhoto.isEmpty()) {
					final Intent displayPhotoIntent = new Intent(this, DisplayPhotoActivity.class);
					displayPhotoIntent.putExtra("photoToDisplay", textSelectedPhoto);
					startActivity(displayPhotoIntent);
				} else {
					MdcUtil.showToastShort(getString(R.string.select_photo_first), getApplicationContext());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e("fout", e.getLocalizedMessage());
			}
			break;

		default:
			break;
		}
	}

	private void deleteSelectedPhoto(String selectedPhotoName) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle(getString(R.string.delete_photo_));
		alertDialogBuilder.setMessage(String.format(getString(R.string.click_yes_to_delete_photo), textSelectedPhoto))
				.setCancelable(false).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						try {
							unitOfWork.getDao().delete(project.getDataLocation() + textSelectedPhoto + ".jpg");
							textSelectedPhoto = null;
							loadPhotoNames();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							Log.e("ERROR DELETING PHOTO - ", e.getLocalizedMessage());
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

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int indexListItem, long arg3) {
		textSelectedPhoto = (String) listViewPhotos.getItemAtPosition(indexListItem);
		deleteSelectedPhoto(textSelectedPhoto);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int indexListItem, long arg3) {
		textSelectedPhoto = (String) listViewPhotos.getItemAtPosition(indexListItem);
	}

}
