package be.mobiledatacaptator.model;

import java.util.ArrayList;
import java.util.List;

public class Fiche {

	private String naam;
	private String path;
	private List<Group> groups;
	
	public Fiche(){
		groups = new ArrayList<Group>();
	}
	
	public String getNaam() {
		return naam;
	}

	public void setNaam(String naam) {
		this.naam = naam;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<Group> getGroups() {
		return groups;
	}

}
