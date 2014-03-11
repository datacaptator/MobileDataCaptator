package be.mobiledatacaptator.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import be.mobiledatacaptator.R;
import be.mobiledatacaptator.model.Project;
import be.mobiledatacaptator.model.UnitOfWork;

public class DisplayPhotoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_photo);

		Bitmap bitMap;
		ImageView imageViewDisplayPhoto;
		Project project;
		UnitOfWork unitOfWork;

		imageViewDisplayPhoto = (ImageView) findViewById(R.id.imageViewDisplayPhoto);

		String photoToDisplay = null;

		unitOfWork = UnitOfWork.getInstance();
		project = unitOfWork.getActiveProject();

		photoToDisplay = getIntent().getExtras().get("photoToDisplay").toString();

		setTitle(getString(R.string.photo) + " " + photoToDisplay);

		try {
			bitMap = unitOfWork.getDao().getBitmapFromFile(project.getDataLocation() + photoToDisplay + ".jpg");
			imageViewDisplayPhoto.setImageBitmap(bitMap);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("FileiInputStream CATCH: ", e.getLocalizedMessage());
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return (true);
		}
		return (super.onOptionsItemSelected(item));
	}

}
