package com.ivanceras.db.api;

import com.ivanceras.commons.conf.DBConfig;


public class TestCommons {
	
	public static DBConfig getDBConfig(){
		DBConfig config = new DBConfig("postgresql", 
				"localhost", "5432", "appd", "user", "pwd");
		return config;
	}
	
	public static ModelDef getUserModel(){
		String   namespace146                               = "portal";
		String   modelName146                               = "User";
		String[] attributes146                              = {"name", "password", "firstname", "lastname", "email", "user_id", "photo"};
		String   generatedAttribute146                      = null;
		String[] primaryAttributes146                       = {"user_id"};
		String[] uniqueAttributes146                        = {"name"};
		String[] dataTypes146                               = {"String", "String", "String", "String", "String", "String", "String"};

		String[] hasOneLocalColumn146                       = {};
		String[] hasOne146                                  = {};
		String[] hasOneReferencedColumn146                  = {};


		boolean  caseSensitive146								= false;
		
		ModelDef modelDef146 = new ModelDef(
			namespace146, 
			modelName146, 
			attributes146,
			generatedAttribute146,
			dataTypes146, 
			primaryAttributes146, 
			uniqueAttributes146, 
			hasOne146, 
			hasOneLocalColumn146, 
			hasOneReferencedColumn146, 
			null, 
			null, 
			null, 
			caseSensitive146);
				
		modelDef146.setPolymorphic(false);
		modelDef146.setTableName("user");
		modelDef146.setDescription("null");
		return modelDef146;
	}

}
