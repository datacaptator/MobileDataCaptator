package be.mobiledatacaptator;

import android.app.Application;
import be.mobiledatacaptator.model.UnitOfWork;

public class MdcApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		// De 'unitOfWork' hier eerst initialiseren, dan blijft hij zeker
		// bestaan zolang de app draait.
		UnitOfWork.getInstance();
	}

}
