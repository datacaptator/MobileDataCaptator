package be.mobiledatacaptator.model;

import android.content.Context;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

public class UIField extends TableRow {

	DataField dataField;

	TextView textViewLabel;
	EditText editTextValue;
	Spinner spinnerChoice;

	public UIField(Context context, DataField dataField) {
		super(context);

		this.dataField = dataField;

		// Label plaatsen
		textViewLabel = new TextView(context);
		textViewLabel.setText(dataField.getLabel() + ": ");
		// TODO : Field requires API level 14 (current min is 11):
		// android.R.style#TextAppearance_DeviceDefault_Medium
		textViewLabel.setTextAppearance(context, android.R.style.TextAppearance_DeviceDefault_Medium);
		addView(textViewLabel);

		if (dataField.getType() == VeldType.CHOICE) {

			// Idien keuzelijst, spinner plaatsen
			spinnerChoice = new Spinner(context);
			ArrayAdapter<ChoiceItem> adapter = new ArrayAdapter<ChoiceItem>(context,
					android.R.layout.simple_spinner_dropdown_item, dataField.getChoiceItems());
			spinnerChoice.setAdapter(adapter);
			addView(spinnerChoice);
		} else {

			// Anders textveld plaatsen
			editTextValue = new EditText(context);
			if (dataField.getType() == VeldType.DOUBLE)
				editTextValue.setKeyListener(new DigitsKeyListener(true, true));
			if (dataField.getType() == VeldType.INT)
				editTextValue.setKeyListener(new DigitsKeyListener(true, false));
			addView(editTextValue);
		}
	}

	public UIField(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public String getValue() {
		if (dataField.getType().equals(VeldType.CHOICE))
			return spinnerChoice.getSelectedItem().toString();
		return editTextValue.getText().toString();
	}

	public int getId() {
		if (dataField.getType().equals(VeldType.CHOICE))
			return ((ChoiceItem) spinnerChoice.getSelectedItem()).getId();
		return 0;
	}

}
