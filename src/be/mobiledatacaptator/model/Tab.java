package be.mobiledatacaptator.model;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TableLayout;
import be.mobiledatacaptator.fragments.ITitleFragment;

public class Tab extends Fragment implements ITitleFragment {

	protected String name;
	private List<DataField> dataFields = new ArrayList<DataField>();
	private Context context;

	private Element xmlTemplate;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		ScrollView scrollView = new ScrollView(getActivity());
		TableLayout tableLayout = new TableLayout(getActivity());
		for (View v : dataFields) {
			ViewGroup parent = (ViewGroup) v.getParent();
			if (!(parent == null))
				parent.removeView(v);
			tableLayout.addView(v);
		}
		tableLayout.setColumnStretchable(1, true);
		scrollView.addView(tableLayout);

		return scrollView;
	}

	private void loadTemplate() {
		if (xmlTemplate != null) {
			if (xmlTemplate.hasAttribute("Name"))
				name = xmlTemplate.getAttribute("Name");
			NodeList fields = xmlTemplate.getElementsByTagName("Field");
			for (int k = 0; k < fields.getLength(); k++) {
				Element fieldEle = (Element) fields.item(k);
				dataFields.add(new DataField(context, fieldEle));
			}
		}
	}

	public Element getXmlTemplate() {
		return xmlTemplate;
	}

	public void setXmlTemplate(Element xmlTemplate) {
		this.xmlTemplate = xmlTemplate;
		loadTemplate();
	}

	public void setContext(Context context) {
		this.context = context;
	}

	@Override
	public String getTitle() {
		return name;
	}

}
