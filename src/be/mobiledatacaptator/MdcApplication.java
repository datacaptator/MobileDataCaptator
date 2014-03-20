package be.mobiledatacaptator;

import android.app.Application;
import be.mobiledatacaptator.exception_logging.ExceptionLogger;
import be.mobiledatacaptator.model.UnitOfWork;

public class MdcApplication extends Application {
	
	private ExceptionLogger exceptionLog;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		exceptionLog = new ExceptionLogger(getApplicationContext());
		
		try {
			// De 'unitOfWork' hier eerst initialiseren, dan blijft hij zeker
			// bestaan zolang de app draait.
			UnitOfWork.getInstance();
		} catch (Exception e) {
			exceptionLog.error(e);
		}
	}

}
