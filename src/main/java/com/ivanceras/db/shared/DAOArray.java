package com.ivanceras.db.shared;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ivanceras.db.shared.exception.DAOArrayException;

/**
 * This is useful for compressing the resulting DAO
 * @author lee
 *
 */
public class DAOArray {
	
	String modelName;
	String[] attributes;
	List<Object[]> data = new ArrayList<Object[]>();
	
	public void add(DAO dao) throws DAOArrayException{
		if(dao == null){
			return;
		}
		if(modelName == null){
			Map<String, Object> prop = dao.getProperties();
			modelName = dao.getModelName();
			attributes = new String[prop.size()];
			int index = 0;
			for(Entry<String, Object> entry : prop.entrySet()){
				attributes[index] = entry.getKey();
				index++;
			}
		}
		if(dao.getModelName() != modelName){
			throw new DAOArrayException("Can not contain dao from different models");
		}
		Object[] rows = new Object[attributes.length];
		for(int i = 0; i < attributes.length; i++){
			rows[i] = dao.get_Value(attributes[i]);
		}
		data.add(rows);
		
	}
	
	/**
	 * convert a list of dao into this format
	 * @param daoList
	 * @return
	 * @throws DAOArrayException
	 */
	public DAOArray fromDao(DAO[] daoList) throws DAOArrayException{
		for(DAO dao : daoList){
			add(dao);
		}
		return this;
	}
	
	
	/**
	 * From this format convert back to DAO
	 * @return
	 */
	public DAO[] convert(){
		int size = data.size();
		DAO[] daoList = new DAO[size];
		
		for(int i = 0; i < size; i++){
			Object[] dat = data.get(i);
			DAO dao = new DAO(modelName);
			for(int j = 0; j < attributes.length; j++){
				dao.set_Value(attributes[j], dat[j]);
			}
			daoList[i] = dao;
		}
		return daoList;
	}
	
	public int size(){
		return data.size();
	}
	

}
