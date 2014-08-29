package be.mobiledatacaptator.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;

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
	public List<String> getAllFilesFromPathWithExtension(String path,
			String extension, Boolean returnExtension)
			throws InvalidPathException, DbxException {

		List<String> folderContent = new ArrayList<String>();
		List<DbxFileInfo> fileInfoList = dbxFileSystem.listFolder(new DbxPath(
				path));

		for (DbxFileInfo dbxFileInfo : fileInfoList) {
			String name = dbxFileInfo.path.getName();
			if (name.endsWith(extension)) {
				if (!(returnExtension))
					name = name
							.substring(0, name.length() - extension.length());
				folderContent.add(name);
			}
		}
		return folderContent;
	}

	@Override
	public boolean existsFile(String path) throws InvalidPathException,
			DbxException {
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
	public void appendStringToFile(String path, String string) throws Exception {
		DbxPath dbxPath = new DbxPath(path);
		DbxFile f;

		if (dbxFileSystem.exists(dbxPath)) {
			f = dbxFileSystem.open(dbxPath);
		} else {
			f = dbxFileSystem.create(new DbxPath(path));
		}
		f.appendString(string);
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
		// DbxFileInfo fileInfo = dbxFile.getInfo();
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

	@Override
	public void saveAllFilesFromPath(Context ctxt, String path,
			String destination) throws Exception {

		List<DbxFileInfo> fileInfoList = dbxFileSystem.listFolder(new DbxPath(
				path));

		File outputPath = new File(Environment.getExternalStorageDirectory()
				.getPath() + "/" + destination + "/");
		outputPath.mkdirs();
		Log.i("DUMP", "START_DUMP");

		for (DbxFileInfo dbxFileInfo : fileInfoList) {
			InputStream inputStream = null;
			OutputStream outputStream = null;
			DbxFile dbxFile = null;

			try {
				dbxFile = dbxFileSystem.open(dbxFileInfo.path);
				// read this file into InputStream
				inputStream = dbxFile.getReadStream();

				// write the inputStream to a FileOutputStream
				outputStream = new FileOutputStream(new File(outputPath,
						dbxFileInfo.path.getName()));

				int read = 0;
				byte[] bytes = new byte[1024];

				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}

				Log.i("DUMP", dbxFileInfo.path.getName() + " Done!");

			} catch (IOException e) {
				Log.e("DUMP", dbxFileInfo.path.getName());
				e.printStackTrace();
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (outputStream != null) {
					try {
						outputStream.flush();
						outputStream.close();
						String str = outputPath.getAbsolutePath() + "/"
								+ dbxFileInfo.path.getName();
						MediaScannerConnection.scanFile(ctxt,
								new String[] { str }, null, null);
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
				if (dbxFile != null) {
					dbxFile.close();
				}
			}
		}
		Log.i("DUMP", "END_DUMP");
	}
}
