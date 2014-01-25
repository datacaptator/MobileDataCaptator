package be.mobiledatacaptator.dao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import be.mobiledatacaptator.model.UnitOfWork;
import be.mobiledatacaptator.utilities.MdcUtil;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxFileSystem;

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
			((DropBoxDao) UnitOfWork.getInstance().getDao()).setDbxFileSystem(DbxFileSystem.forAccount(dbxAccountManager.getLinkedAccount()));
			finish();
		} catch (Exception e) {
			MdcUtil.showToastShort(e.getMessage(), getApplicationContext());
		}
	}
}
