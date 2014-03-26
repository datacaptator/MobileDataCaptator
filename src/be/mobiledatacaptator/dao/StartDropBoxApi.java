package be.mobiledatacaptator.dao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import be.mobiledatacaptator.exception_logging.MdcExceptionLogger;
import be.mobiledatacaptator.model.UnitOfWork;
import be.mobiledatacaptator.utilities.MdcUtil;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxFileSystem;

public class StartDropBoxApi extends Activity {
	
	
	public final static String APPKEY = "1iuuns3gstd6kbt"; //"1bzrye5f167u7ov";
	public final static String APPSECRET = "pbxmkw7vj47k4l7"; //"d0hprnxcunyp18h";
		
	public final static int REQUEST_LINK_TO_DBX = 0;
	
		
	private DbxAccountManager dbxAccountManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			dbxAccountManager = DbxAccountManager.getInstance(getApplicationContext(), APPKEY, APPSECRET);

			if (dbxAccountManager.hasLinkedAccount()) {
				initDao();
			} else {
				dbxAccountManager.startLink(this, REQUEST_LINK_TO_DBX);
			}
		} catch (Exception e) {
			MdcExceptionLogger.error(e, this);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (requestCode == REQUEST_LINK_TO_DBX) {
				initDao();
			} else {
				super.onActivityResult(requestCode, resultCode, data);
			}
		} catch (Exception e) {
			MdcExceptionLogger.error(e, this);
		}
	}

	private void initDao() {
		try {
			((DropBoxDao) UnitOfWork.getInstance().getDao()).setDbxFileSystem(DbxFileSystem
					.forAccount(dbxAccountManager.getLinkedAccount()));
			finish();
		} catch (Exception e) {
			MdcUtil.showToastShort(e.getMessage(), getApplicationContext());
			MdcExceptionLogger.error(e, this);
		}
	}
}
