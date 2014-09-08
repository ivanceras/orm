package com.ivanceras.db.api;

import com.ivanceras.commons.conf.DBConfig;
import com.ivanceras.db.shared.DAO;
import com.ivanceras.db.shared.Filter;
import com.ivanceras.db.shared.Order;
import com.ivanceras.db.shared.exception.DatabaseException;


public abstract class DB_Odbms {
	
	protected String dbName;
	protected boolean initialized = false;


	static long writeCalls = 0;
	static boolean countWrites = false;
	
	protected String dbHost;
	protected String dbPort;
	protected String dbUser;
	protected String dbPassword;
	protected String dbSchema;

	public DB_Odbms(DBConfig config, String overwriteFile) throws DatabaseException{
		this.dbHost = config.getDbHost();
		this.dbPort = config.getDbPort();
		this.dbName = config.getDbName();
		this.dbUser = config.getDbUser();
		this.dbPassword = config.getDbPassword();
	}
	
	public DAO[] getChildren(DAO parentDao,Class<? extends DAO> relatedClass, Order[] orders, Filter[] filters, 
			Integer page, Integer itemPerPage, Boolean doCount) throws DatabaseException{
		return null;
		
	}

	public DAO[] getHasMany(DAO parentDao,Class<? extends DAO> relatedClass, 
			Class<? extends DAO> pivotTable, Order[] orders, Filter[] filters, 
			Integer page, Integer itemPerPage, Boolean doCount) throws DatabaseException{
		return null;
		
	}

	public DAO getHasOne(DAO dao, Class<? extends DAO> daoClass, Filter[] filters, Order...orders) throws DatabaseException{
		return null;
		
	}

	public DAO[] getTree(Class<? extends DAO> daoClass, String column, String parentColumn, Boolean doHierarchy, Filter... filters) throws DatabaseException{
		return null;
	}

	
}
