package be.mobiledatacaptator.activities;

import android.app.Activity;
import android.os.Bundle;
import be.mobiledatacaptator.model.UnitOfWork;
import be.testmobiledatacaptator.R;

public class FicheActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fiche);
		
		setTitle(UnitOfWork.getInstance().getActiveFiche().getNaam());
	}

	
}
