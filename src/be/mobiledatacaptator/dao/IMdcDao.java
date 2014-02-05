package be.mobiledatacaptator.dao;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public interface IMdcDao {

	public String getFilecontent(String path) throws IOException;
	public List<String> getAllFilesFromPathWithExtension(String path, String extension, Boolean returnExtension) throws Exception;
	public boolean existsFile(String path) throws Exception;
    public FileOutputStream getWriteStreamForNewFile(String path) throws Exception;

}
