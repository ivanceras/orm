package com.ivanceras.db.server.util;

import java.util.LinkedList;
import java.util.List;

import com.ivanceras.commons.strings.CStringUtils;
import com.ivanceras.db.api.IDatabase;
import com.ivanceras.db.api.ModelDef;
import com.ivanceras.db.api.SchemaTable;
import com.ivanceras.db.shared.exception.DatabaseException;

/**
 * provides the model definition by directly reading the actual table meta data directly from postgresql database.
 * @author lee
 *
 */
public class ModelDefinitionProvider {


	private IDatabase db;
	private String user;
	private String tablePattern;
	private String[] includeSchema;

	public ModelDefinitionProvider(IDatabase db, String user, String tablePattern, 
			String[] includeSchema){
		this.db = db;
		this.user = user;
		this.tablePattern = tablePattern;
		this.includeSchema = includeSchema;
	}

	public ModelDef[] getTableDefinitions() throws DatabaseException {

		System.out.println("Creating the overall list of ModelDefinitions....");
		SchemaTable[] sch_tableNames = getTableNames();
		List<ModelDef> modelList = new LinkedList<ModelDef>();
		for(SchemaTable sch_table : sch_tableNames){
			String table = sch_table.getTableName();
			System.out.println("\tAdding "+table+"....");
			String schema = sch_table.getSchema();
			ModelDef modelDef =  null;
			modelDef =	db.getModelMetaData(schema, table);
			modelDef.setModelName(CStringUtils.getModelName(table));
			modelList.add(modelDef);
		}
		return modelList.toArray(new ModelDef[modelList.size()]);
	}

	private SchemaTable[] getTableNames() throws DatabaseException{
		return db.getTableNames(user, tablePattern, includeSchema);
	}


}
