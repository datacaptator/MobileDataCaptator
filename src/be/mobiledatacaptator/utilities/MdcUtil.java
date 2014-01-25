package be.mobiledatacaptator.utilities;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
import be.mobiledatacaptator.R;


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
	

	
}
