package be.mobiledatacaptator;

import be.mobiledatacaptator.model.UnitOfWork;
import android.app.Application;

public class MdcApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		// De 'unitOfWork' hier eerst initialiseren, dan blijft hij zeker
		// bestaan zolang de app draaid.
		UnitOfWork.getInstance();
	}

}
