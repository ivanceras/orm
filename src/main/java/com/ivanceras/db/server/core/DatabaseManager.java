/*******************************************************************************
 * Copyright by CMIL
 ******************************************************************************/
package com.ivanceras.db.server.core;

import com.ivanceras.commons.conf.DBConfig;
import com.ivanceras.db.api.IDatabase;
import com.ivanceras.db.shared.exception.DBConnectionException;
import com.ivanceras.db.shared.exception.DatabaseException;

public class DatabaseManager {

	public static final String POSTGRESQL = "postgresql";
	public static final String BIGTABLE = "bigtable";
	public static final String ORACLE = "oracle";
	public static final String MYSQL = "mysql";
	public static final String SQLITE = "sqlite";
	public static final String SQLDROID = "sqldroid";
	public static final String HTML5 = "html5";
	public static final String MONGO = "mongo";
	public static final String CASSANDRA = "cassandra";
	public static final String HIVE = "hive";
	public static final String DYNAMO = "dynamo";

	public static IDatabase create(DBConfig config) throws DBConnectionException, DatabaseException{
		IDatabase db = null;
		String dbType = config.getDbType();
		if(dbType.equals(POSTGRESQL)){
			db = new DB_PostgreSQL(config);
			System.out.println("connecting using postgresql");
			return db;
		}
		//		else if(dbType.equals(HIVE )){ /** hive support */
		//			db = new DB_Hive(config); 
		//			return db;
		//		}
		//		else if(dbType.equals(CASSANDRA)){ /** tentative support for Cassandra */
		//			db = new DB_Cassandra(config); 
		//			return db;
		//		}
		//		else if(dbType.equals(DYNAMO)){ /** tentative support for Cassandra */
		//			db = new DB_Dynamo(config); 
		//			return db;
		//		}
		//		else if(dbType.equals(ORACLE)){ /** tentative support for Oracle */
		//			db = new DB_Oracle(config, overwriteFile); 
		//			return db;
		//		}
		//				else if(dbType.equals(MYSQL)){ /** mysql */
		//					db = new DB_MySQL(config);
		//					return db;
		//				}
		//		else if(dbType.equals(MONGO)){ /** speculative support for Mongo DB */
		//			try {
		//				db = new DB_Mongo(config, overwriteFile);
		//				return db;
		//			} catch (DatabaseException e) {
		//				e.printStackTrace();
		//			} 
		//		}
		//		else if(dbType.equals(BIGTABLE)){ /** tentative support for Google BigTable */
		//			db = new DB_BigTable(config);
		//			System.out.println("Created big table...");
		//			return db;
		//		}
		else{
			String msg = "Database of type "+dbType+" not yet supported!";
			throw new DBConnectionException(msg);
		}
	}

}
