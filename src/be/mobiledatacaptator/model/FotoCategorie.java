package be.mobiledatacaptator.model;

public class FotoCategorie {
	
	private String name;
	private String suffix;
	
	
	
	public FotoCategorie(String name, String suffix) {
		this.name = name;
		this.suffix = suffix;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

}
