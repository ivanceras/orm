package com.ivanceras.db.server.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.ivanceras.commons.strings.CStringUtils;
import com.ivanceras.db.api.IDatabase;
import com.ivanceras.db.api.IDatabaseDev;
import com.ivanceras.db.api.ModelDef;
import com.ivanceras.db.api.Pair;
import com.ivanceras.db.shared.exception.DatabaseException;

/**
 * provides the model definition by directly reading the actual table meta data directly from postgresql database.
 * @author lee
 *
 */
public class ModelDefinitionProvider {


	private IDatabaseDev db;
	private String user;
	private String tablePattern;
	private String[] includeSchema;

	public ModelDefinitionProvider(IDatabaseDev db, String user, String tablePattern, 
			String[] includeSchema){
		this.db = db;
		this.user = user;
		this.tablePattern = tablePattern;
		this.includeSchema = includeSchema;
	}

	public ModelDef[] getTableDefinitions() throws DatabaseException {

		System.out.println("Creating the overall list of ModelDefinitions....");
		Pair[] sch_tableNames = getTableNames();
		List<ModelDef> modelList = new LinkedList<ModelDef>();
		for(Pair sch_table : sch_tableNames){
			String table = sch_table.getRight();
			System.out.println("\tAdding "+table+"....");
			String schema = sch_table.getLeft();
			ModelDef modelDef =  null;
			modelDef =	db.getModelMetaData(schema, table);
			modelDef.setModelName(CStringUtils.getModelName(table));
			modelList.add(modelDef);
		}
		Collections.sort(modelList, new Comparator<ModelDef>() {

			@Override
			public int compare(ModelDef arg0, ModelDef arg1) {
				if(arg0 != null && arg1 != null){
					String table0 = arg0.getTableName();
					String table1 = arg1.getTableName();
					if(table0 != null && table1 != null){
						return table0.compareTo(table1);
					}
				}
				return 0;
			}
		});
		return modelList.toArray(new ModelDef[modelList.size()]);
	}

	private Pair[] getTableNames() throws DatabaseException{
		return db.getTableNames(user, tablePattern, includeSchema);
	}


}
