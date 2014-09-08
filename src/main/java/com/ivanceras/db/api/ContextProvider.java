package com.ivanceras.db.api;

import com.ivanceras.db.shared.DAO;
import com.ivanceras.db.shared.exception.DatabaseException;

public interface ContextProvider {

	public static final String DELETE = "DELETE";
	public static final String INSERT = "INSERT";
	public static final String UPDATE = "UPDATE";
	
	public void recordUpdateChange(DAO dao, DAO[] affectedRecords) throws DatabaseException;

	public void recordInsertChange(DAO dao) throws DatabaseException;

	public void recordDeleteChange(ModelDef model, DAO[] affectedRecords) throws DatabaseException;

	boolean isEnableRecordChangelog();

	void setEnableRecordChangelog(boolean enableRecordChangelog);

	/**
	 * by default update and delete should be recorded, while update can be optional
	 * @return
	 */
	boolean isEnableInsertChangelog();

	void setEnableInsertChangelog(boolean enableInsertChangelog);

	/*
	public <T> T[] getRecordHistory(Class<? extends DAO> daoClass)
			throws DatabaseException;

	public <T> T[] getRecordHistory(Class<? extends DAO> daoClass,
			int itemsPerPage, int page) throws DatabaseException;

	public <T> T[] getRecordHistory(Class<? extends DAO> daoClass,
			Order order, Filter[] filters) throws DatabaseException;
	*/
}
