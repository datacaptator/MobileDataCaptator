package be.mobiledatacaptator.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import be.mobiledatacaptator.R;
import be.mobiledatacaptator.model.UnitOfWork;

public class FicheActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fiche);
		
		setTitle(UnitOfWork.getInstance().getActiveFiche().getNaam());
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return (true);
		}

		return (super.onOptionsItemSelected(item));
	}

	
}
