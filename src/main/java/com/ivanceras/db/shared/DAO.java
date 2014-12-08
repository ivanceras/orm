package com.ivanceras.db.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.ivanceras.db.api.Pair;

public class DAO{

	protected Map<String, Object> properties = new TreeMap<String, Object>();
	protected DAO[] daoList = null;
	private Map<String, Pair[]> renamedFields;// the query used when this dao is retrieved, used for casting the DAO's when there are conflicting columns in the set of joined tables
	
	private List<String> ignoreColumn = new ArrayList<String>(); //when inserting new records, other columns will be set by the database (i.e created, updated)
	
	private String[] defaultColumnValues; //when updating column, some colums will be automatically set by the database (i.e updated)
	
	private String modelName;


	public DAO(String modelName){
		this.modelName = modelName;
	}

	/**
	 * 
	 * @param dao1
	 * @return true if If the values of all the keys in these dao object matched to the values of the keys on the matched dao object
	 */
	public boolean equals(DAO dao1){
		if(dao1 == null){
			return false;
		}
		else{
			Map<String, Object> d1properties = dao1.getProperties();
			for(Entry<String, Object> prop : properties.entrySet()){
				String key = prop.getKey();
				Object value = prop.getValue();
				Object value1 = d1properties.get(key);
				if(value == null && value1 == null){
					;//should matched!
				}
				if( value != null && value1 != null && !value.equals(value1)){
					return false;
				}
			}
			return true;
		}
	}


	public Object get_Value(String attribute){
		return properties.get(attribute);
	}

	public DAO[] getDaoList() {
		return daoList;
	}

	public String[] get_DefaultedColumnValues() {
		return defaultColumnValues;
	}

	public String[] get_IgnoreColumn() {
		return ignoreColumn.toArray(new String[ignoreColumn.size()]);
	}

	public String getModelName(){
		return modelName;
	}


	public Map<String, Object> getProperties(){
		return properties;
	}

	public Map<String, Pair[]> getRenamedFields() {
		return renamedFields;
	}
	
	public Pair[] getRenamedFields(String tableName) {
		if(renamedFields != null){
			Pair[] pairs = renamedFields.get(tableName);
			if(pairs != null && pairs.length > 0){
				return pairs;
			}
		}
		return null;
	}

	public void set_Value(String attribute, Object value){
		properties.put(attribute, value);
	}
	public void setDaoList(DAO[] daoList) {
		this.daoList = daoList;
	}

	public void set_DefaultColumnValues(String[] defaultColumnValues) {
		this.defaultColumnValues = defaultColumnValues;
	}

	public void add_IgnoreColumn(String... ignoreColumn) {
		for(String igc : ignoreColumn){
			this.ignoreColumn.add(igc);
		}
	}
	
	public void clear_IgnoredColumns(){
		this.ignoreColumn.clear();
	}

	public void setProperties(Map<String, Object> properties){
		this.properties = properties;
	}

	/**
	 * Renamed columns contains the list of columns that conflicts to other Tables mentioned in the whole query, each casting needs to get those list
	 * @param renamedColumns
	 */
	public void setRenamedFields(Map<String, Pair[]> renamedFields) {
		this.renamedFields = renamedFields;
	}

	@Override
	public String toString(){
		return modelName+"->"+properties.toString();
	}
	
}
