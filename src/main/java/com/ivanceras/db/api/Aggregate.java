package com.ivanceras.db.api;

import java.util.LinkedList;
import java.util.List;

import com.ivanceras.db.shared.DAO;

public class Aggregate {
	
	/** The average of records	 */
	public final static String AVG = "AVG";
	
	public final static String COUNT = "COUNT";
	
	public final static String FIRST = "FIRST";
	
	public final static String LAST = "LAST";
	
	public final static String MAX = "MAX";
	
	public final static String MIN = "MIN";
	
	public final static String SUM = "SUM";
	
	
	public final static String ASTERISK = "*";
	
	public final static String DISTINCT = "DISTINCT";
	
	
	
	private List<String> function = new LinkedList<String>();
	
	private String column;
	
	private String asColumn;
	
	private boolean preppendModelName;
	
	private Class<? extends DAO> modelClass;

	public Aggregate(String column, String function){
		this.column = column;
		this.function.add(function);
		this.preppendModelName = true;
	}
	
	public Aggregate(Class<? extends DAO> modelClass, String column, String function){
		this.column = column;
		this.function.add(function);
		this.modelClass = modelClass;
		this.preppendModelName = true;
	}
	
	public Aggregate(Class<? extends DAO> modelClass, String column, String function, String asColumn){
		this.column = column;
		this.function.add(function);
		this.modelClass = modelClass;
		this.asColumn = asColumn;
		if(column.equals("*")){
			this.preppendModelName = false;
		}
	}
	
	public Aggregate(Class<? extends DAO> modelClass, String column, String[] function, String asColumn){
		this.column = column;
		for(String f : function){
			this.function.add(f);
		}
		this.modelClass = modelClass;
		this.asColumn = asColumn;
		if(column.equals("*")){
			this.preppendModelName = false;
		}
	}
	
	public Class<? extends DAO> getModelClass(){
		return modelClass;
	}
	
	
	public String getColumn() {
		return column;
	}

	public String[] getFunctions() {
		return function.toArray(new String[function.size()]);
	}

	public String getAsColumn(){
		if(asColumn == null){
			StringBuffer fname = new StringBuffer();
			for(String f : function){
				fname.append(f.toLowerCase());
				fname.append("_");
			}
			return fname+column;
		}
		else{
			return asColumn;
		}
	}
	
	public boolean preppendModelName(){
		return this.preppendModelName;
	}
	
	
	
}
