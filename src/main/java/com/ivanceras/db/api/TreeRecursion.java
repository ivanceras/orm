package com.ivanceras.db.api;

import com.ivanceras.db.shared.Filter;

public class TreeRecursion {
	
	String column;
	String parentColumn;
	Filter[] filters;
	
	
	public TreeRecursion(String column, String parentColumn, Filter... filters) {
		super();
		this.column = column;
		this.parentColumn = parentColumn;
		this.filters = filters;
	}

	public Filter[] getFilters() {
		return filters;
	}

	public void setFilters(Filter[] filters) {
		this.filters = filters;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getParentColumn() {
		return parentColumn;
	}

	public void setParentColumn(String parentColumn) {
		this.parentColumn = parentColumn;
	}
	

}
