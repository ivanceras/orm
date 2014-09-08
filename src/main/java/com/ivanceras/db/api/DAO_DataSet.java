package com.ivanceras.db.api;

import java.sql.ResultSet;
import java.util.Map;

import com.ivanceras.db.model.ModelMetaData;
import com.ivanceras.db.server.core.DB_Jdbc;
import com.ivanceras.db.shared.DAO;
import com.ivanceras.db.shared.exception.DatabaseException;

/**
 * For fetching a really huge amount of data
 * @author lee
 *
 */
public interface DAO_DataSet {

	public void init(ModelMetaData meta, ModelDef model, ResultSet rs, boolean autoCast, DB_Jdbc jdbc);

	public void setSelectAllColumns(Boolean selectAllColumns);
	public void setDistintColumns(String[] distinctColumns);
	public void setAggrs(Aggregate[] aggrs);
	public void setRenamedColumns(Map<String, ColumnPair> renamedColumns);

	public <T extends DAO> T get() throws DatabaseException;

	public boolean next() throws DatabaseException;

}
