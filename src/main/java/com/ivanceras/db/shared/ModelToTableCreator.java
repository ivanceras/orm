package com.ivanceras.db.shared;

import com.ivanceras.commons.client.Console;
import com.ivanceras.db.api.EntityManager;
import com.ivanceras.db.api.ModelDef;
import com.ivanceras.db.shared.exception.DatabaseException;

public class ModelToTableCreator {
	
	private EntityManager em;
	private ModelDef[] modelList;
	
	
	public ModelToTableCreator(EntityManager em, ModelDef[] modelList){
		this.em = em;
		this.modelList = modelList;
		Console.log("Creating tables for "+modelList.length);
	}
	
	public void start() throws DatabaseException{
		for(ModelDef model : modelList){
			em.createModel(model);
		}
		setPrimaryConstraints();
		setForeignConstraints();
	}
	
	private void setPrimaryConstraints() throws DatabaseException{
		for(ModelDef model : modelList){
			Console.log("primary for: "+model.getTableName()+" "+modelList.length);
			em.setPrimaryConstraint(model);
		}
	}
	
	private void setForeignConstraints() throws DatabaseException{
		for(ModelDef model : modelList){
			em.setForeignConstraint(model);
		}
	}
}
