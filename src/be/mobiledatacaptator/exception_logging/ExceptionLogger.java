package be.mobiledatacaptator.exception_logging;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import be.mobiledatacaptator.model.UnitOfWork;

public class ExceptionLogger {
	private static ExceptionLogger instance = null;
	private UnitOfWork unitOfWork;

	public enum Level {
		DEBUG, INFO, WARN, ERROR
	}

	private String tag;
	
	public void LoggerSetup(String className, Level level) {
		this.tag = className;
	}

	public ExceptionLogger(Context context) {
		LoggerSetup(context.getClass().getSimpleName(), Level.INFO);
		unitOfWork = UnitOfWork.getInstance();
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
	
	public void debug(String message, Object... parameters) {
		if (parameters.length > 0) {
			Log.d(tag, MessageFormat.format(message, parameters));
		} else {
			Log.d(tag, message);
		}
		writeToLogFile(Level.DEBUG, message, parameters);
	}

	public void info(String message, Object... parameters) {
		if (parameters.length > 0) {
			Log.i(tag, MessageFormat.format(message, parameters));
		} else {
			Log.i(tag, message);
		}
		writeToLogFile(Level.INFO, message, parameters);
	}

	public void warn(String message, Object... parameters) {
		if (parameters.length > 0) {
			Log.w(tag, MessageFormat.format(message, parameters));
		} else {
			Log.w(tag, message);
		}
		writeToLogFile(Level.WARN, message, parameters);
	}

	public void error(String message, Object... parameters) {
		if (parameters.length > 0) {
			Log.e(tag, MessageFormat.format(message, parameters));
		} else {
			Log.e(tag, message);
		}
		writeToLogFile(Level.ERROR, message, parameters);
	}

	@SuppressLint("SimpleDateFormat")
	private void writeToLogFile(Level level, String message, Object... parameters) {
		
		   SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		   Date date = new Date();
		   
		   StringBuilder exceptionMsg = new StringBuilder();
		   exceptionMsg.append(dateFormat.format(date));
		   exceptionMsg.append("-");
		   exceptionMsg.append(unitOfWork.getActiveProject().getName());
		   exceptionMsg.append("-");
		   exceptionMsg.append(this.tag);
		   exceptionMsg.append("-");
		   exceptionMsg.append(message);
		   
		
		try {
			unitOfWork.getDao().appendStringToFile("DataCaptator/ExceptionLog/exception_log.txt", exceptionMsg.toString() + ";\n");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("test","test" );
			
		}

	}
}