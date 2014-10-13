package com.ivanceras.db.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QueryAnalysis {
	
	//TODO: need to clean up, unify the container of renamed columns...

	//all columns in all tables
	private Map<String, String> renameColumns = new HashMap<String, String>();
	private Map<ModelDef, List<String>> conflictModelColumn = new HashMap<ModelDef, List<String>>();
	
	//renamed columns for each table
	private Map<String, ColumnPair> renamedColumnPairs = new HashMap<String, ColumnPair>();
	
	//conflicted columns only
	Set<String> allColumnConflict = new HashSet<String>();

	Query query;


	public QueryAnalysis(Query query) {
		this.query = query;
	}

	public void determineConflictingColumns(ModelDef model){
		ModelDef[] involvedModels = query.getInvolvedModels();
		for(int i = 0; i < involvedModels.length; i++){
			ModelDef invmodel = involvedModels[i];
			if(!invmodel.equals(model)){
				String[] sameAttributes = invmodel.getSameAttributes(model);
				if(sameAttributes != null && sameAttributes.length > 0){
					addToConflictingModelColumns(model, sameAttributes);
					addToConflictingModelColumns(invmodel, sameAttributes);
					
					for(String sa : sameAttributes){//list down all to allColumns that has conflict
						allColumnConflict.add(sa);
					}
				}
			}
		}
	}

	/**
	 * This is called before of each joined model
	 * @param model
	 * @param columns
	 */
	private void addToConflictingModelColumns(ModelDef model, String[] columns){
		List<String> conflictColumns = null;
		if(conflictModelColumn.containsKey(model)){
			conflictColumns = conflictModelColumn.get(model);
		}
		else{
			conflictColumns = new ArrayList<String>();
		}
		for(String column : columns){
			if(!conflictColumns.contains(column)){
				conflictColumns.add(column);
			}
		}
		conflictModelColumn.put(model, conflictColumns);

		ColumnPair columnPairs = null;
		String tableName = model.getModelName();
		for(String conflictColumn : conflictColumns){
			String asColumn = getRenameColumnValue(model, conflictColumn);
			rename(model, conflictColumn, asColumn);
			if(columnPairs == null){
				columnPairs = new ColumnPair(conflictColumn, asColumn);
			}
			else{
				columnPairs.add(new ColumnPair(conflictColumn, asColumn));
			}
		}
		renamedColumnPairs.put(tableName, columnPairs);
	}

	public void addToRenamedColumnPairs(ModelDef model, String column, String asColumn){
		ColumnPair columnPairs = renamedColumnPairs.get(model.getTableName());
		if(columnPairs == null){
			columnPairs = new ColumnPair(column, asColumn);
		}else{
			columnPairs.add(new ColumnPair(column, asColumn));
		}
		String tableName = model.getModelName();
		renamedColumnPairs.put(tableName, columnPairs);
		
	}

	public Map<String, ColumnPair> getRenamedColumnPairs(){
		return renamedColumnPairs;
	}

	private String getRenameColumnValue(ModelDef model, String column){
		return (model.getTableName()+"_"+column).toLowerCase();
	}


	/**
	 * Not very simple, since you have to support renaming a column (without the model) as well.
	 */
	public void rename(ModelDef model, String column, String asColumn){
		String hash = getRenameColumnValue(model, column);
		renameColumns.put(hash, asColumn);
		addToRenamedColumnPairs(model, column, asColumn);
	}


	public String getRenamed(ModelDef model, String column){
//		if(conflictModelColumn.containsKey(model)){
			String hash = getRenameColumnValue(model, column);
			return renameColumns.get(hash);
//		}
//		return null;
	}

	public boolean hasConflictedColumn(String column) {
		return allColumnConflict.contains(column);
	}

}
