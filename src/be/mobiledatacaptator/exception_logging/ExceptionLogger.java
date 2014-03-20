package be.mobiledatacaptator.exception_logging;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;

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

	private Level level = Level.ERROR;
	private String tag;
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy hh:mm:ss z");

	// private File logFile;

	public void LoggerSetup(String tag, String logFilename, Level level) {

		this.tag = tag;

		this.level = level;
	}

	public ExceptionLogger(Context context) {
		LoggerSetup(context.getPackageName(), "application.writeToLogFile", Level.INFO);
		unitOfWork = UnitOfWork.getInstance();
	}

	public static ExceptionLogger getInstance(Context context) {
		if (instance == null) {
			instance = new ExceptionLogger(context);
		}
		return instance;
	}

	public ExceptionLogger() {
		LoggerSetup("", "application.writeToLogFile", Level.INFO);
		unitOfWork = UnitOfWork.getInstance();
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public boolean isLoggable(Level level) {
		return level.ordinal() >= this.level.ordinal();
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

	public void error(Throwable throwable) {
		String message = Log.getStackTraceString(throwable);
		Log.e(tag, message, throwable);
		writeToLogFile(Level.ERROR, message);
	}

	private void writeToLogFile(Level level, String message, Object... parameters) {
		
		try {
			unitOfWork.getDao().appendStringToFile("DataCaptator/ExceptionLog/exception_log.txt", message + ";\n");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("test","test" );
			
		}

	}
}