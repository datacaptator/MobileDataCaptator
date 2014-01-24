package be.mobiledatacaptator.model;

import java.util.ArrayList;
import java.util.List;

public class Fiche {

	private String name;
	private String path;
	private List<Group> groups;
	
	public Fiche(){
		groups = new ArrayList<Group>();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
