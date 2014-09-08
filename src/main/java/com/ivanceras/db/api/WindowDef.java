package com.ivanceras.db.api;

import java.util.ArrayList;
import java.util.List;

public class WindowDef {

	private String windowName;
	
	private List<String> involvedTables = new ArrayList<String>();
	
	public WindowDef(String windowName){
		this.windowName = windowName;
	}
	public WindowDef(String windowName, String[] involvedTables){
		this.windowName = windowName;
		addIncludedTable(involvedTables);
	}
	
	public void addIncludedTable(String...tables){
		for(String t : tables){
			involvedTables.add(t);
		}
	}
	
	public String getWindowName() {
		return windowName;
	}

	public void setWindowName(String windowName) {
		this.windowName = windowName;
	}

	public String[] getInvolvedTables() {
		return involvedTables.toArray(new String[involvedTables.size()]);
	}

	public void setInvolvedTables(String[] involvedTables) {
		this.involvedTables.clear();
		addIncludedTable(involvedTables);
	}
	
	@Override
	public String toString(){
		return windowName+" "+involvedTables;
	}
	
}
