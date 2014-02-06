package be.mobiledatacaptator.model;

public class TabTemplate extends Tab {

	public TabTemplate(String name) {
		super(name);
	}

	public Tab getNewTab() {
		Tab tab = new Tab(name);
		
		for (DataField dataField : getDataFields()) {
			DataField field =new DataField();
			field.setLabel(dataField.getLabel());
			field.setName(dataField.getName());
			field.setDefaultValue(dataField.getDefaultValue());
			field.setRequired(dataField.getRequired());
			field.setType(dataField.getType());
			
			for (ChoiceItem item : dataField.getChoiceItems()) {
				ChoiceItem choiceItem = new ChoiceItem(item.getId(),item.getText());
				field.getChoiceItems().add(choiceItem);
			}
			
			field.setUiField(new UIField(dataField.getUiField().getContext(), field));
			tab.getDataFields().add(field);
		}

		return tab;
	}

}
