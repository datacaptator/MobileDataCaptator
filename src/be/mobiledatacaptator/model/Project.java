package be.mobiledatacaptator.model;


public class Project {

	private String naam;
	private String filePrefix;
	private String datalocatie;
	private String template;

	public String getNaam() {
		return naam;
	}

	public void setNaam(String naam) {
		this.naam = naam;
	}

	public String getFilePrefix() {
		return filePrefix;
	}

	public void setFilePrefix(String filePrefix) {
		this.filePrefix = filePrefix;
	}

	public String getDatalocatie() {
		return datalocatie;
	}

	public void setDatalocatie(String datalocatie) {
		this.datalocatie = datalocatie;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	@Override
	public String toString() {
		return getNaam();
	}

}
