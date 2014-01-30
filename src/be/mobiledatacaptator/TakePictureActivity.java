package be.mobiledatacaptator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import be.mobiledatacaptator.model.Project;
import be.mobiledatacaptator.model.UnitOfWork;
import be.mobiledatacaptator.utilities.MdcUtil;

public class TakePictureActivity extends Activity implements OnClickListener {

	private Project project;
	private UnitOfWork unitOfWork;

	private Button buttonTakePicture;
	// foto
	private Bitmap bitMap;
	final static int cameraData = 0;
	private Intent myFotoIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_take_picture);

		buttonTakePicture = (Button) findViewById(R.id.buttonTakePicture);
		buttonTakePicture.setOnClickListener(this);

		unitOfWork = UnitOfWork.getInstance();
		project = unitOfWork.getActiveProject();

		setTitle(project.getName());
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

			String file = dir + "myFile4.jpg";
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
				
//				unitOfWork.getDao().uploadPicture(newfile);
//				MdcUtil.showToastLong("DataLocation = " + project.getDataLocation(), getApplicationContext());
//				
				
				MdcUtil.showToastShort("net voor dao!", getApplicationContext());	
				String path = project.getDataLocation();
				MdcUtil.showToastShort(project.getDataLocation(), getApplicationContext());
								
				unitOfWork.getDao().uploadPicture(newfile, path);
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

//			try {
//
//				unitOfWork.getDao().uploadPicture(newfile);
//				MdcUtil.showToastLong("DataLocation = " + project.getDataLocation(), getApplicationContext());
//
//				// unitOfWork.getDao().uploadPicture("DataCaptator/AppData/Pidpa/Bonheiden", newfile);
//			} catch (InvalidPathException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

		}
	}
	
	
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.buttonTakePicture:
			myFotoIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(myFotoIntent, cameraData);
			break;

		default:
			break;
		}
		
		
		
	}


}
