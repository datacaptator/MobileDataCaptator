package be.mobiledatacaptator.dao;

import be.mobiledatacaptator.model.UnitOfWork;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxFileSystem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class StartDropBoxApi extends Activity {

	public final static String APPKEY = "1bzrye5f167u7ov";
	public final static String APPSECRET = "d0hprnxcunyp18h";
	public final static int REQUEST_LINK_TO_DBX = 0;

	private DbxAccountManager dbxAccountManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		dbxAccountManager = DbxAccountManager.getInstance(getApplicationContext(), APPKEY, APPSECRET);

		if (dbxAccountManager.hasLinkedAccount()) {
			initDao();
		} else {
			dbxAccountManager.startLink(this, REQUEST_LINK_TO_DBX);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_LINK_TO_DBX) {
			initDao();
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void initDao() {
		try {
			((DropBoxDao) UnitOfWork.getInstance().getDao()).setDbxFileSystem(DbxFileSystem
					.forAccount(dbxAccountManager.getLinkedAccount()));
			finish();
		} catch (Exception e) {
			toonBoodschap(e.getMessage());
		}
	}

	private void toonBoodschap(String boodschap) {
		if (boodschap == null || boodschap == "") {
			boodschap = "Niet nader omschreven fout";
		}
		Toast.makeText(getApplicationContext(), boodschap, Toast.LENGTH_SHORT).show();
		finish();
	}
}
