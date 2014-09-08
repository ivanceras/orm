package com.ivanceras.db.api;

/**
 * An object to contain parts of preliminary evaluation of a query object
 * @author lee
 *
 */
public class QueryBreakdown {
	
	private String sql;
	private Object[] parameters;
	/**
	 * The mentioned columns of a declared query
	 */
	private String[] mentionedColumns;
	private String name;
	private boolean doComma;
	
	public QueryBreakdown(){
		
	}
	public QueryBreakdown(String sql){
		this.sql = sql;
	}
	public QueryBreakdown(String sql, String[] parameters){
		this.sql = sql;
		this.parameters = parameters;
	}
	
	public QueryBreakdown(String sql, String[] parameters, String[] mentionedColumns, String name){
		this.sql = sql;
		this.parameters = parameters;
		this.mentionedColumns = mentionedColumns;
		this.name = name;
	}
	
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public Object[] getParameters() {
		return parameters;
	}
	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}
	public String[] getMentionedColumns() {
		return mentionedColumns;
	}
	public void setMentionedColumns(String[] mentionedColumns) {
		this.mentionedColumns = mentionedColumns;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isDoComma() {
		return doComma;
	}
	public void setDoComma(boolean doComma) {
		this.doComma = doComma;
	}
	
	
}
