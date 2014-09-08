package com.ivanceras.db.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ivanceras.db.api.DAOFactory;
import com.ivanceras.db.api.ModelDef;
import com.ivanceras.db.server.util.DAOGenerator;
import com.ivanceras.db.shared.DAO;
import com.ivanceras.db.shared.exception.DatabaseException;


/**
 * This class is inherited by a Generated Class {@link DAOGenerator#generateModelMetaData}
 * @author lee
 *
 */

public class ModelMetaData{

	
	Map<Class<? extends DAO>, ModelDef> modelDefList = null;
	Map<String, ModelDef> namedModelDefList = null;
	Map<Class<? extends DAO>, String> modelNameListNdx = null;
	Map<String, Class<? extends DAO>> classListNdx = null;
	private DAOFactory instanceProvider = null;
	
	public ModelMetaData(){
		this.modelDefList = new LinkedHashMap<Class<? extends DAO>, ModelDef>();
		this.namedModelDefList = new LinkedHashMap<String, ModelDef>();
		this.modelNameListNdx = new LinkedHashMap<Class<? extends DAO>, String>();
		this.classListNdx = new LinkedHashMap<String, Class<? extends DAO>>();
	}
	
	public ModelMetaData(Map<Class<? extends DAO>, ModelDef> modelDefList, Map<String, ModelDef> namedModelDefList, Map<Class<? extends DAO>, String> modelNameList, Map<String, Class<? extends DAO>> classList){
		this.modelDefList = modelDefList;
		this.namedModelDefList = namedModelDefList;
		this.modelNameListNdx = modelNameList;
		this.classListNdx = classList;
	}
	
	public ModelMetaData addMeta(ModelMetaData metaData1) {
		modelDefList.putAll(metaData1.modelDefList);
		modelNameListNdx.putAll(metaData1.modelNameListNdx);
		classListNdx.putAll(metaData1.classListNdx);
		return this;
	}
	
	/**
	 * 
	 * This is for adding models that are dynamic, if overwrite, then replace the current model if it exist
	 * @param model
	 * @return
	 * @throws DatabaseException
	 */
	public boolean addModel(ModelDef model, boolean overwrite) throws DatabaseException{
		String modelName = model.getModelName();
		if(!overwrite && this.namedModelDefList.containsKey(modelName)){
			throw new DatabaseException("Model ["+modelName+"] can not be added since It already exists");
		}
		else{
			this.namedModelDefList.put(modelName, model);
			return true;
		}
	}
	
	public List<ModelDef> getModelDefinitionList(){
		List<ModelDef> list = new ArrayList<ModelDef>();
		for(Entry<Class<? extends DAO>, ModelDef> entry : modelDefList.entrySet()){
			list.add(entry.getValue());
		}
		return list;
	}
	

	
	public ModelDef getDefinition(Class<? extends DAO> daoClass){
		if(modelDefList == null){
			System.out.println("Model definition List is not set!");
		}
		if(modelDefList.containsKey(daoClass)){
			return modelDefList.get(daoClass);
		}
		else{
			String msg = "No model definition found for ["+daoClass+"]";
			try {
				throw new DatabaseException(msg);
			} catch (DatabaseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public Class<? extends DAO> getDaoClass(String modelName){
		if(classListNdx == null){
			System.out.println("Class List Index is not set");
		}
		if(classListNdx.containsKey(modelName)){
			return classListNdx.get(modelName);
		}else{
			System.out.println("No DAO class index found for "+modelName);
		}
		return null;
	}

	public ModelDef getDefinition(String modelName){
		if(namedModelDefList == null){
			System.out.println("Model definition List is not set!");
		}
		if(namedModelDefList.containsKey(modelName)){
			return namedModelDefList.get(modelName);
		}
		else{
			String msg = "No model definition found for ["+modelName+"]";
			try {
				throw new DatabaseException(msg);
			} catch (DatabaseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public String getModelName(Class<? extends DAO> daoClass) {
		return modelNameListNdx.get(daoClass);
	}

	public DAOFactory getInstanceProvider() {
		return this.instanceProvider;
	}
	public void setInstanceProvider(DAOFactory ip) {
		this.instanceProvider = ip;
	}
}
