package be.mobiledatacaptator.model;

import java.util.ArrayList;
import java.util.List;

public class Project {

	private String name;
	private String filePrefix;
	private String dataLocation;
	private String template;
	private boolean LoadFotoActivity;
	private boolean LoadSchetsActivity;
	private List<FotoCategorie> fotoCategories = new ArrayList<FotoCategorie>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFilePrefix() {
		return filePrefix;
	}

	public void setFilePrefix(String filePrefix) {
		this.filePrefix = filePrefix;
	}

	public String getDataLocation() {
		return dataLocation;
	}

	public void setDataLocation(String dataLocation) {
		if (!(dataLocation.endsWith("/")))
			dataLocation += "/";
		this.dataLocation = dataLocation;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public boolean isLoadFotoActivity() {
		return LoadFotoActivity;
	}

	public void setLoadFotoActivity(boolean loadFotoActivity) {
		LoadFotoActivity = loadFotoActivity;
	}

	public List<FotoCategorie> getFotoCategories() {
		return fotoCategories;
	}

	public void setFotoCategories(List<FotoCategorie> fotoCategories) {
		this.fotoCategories = fotoCategories;
	}

	public boolean isLoadSchetsActivity() {
		return LoadSchetsActivity;
	}

	public void setLoadSchetsActivity(boolean loadSchetsActivity) {
		LoadSchetsActivity = loadSchetsActivity;
	}

	@Override
	public String toString() {
		return getName();
	}

}
