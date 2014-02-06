package be.mobiledatacaptator.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TableLayout;
import be.mobiledatacaptator.model.DataField;
import be.mobiledatacaptator.model.Tab;

public class TabFragment extends Fragment implements ITitleFragment {

	private Tab tab;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		ScrollView scrollView = new ScrollView(getActivity());
		TableLayout tableLayout = new TableLayout(getActivity());
		if (tab != null) {
			for (DataField dataField : tab.getDataFields()) {
				View v = dataField.getUiField();
				ViewGroup parent = (ViewGroup) v.getParent();
				if (!(parent == null))
					parent.removeView(v);
				tableLayout.addView(v);
			}
		}
		tableLayout.setColumnStretchable(1, true);
		scrollView.addView(tableLayout);

		return scrollView;

	}

	public Tab getTab() {
		return tab;
	}

	public void setTab(Tab tab) {
		this.tab = tab;
	}

	@Override
	public String getTitle() {
		return tab.getName();
	}

}