package be.mobiledatacaptator.exception_logging;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import be.mobiledatacaptator.model.UnitOfWork;

public class ExceptionLogger {
	private static ExceptionLogger instance = null;
	private UnitOfWork unitOfWork;
	private String tagClassName;
	private Context context;

	public enum Level {
		DEBUG, INFO, WARN, ERROR
	}

	public void LoggerSetup(String className, Level level) {
		this.tagClassName = className;
	}

	public ExceptionLogger(Context context) {
		LoggerSetup(context.getClass().getSimpleName(), Level.INFO);
		unitOfWork = UnitOfWork.getInstance();
		this.context = context;

	}

	public ExceptionLogger() {
		LoggerSetup("NO CONTEXT GIVEN", Level.INFO);
		unitOfWork = UnitOfWork.getInstance();
	}

	public static ExceptionLogger getInstance(Context context) {
		if (instance == null) {
			instance = new ExceptionLogger(context);
		}
		return instance;
	}

	public void debug(Exception e) {
		Log.d(tagClassName, e.getLocalizedMessage());
		writeToLogFile(Level.DEBUG, e);
	}

	public void info(Exception e) {
		Log.i(tagClassName, e.getLocalizedMessage());
		writeToLogFile(Level.INFO, e);
	}

	public void warn(Exception e) {
		Log.w(tagClassName, e.getLocalizedMessage());
		writeToLogFile(Level.WARN, e);
	}

	public void error(Exception e) {
		Log.e(tagClassName, e.getLocalizedMessage());
		writeToLogFile(Level.ERROR, e);
	}

	@SuppressLint("SimpleDateFormat")
	private void writeToLogFile(Level level, Exception e, Object... parameters) {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss");
		Date date = new Date();

		StringBuilder eMsg = new StringBuilder();
		eMsg.append(level.toString());
		eMsg.append("_");
		eMsg.append(dateFormat.format(date));
		eMsg.append("_");
		eMsg.append(unitOfWork.getActiveProject().getName());
		eMsg.append("_");
		eMsg.append(this.tagClassName);
		eMsg.append("_");
		eMsg.append(e.getStackTrace()[0].getMethodName());
		eMsg.append("_");
		eMsg.append(e.getLocalizedMessage());

		try {
			unitOfWork.getDao().appendStringToFile("DataCaptator/ExceptionLog/exception_log.txt", eMsg.toString() + ";\n");
			showExceptionDialog(e);
		} catch (Exception ex) {
			showExceptionDialog(ex);

		}
	}

	private void showExceptionDialog(Exception ex) {
		
		if (context != null) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
			alertDialogBuilder.setTitle("Application Error!");
			alertDialogBuilder.setMessage("Contact your IT-Administrator \n" + ex.getMessage()).setCancelable(true).setNegativeButton("OK", null);
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}
		
	}
}