package be.mobiledatacaptator.model;

import java.util.ArrayList;
import java.util.List;

public class Group {

	private String name;
	private boolean Expandable;
	private TabTemplate tabTemplate;
	private List<Tab> tabs;

	public Group(String name) {
		this.name = name;
		tabs = new ArrayList<Tab>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isExpandable() {
		return Expandable;
	}

	public void setExpandable(boolean expandable) {
		Expandable = expandable;
	}

	public TabTemplate getTabTemplate() {
		return tabTemplate;
	}

	public void setTabTemplate(TabTemplate tabTemplate) {
		this.tabTemplate = tabTemplate;
	}

	public List<Tab> getTabs() {
		return tabs;
	}

	public void setTabs(List<Tab> tabs) {
		this.tabs = tabs;
	}

}
