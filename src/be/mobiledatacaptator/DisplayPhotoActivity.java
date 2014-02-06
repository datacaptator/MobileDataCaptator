package be.mobiledatacaptator;

import java.io.FileInputStream;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
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
		FileInputStream fileInputStream = null;
		
		imageViewDisplayPhoto = (ImageView) findViewById(R.id.imageViewDisplayPhoto);
		
		String photoToDisplay = null;
		
		unitOfWork = UnitOfWork.getInstance();
		project = unitOfWork.getActiveProject();
		
		photoToDisplay =getIntent().getExtras().get("photoToDisplay").toString();
		try {
			fileInputStream = unitOfWork.getDao().getReadStreamFromFile(project.getDataLocation() + photoToDisplay + ".jpg");
			bitMap = BitmapFactory.decodeStream(fileInputStream);
			imageViewDisplayPhoto.setImageBitmap(bitMap);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("FileiInputStream CATCH: ", e.getLocalizedMessage());
		}
		finally{
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.e("FileiInputStream FINALLY: ", e.getLocalizedMessage());
				}	
			}
			
		}

	}

}
