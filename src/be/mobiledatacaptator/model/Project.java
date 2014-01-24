package be.mobiledatacaptator.model;


public class Project {

	private String name;
	private String filePrefix;
	private String dataLocation;
	private String template;

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

	@Override
	public String toString() {
		return getName();
	}

}
