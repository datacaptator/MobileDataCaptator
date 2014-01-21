package be.mobiledatacaptator.model;

import java.util.ArrayList;
import java.util.List;

public class DataField {

	private String naam;
	private String label;
	private Boolean required;
	private VeldType type;
	private String defaultValue;
	private List<ChoiceItem> choiceItems;
	private UIField uiField;

	public DataField() {
		choiceItems = new ArrayList<ChoiceItem>();
	}

	public String getNaam() {
		return naam;
	}

	public void setNaam(String naam) {
		this.naam = naam;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public VeldType getType() {
		return type;
	}

	public void setType(VeldType type) {
		this.type = type;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public List<ChoiceItem> getChoiceItems() {
		return choiceItems;
	}

	public void setChoiceItems(List<ChoiceItem> choiceItems) {
		this.choiceItems = choiceItems;
	}

	public UIField getUiField() {
		return uiField;
	}

	public void setUiField(UIField uiField) {
		this.uiField = uiField;
	}

	
}
