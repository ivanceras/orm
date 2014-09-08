package com.ivanceras.db.api;

public class CombinedQuery {
	
	private Query query;
	private String combineType;
	private String combineModifier;
	
	public CombinedQuery(Query query, String combineType, String combineModifier){
		this.query = query;
		this.combineType = combineType;
		this.combineModifier = combineModifier;
	}

	public Query getQuery() {
		return query;
	}

	public String getCombineType() {
		return combineType;
	}

	public String getCombineModifier() {
		return combineModifier;
	}
	

}
