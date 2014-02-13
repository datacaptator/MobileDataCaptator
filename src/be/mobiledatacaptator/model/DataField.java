package be.mobiledatacaptator.model;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.method.DigitsKeyListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

@SuppressLint("DefaultLocale")
public class DataField extends TableRow {

	private String name;
	private String label;
	private Boolean required;
	private VeldType type;
	private String defaultValue;
	private List<ChoiceItem> choiceItems = new ArrayList<ChoiceItem>();

	private TextView textViewLabel;
	private EditText editTextValue;
	private Spinner spinnerChoice;

	private Element xmlTemplate;

	public DataField(Context context, Element xml) {
		super(context);
		xmlTemplate = xml;
		loadTemplate();
	}
	
	public DataField(Context context){
		super(context);
	}

	private void loadTemplate() {
		// Velden invullen aan hand van xml element.
		if (xmlTemplate.hasAttribute("Name"))
			name = xmlTemplate.getAttribute("Name");
		if (xmlTemplate.hasAttribute("Label"))
			label = xmlTemplate.getAttribute("Label");
		if (xmlTemplate.hasAttribute("DefaultValue"))
			defaultValue = xmlTemplate.getAttribute("DefaultValue");
		if (xmlTemplate.hasAttribute("Required"))
			if (xmlTemplate.getAttribute("Required").toLowerCase().equals("y"))
				required = true;
		if (xmlTemplate.hasAttribute("Type")) {
			String strType = xmlTemplate.getAttribute("Type").toLowerCase();
			if (strType.equals("text"))
				type = VeldType.TEXT;
			if (strType.equals("choice"))
				type = VeldType.CHOICE;
			if (strType.equals("int"))
				type = VeldType.INT;
			if (strType.equals("double"))
				type = VeldType.DOUBLE;
		}
		NodeList temp = xmlTemplate.getElementsByTagName("Choices");
		if (temp.getLength() > 0) {
			NodeList keuzes = ((Element) temp.item(0)).getElementsByTagName("Choice");
			for (int l = 0; l < keuzes.getLength(); l++) {
				Element keuzeNode = (Element) keuzes.item(l);
				choiceItems.add(new ChoiceItem(Integer.parseInt(keuzeNode.getAttribute("Idn")), keuzeNode
						.getAttribute("Text")));
			}
		}

		// Label plaatsen
		textViewLabel = new TextView(getContext());
		textViewLabel.setText(label + ": ");
		textViewLabel.setTextAppearance(getContext(), android.R.style.TextAppearance_DeviceDefault_Medium);
		addView(textViewLabel);

		if (type == VeldType.CHOICE) {

			// Idien keuzelijst, spinner plaatsen
			spinnerChoice = new Spinner(getContext());
			ArrayAdapter<ChoiceItem> adapter = new ArrayAdapter<ChoiceItem>(getContext(),
					android.R.layout.simple_spinner_dropdown_item, choiceItems);
			spinnerChoice.setAdapter(adapter);

			if (defaultValue != null) {
				for (int i = 0; i < choiceItems.size(); i++) {
					if (choiceItems.get(i).getText().equals(defaultValue)) {
						spinnerChoice.setSelection(i);
						break;
					}
				}
			}
			addView(spinnerChoice);
		} else {

			// Anders textveld plaatsen
			editTextValue = new EditText(getContext());
			if (type == VeldType.DOUBLE)
				editTextValue.setKeyListener(new DigitsKeyListener(true, true));
			if (type == VeldType.INT)
				editTextValue.setKeyListener(new DigitsKeyListener(true, false));

			if (defaultValue != null)
				editTextValue.setText(defaultValue);
			addView(editTextValue);
		}
	}

}
