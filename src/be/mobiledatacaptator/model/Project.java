package be.mobiledatacaptator.model;

import java.util.HashMap;
import java.util.Map;


public class Project {

	private String name;
	private String filePrefix;
	private String dataLocation;
	private String template;
	
	// TODO - een project heeft fotocategorie of niet
	private boolean pictureFunctionalityEnabled;// 
	private Map <String,String> fotoCategories = new HashMap<String,String>();


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
		this.dataLocation = dataLocation;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public Map <String,String> getFotoCategories() {
		return fotoCategories;
	}

	public void setFotoCategories(Map <String,String> fotoCategories) {
		this.fotoCategories = fotoCategories;
		setPictureFunctionalityEnabled(true);
	}

	public boolean isPictureFunctionalityEnabled() {
		return pictureFunctionalityEnabled;
	}

	public void setPictureFunctionalityEnabled(boolean pictureFunctionalityEnabled) {
		this.pictureFunctionalityEnabled = pictureFunctionalityEnabled;
	}

	@Override
	public String toString() {
		return getName();
	}

}
