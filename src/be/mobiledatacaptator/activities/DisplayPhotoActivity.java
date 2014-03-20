package be.mobiledatacaptator.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import be.mobiledatacaptator.R;
import be.mobiledatacaptator.exception_logging.ExceptionLogger;
import be.mobiledatacaptator.model.Project;
import be.mobiledatacaptator.model.UnitOfWork;

public class DisplayPhotoActivity extends Activity {

	ExceptionLogger exceptionLog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_photo);
		
		exceptionLog = new ExceptionLogger(this);

		try {

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

			bitMap = unitOfWork.getDao().getBitmapFromFile(project.getDataLocation() + photoToDisplay + ".jpg");
			imageViewDisplayPhoto.setImageBitmap(bitMap);

		} catch (Exception e) {
			exceptionLog.error(e);
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
