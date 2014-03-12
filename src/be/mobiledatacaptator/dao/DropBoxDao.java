package be.mobiledatacaptator.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
	public List<String> getAllFilesFromPathWithExtension(String path, String extension, Boolean returnExtension)
			throws InvalidPathException, DbxException {

		List<String> folderContent = new ArrayList<String>();
		List<DbxFileInfo> fileInfoList = dbxFileSystem.listFolder(new DbxPath(path));

		for (DbxFileInfo dbxFileInfo : fileInfoList) {
			String name = dbxFileInfo.path.getName();
			if (name.endsWith(extension)) {
				if (!(returnExtension))
					name = name.substring(0, name.length() - extension.length());
				folderContent.add(name);
			}
		}
		return folderContent;
	}

	@Override
	public boolean existsFile(String path) throws InvalidPathException, DbxException {
		return dbxFileSystem.exists(new DbxPath(path));
	}

	@Override
	public void delete(String path) throws Exception {
		dbxFileSystem.delete(new DbxPath(path));
	}

	@Override
	public void saveFile(String path, File file) throws Exception {
		DbxPath dbxPath = new DbxPath(path);
		final DbxFile f;

		if (dbxFileSystem.exists(dbxPath)) {
			f = dbxFileSystem.open(dbxPath);
		} else {
			f = dbxFileSystem.create(new DbxPath(path));
		}
		f.writeFromExistingFile(file, false);
		f.close();
	}

	@Override
	public void saveStringToFile(String path, String string) throws Exception {
		DbxPath dbxPath = new DbxPath(path);
		DbxFile f;

		if (dbxFileSystem.exists(dbxPath)) {
			f = dbxFileSystem.open(dbxPath);
		} else {
			f = dbxFileSystem.create(new DbxPath(path));
		}
		f.writeString(string);
		f.close();

	}

	@Override
	public Bitmap getBitmapFromFile(String path) throws Exception {
		DbxFile dbxFile = dbxFileSystem.open(new DbxPath(path));

		// Dit zou het probmeem met het (soms) niet weergeven van bitmaps moeten
		// oplossen.
		// MAAR WERKT NIET!
		// dbx weet dat er een nieuwe versie van een file is, maar geeft deze
		// niet weer.
		// Bug? App opnieuw installeren helpt wel...

		// DbxFileStatus status = dbxFile.getSyncStatus();
		// DbxFileStatus newerStatus = dbxFile.getNewerStatus();
		//
		// if (newerStatus != null && newerStatus.isCached) {
		// dbxFile.update();
		// }

		FileInputStream fileInputStream = dbxFile.getReadStream();
		Bitmap bitMap = BitmapFactory.decodeStream(fileInputStream);
		fileInputStream.close();
		dbxFile.close();
		return bitMap;
	}

}
