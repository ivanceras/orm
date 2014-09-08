package com.ivanceras.db.api;


/**
 * Declared Query is equivalent to CTE ( common table expression)
 * @author lee
 *
 */
public class DeclaredQuery {
	
	private String[] columns;
	private Query query;
	private boolean recursive = false;
	private String declaredName;
	
	public DeclaredQuery(String declaredName, String[] columns, Query query, boolean recursive){
		this.columns = columns;
		this.query = query;
		this.recursive = recursive;
		this.declaredName = declaredName;
	}

	public String[] getColumns() {
		return columns;
	}

	public Query getQuery() {
		return query;
	}

	public boolean isRecursive() {
		return recursive;
	}

	public String getDeclaredName() {
		return declaredName;
	}
	

}
