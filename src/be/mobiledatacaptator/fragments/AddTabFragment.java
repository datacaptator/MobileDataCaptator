package be.mobiledatacaptator.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import be.mobiledatacaptator.R;
import be.mobiledatacaptator.adapters.FichePagerAdapter;
import be.mobiledatacaptator.model.Group;
import be.mobiledatacaptator.model.Tab;

public class AddTabFragment extends Fragment implements ITitleFragment {

	private FichePagerAdapter fichePagerAdapter;
	private Group group;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		GridLayout gridLayout = new GridLayout(getActivity());
		Button button = new Button(getActivity());
		button.setText(R.string.AddNew);
		gridLayout.addView(button);

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				Tab tab = group.getTabTemplate().getNewTab();
//				group.getTabs().add(tab);
//				TabFragment fragment = new TabFragment();
//				fragment.setTab(tab);
//				//fichePagerAdapter.addItemBeforeLast(fragment);
//				fichePagerAdapter.addItem(fragment);
			}
		});

		return gridLayout;
	}

	@Override
	public String getTitle() {
		return "Add New";
		//return getResources().getString(R.string.AddNew);
	}

	public FichePagerAdapter getFichePagerAdapter() {
		return fichePagerAdapter;
	}

	public void setFichePagerAdapter(FichePagerAdapter fichePagerAdapter) {
		this.fichePagerAdapter = fichePagerAdapter;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

}
