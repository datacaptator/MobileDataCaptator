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
	private String tag;
	private Context context;

	public enum Level {
		DEBUG, INFO, WARN, ERROR
	}

	public void LoggerSetup(String className, Level level) {
		this.tag = className;
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

	public void debug(Exception exception) {
		Log.d(tag, exception.getLocalizedMessage());
		writeToLogFile(Level.DEBUG, exception);
	}

	public void info(Exception exception) {
		Log.i(tag, exception.getLocalizedMessage());
		writeToLogFile(Level.INFO, exception);
	}

	public void warn(Exception exception) {
		Log.w(tag, exception.getLocalizedMessage());
		writeToLogFile(Level.WARN, exception);
	}

	public void error(Exception exception) {
		Log.e(tag, exception.getLocalizedMessage());
		writeToLogFile(Level.ERROR, exception);
	}

	@SuppressLint("SimpleDateFormat")
	private void writeToLogFile(Level level, Exception exception, Object... parameters) {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss");
		Date date = new Date();

		StringBuilder exceptionMsg = new StringBuilder();
		exceptionMsg.append(level.toString());
		exceptionMsg.append("_");
		exceptionMsg.append(dateFormat.format(date));
		exceptionMsg.append("_");
		exceptionMsg.append(unitOfWork.getActiveProject().getName());
		exceptionMsg.append("_");
		exceptionMsg.append(this.tag);
		exceptionMsg.append("_");
		exceptionMsg.append(exception.getStackTrace()[0].getMethodName());
		exceptionMsg.append("_");
		exceptionMsg.append(exception.getLocalizedMessage());

		try {
			unitOfWork.getDao().appendStringToFile("DataCaptator/ExceptionLog/exception_log.txt", exceptionMsg.toString() + ";\n");
			showExceptionDialog(exception);
		} catch (Exception e) {
			showExceptionDialog(exception);

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