package be.mobiledatacaptator.utilities;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
import be.mobiledatacaptator.R;
import be.mobiledatacaptator.model.Project;
import be.mobiledatacaptator.model.UnitOfWork;


public class MdcUtil extends Activity{

	public static void showToastShort(String message, Context context)
	{
		if (message == null || message == "") {
			message = context.getString(R.string.unknown_error);
		}
		
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}
	
	public static void showToastLong(String message, Context context)
	{
		if (message == null || message == "") {
			message = context.getString(R.string.unknown_error);
		}
		
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}
	
	public static String setActivityTitle(UnitOfWork unitOfWork, Context context)
	{
		Project project = unitOfWork.getActiveProject();
		String projectName = unitOfWork.getActiveProject().getName();
        String ficheName = unitOfWork.getActiveFiche().getName().substring(project.getFilePrefix().length());	
        
        return projectName + " - " + context.getString(R.string.fiche) + " " + ficheName;
        
		
		
	}

	
}
