package com.ivanceras.db.server.util;

import com.ivanceras.db.api.EntityManager;
import com.ivanceras.db.api.ModelDef;
import com.ivanceras.db.shared.exception.DatabaseException;

public class ModelToTableCreator {
	
	private EntityManager em;
	private ModelDef[] modelList;
	
	
	public ModelToTableCreator(EntityManager em, ModelDef[] modelList){
		this.em = em;
		this.modelList = modelList;
	}
	
	public void start() throws DatabaseException{
		for(ModelDef model : modelList){
			em.createModel(model);
		}
		setConstraints();
	}
	
	private void setConstraints() throws DatabaseException{
		for(ModelDef model : modelList){
			em.setForeignConstraint(model);
		}
	}
}
