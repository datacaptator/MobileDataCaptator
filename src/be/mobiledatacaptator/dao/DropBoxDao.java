package be.mobiledatacaptator.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;
import com.dropbox.sync.android.DbxPath.InvalidPathException;

public class DropBoxDao implements IMdcDao {

	private DbxFileSystem dbxFileSystem;

	public void setDbxFileSystem(DbxFileSystem dbxFileSystem) {
		this.dbxFileSystem = dbxFileSystem;
	}

	@Override
	public String getFilecontent(String path) throws IOException {
		DbxFile dbxFile = dbxFileSystem.open(new DbxPath(path));
		String s = dbxFile.readString();
		dbxFile.close();
		return s;
	}

	@Override
	public List<String> getAllFilesFromPathWithExtension(String path, String extension, Boolean returnExtention)
			throws InvalidPathException, DbxException {

		List<String> folderContent = new ArrayList<String>();
		List<DbxFileInfo> fileInfoList = dbxFileSystem.listFolder(new DbxPath(path));

		for (DbxFileInfo dbxFileInfo : fileInfoList) {
			String naam = dbxFileInfo.path.getName();
			if (naam.endsWith(extension)) {
				if (!(returnExtention))
					naam = naam.substring(0, naam.length() - extension.length());
				folderContent.add(naam);
			}
		}
		return folderContent;
	}

	@Override
	public boolean existsFile(String path) throws InvalidPathException, DbxException {
		return dbxFileSystem.exists(new DbxPath(path));
	}

}
