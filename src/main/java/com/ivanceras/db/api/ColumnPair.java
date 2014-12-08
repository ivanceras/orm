package com.ivanceras.db.api;

import java.util.ArrayList;
import java.util.List;


//TODO:cleanup this one
public class ColumnPair{
	
	private String column1;
	private String column2;
	
	private List<ColumnPair> pairs = new ArrayList<ColumnPair>();
	
	public ColumnPair(String column1, String column2){
		this.column1 = column1;
		this.column2 = column2;
		pairs.add(this);
	}
	
	public ColumnPair(String[] column1, String[] column2){
		for(int i = 0; i < column1.length; i++){
			String m1c = column1[i];
			String m2c = column2[i];
			pairs.add(new ColumnPair(m1c, m2c));
		}
	}
	
	public void add(ColumnPair columnPair){
		pairs.add(columnPair);
	}
	
	public ColumnPair[] getPairs(){
		return pairs.toArray(new ColumnPair[pairs.size()]);
	}

	public String getColumn1() {
		return column1;
	}

	public String getColumn2() {
		return column2;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(ColumnPair pair : pairs){
			sb.append(pair.getColumn1()+"->"+pair.getColumn2());
		}
		return sb.toString();
	}
	
}
