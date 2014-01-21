package be.mobiledatacaptator.model;

import java.util.ArrayList;
import java.util.List;

public class Tab {

	private String name;
	private List<DataField> dataFields;
	
	public Tab(String name){
		this.name=name;
		dataFields= new ArrayList<DataField>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<DataField> getDataFields() {
		return dataFields;
	}

	public void setDataFields(List<DataField> dataFields) {
		this.dataFields = dataFields;
	}
	
	
}
