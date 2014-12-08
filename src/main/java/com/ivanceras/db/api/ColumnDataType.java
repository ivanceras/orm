package com.ivanceras.db.api;

public class ColumnDataType{
	
	private String[] columns = null;
	private String[] dataTypes = null;
	private String[] comments = null;

	public void setDataTypes(String[] dataTypes) {
		this.dataTypes = dataTypes;
	}
	public String[] getDataTypes() {
		return dataTypes;
	}
	public void setColumns(String[] columns) {
		this.columns = columns;
	}
	public String[] getColumns() {
		return columns;
	}
	public String[] getComments() {
		return comments;
	}
	public void setComments(String[] comments) {
		this.comments = comments;
	}
}
