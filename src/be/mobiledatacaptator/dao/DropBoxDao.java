package be.mobiledatacaptator.dao;

import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;
import com.dropbox.sync.android.DbxPath.InvalidPathException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    public List<String> getAllFilesFromPathWithExtension(String path, String extension, Boolean returnExtension) throws InvalidPathException,
            DbxException {

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
    public FileOutputStream getWriteStreamForNewFile(String path) throws IOException {
        DbxFile dbxFile = dbxFileSystem.create(new DbxPath(path));
        return dbxFile.getWriteStream();

    }

	@Override
	public void delete(String path) throws Exception {
		dbxFileSystem.delete(new DbxPath(path));
	}


}
