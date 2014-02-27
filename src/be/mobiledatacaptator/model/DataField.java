package be.mobiledatacaptator.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import be.mobiledatacaptator.R;
import be.mobiledatacaptator.utilities.MdcUtil;

public class DataField extends TableRow implements TextWatcher, OnItemSelectedListener {

	private String name;
	private String label;
	private Tab tab;
	private Boolean required = false;
	private final double nullValue = -9999;
	private double min = nullValue;
	private double max = nullValue;
	private VeldType type;
	private String defaultValue;
	private String link;
	private List<ChoiceItem> choiceItems = new ArrayList<ChoiceItem>();

	private Boolean activateLink = false; // Deze wordt pas true na het inlezen
											// van de
	// template en bestaande data. Dit om te
	// voorkomen dat bestaande data aangepast
	// wordt.

	private TextView textViewLabel;
	private EditText editTextValue;
	private Spinner spinnerChoice;

	private Element xmlTemplate;

	public DataField(Context context, Element xml, Tab tab) {
		super(context);
		xmlTemplate = xml;
		this.tab = tab;
		loadTemplate();
		activateLink = true;
	}

	public DataField(Context context) {
		super(context);
	}

	public void appendXml(Document doc, Element root) {
		Element element = doc.createElement(name);
		root.appendChild(element);

		if (type == VeldType.CHOICE) {
			if (spinnerChoice.getSelectedItem() != null) {
				element.appendChild(doc.createTextNode(spinnerChoice.getSelectedItem().toString()));
				element.setAttribute(getContext().getString(R.string.IdnForXmlAttr),
						String.valueOf(((ChoiceItem) spinnerChoice.getSelectedItem()).getId()));
			}
		} else {
			if (editTextValue.getText() != null) {
				element.appendChild(doc.createTextNode(editTextValue.getText().toString()));
			}
		}
	}

	@SuppressLint("DefaultLocale")
	private void loadTemplate() {
		// Velden invullen aan hand van xml element.
		if (xmlTemplate.hasAttribute("Name"))
			name = xmlTemplate.getAttribute("Name");
		if (xmlTemplate.hasAttribute("Label"))
			label = xmlTemplate.getAttribute("Label");
		if (xmlTemplate.hasAttribute("DefaultValue"))
			defaultValue = xmlTemplate.getAttribute("DefaultValue");
		if (xmlTemplate.hasAttribute("Link"))
			link = xmlTemplate.getAttribute("Link");
		if (xmlTemplate.hasAttribute("Required"))
			if (xmlTemplate.getAttribute("Required").toLowerCase(Locale.getDefault()).equals("y"))
				required = true;
		if (xmlTemplate.hasAttribute("Min"))
			min = Double.parseDouble(xmlTemplate.getAttribute("Min"));
		if (xmlTemplate.hasAttribute("Max"))
			max = Double.parseDouble(xmlTemplate.getAttribute("Max"));
		if (xmlTemplate.hasAttribute("Type")) {
			String strType = xmlTemplate.getAttribute("Type").toLowerCase(Locale.getDefault());
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
			// Blanco Item toevoegen
			choiceItems.add(new ChoiceItem(-1, ""));
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
					android.R.layout.simple_spinner_item, choiceItems);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerChoice.setAdapter(adapter);
			spinnerChoice.setOnItemSelectedListener(this);
			addView(spinnerChoice);

		} else {

			// Anders textveld plaatsen
			editTextValue = new EditText(getContext());
			editTextValue.setSingleLine();
			editTextValue.setImeOptions(EditorInfo.IME_ACTION_NEXT);
			if (type == VeldType.DOUBLE)
				editTextValue.setKeyListener(new DigitsKeyListener(true, true));
			if (type == VeldType.INT)
				editTextValue.setKeyListener(new DigitsKeyListener(true, false));

			editTextValue.addTextChangedListener(this);
			addView(editTextValue);
		}

		String s = null;
		if (defaultValue != null)
			s = defaultValue;
		setValue(s);
	}

	public void setValue(String value) {
		activateLink = false;
		if (type == VeldType.CHOICE) {
			for (int i = 0; i < choiceItems.size(); i++) {
				if (choiceItems.get(i).getText().equals(value)) {
					spinnerChoice.setSelection(i);
					break;
				}
			}
		} else {
			editTextValue.setText(value);
		}
		activateLink = true;
	}

	private String getValue() {
		if (type == VeldType.CHOICE) {
			if (spinnerChoice.getSelectedItem() != null) {
				return spinnerChoice.getSelectedItem().toString();
			}
		} else {
			if (editTextValue.getText() != null) {
				return editTextValue.getText().toString();
			}
		}
		return null;
	}

	public Boolean isValide(StringBuilder errMsg) {
		String value = getValue();

		// Required
		if (required && (value == null || value.equals(""))) {
			errMsg.append(getContext().getString(R.string.ValVerplicht));
			return false;
		}

		if (!(value == null || value.equals(""))) {
			Double d = 0.0;
			try {
				d = Double.parseDouble(value);
			} catch (NumberFormatException e) {
			}

			// Min
			if (min != nullValue && (d < min)) {
				errMsg.append(getContext().getString(R.string.ValTeLaag));
				return false;
			}

			// Max
			if (max != nullValue && (d > max)) {
				errMsg.append(getContext().getString(R.string.ValTeHoog));
				return false;
			}
		}
		return true;
	}

	private void executeLink() {
		try {
			if (!(link == null || link.equals(""))) {
				String[] linkArr = link.split(";");
				String toFieldName;
				for (int i = 0; i < linkArr.length; i++) {
					if (linkArr[i].equals("FIELD")) {
						i++;
						toFieldName = linkArr[i];
						i++;
						if (linkArr[i].equals("DELETE")) {
							getDatafieldByName(toFieldName).setValue(null);
						} else if (linkArr[i].equals("SET")) {
							BigDecimal d1, d2;
							i++;
							try {
								if (linkArr[i].equals("THIS")) {
									d1 = new BigDecimal(getValue());
								} else {
									DataField df = getDatafieldByName(linkArr[i]);
									d1 = new BigDecimal(df != null ? df.getValue() : linkArr[i]);
								}
							} catch (NumberFormatException e) {
								d1 = new BigDecimal(0);
							}
							i++;
							while (!(linkArr[i].equals("END"))) {
								try {
									if (linkArr[i].equals("THIS")) {
										d2 = new BigDecimal(getValue());
									} else {
										DataField df = getDatafieldByName(linkArr[i]);
										d2 = new BigDecimal(df != null ? df.getValue() : linkArr[i]);
									}
								} catch (NumberFormatException e) {
									d2 = new BigDecimal(0);
								}
								i++;
								if (linkArr[i].equals("+")) {
									d1 = d1.add(d2);
								} else if (linkArr[i].equals("-")) {
									d1 = d1.subtract(d2);
								} else if (linkArr[i].equals("*")) {
									d1 = d1.multiply(d2);
								} else if (linkArr[i].equals("/")) {
									d1 = d1.divide(d2);
								} else {
									throw new Error();
								}
								i++;
							}
							getDatafieldByName(toFieldName).setValue(d1.toString());
						}
					} else if (linkArr[i].equals("TABTITLE")) { // Vorm:
																// "TABTITLE;prefix;suffix"
						tab.setName(linkArr[++i] + getValue() + linkArr[++i]);
						tab.getGroup().notifyDataSetChanged();
					}
				}
			}
		} catch (Exception e) {
			MdcUtil.showToastLong(getContext().getString(R.string.LinkFout), getContext());
		}
	}

	private DataField getDatafieldByName(String name) {
		String[] adress = name.split("\\.");
		if (adress.length == 1) {
			for (DataField dataField : tab.getDataFields()) {
				if (dataField.getName().equals(adress[0]))
					return dataField;
			}
		} else if (adress.length == 2) {
			for (Tab t : tab.getGroup().getTabs()) {
				if (t.getName().equals(adress[0])) {
					for (DataField dataField : t.getDataFields()) {
						if (dataField.getName().equals(adress[1]))
							return dataField;
					}
				}
			}
		} else {
			for (Group g : tab.getGroup().getFiche().getGroups()) {
				if (g.getName().equals(adress[0])) {
					for (Tab t : g.getTabs()) {
						if (t.getName().equals(adress[1])) {
							for (DataField dataField : t.getDataFields()) {
								if (dataField.getName().equals(adress[2]))
									return dataField;
							}
						}
					}
				}
			}
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public String getLabel() {
		return label;
	}

	@Override
	public void afterTextChanged(Editable s) {
		// Validatie op textveld
		editTextValue.setError(null);
		StringBuilder errMsg = new StringBuilder();
		if (!(isValide(errMsg)))
			editTextValue.setError(errMsg.toString());
		if (activateLink)
			executeLink();
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (activateLink)
			executeLink();
	}

	// -----------------------------------------------------------------------------------------------------
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

}
