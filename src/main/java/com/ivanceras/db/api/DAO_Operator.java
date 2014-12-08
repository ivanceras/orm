package com.ivanceras.db.api;

import java.util.Map;
import java.util.Map.Entry;

import com.ivanceras.db.shared.DAO;

public class DAO_Operator {
	
	public static <T extends DAO> T cast(T instance, String modelName, DAO dao) 	{
		if(dao == null){
			return null;
		}
		copyProperties(instance, dao);
		Pair[] pairs = dao.getRenamedFields(modelName);//This is the name of the model to cast the dao to
		
		if(pairs != null){
			for(Pair pair : pairs){
				if(dao.getProperties().containsKey(pair.getRight())){
					String origColumn = pair.getLeft();
					Object value = dao.get_Value(pair.getRight());
					instance.set_Value(origColumn, value);
				}
			}
		}
		instance.setRenamedFields(dao.getRenamedFields());
		return instance;
	}

	public static <T extends DAO> T[] cast(T[] instanceArr, String modelName, DAO[] daoList){
		if(daoList == null || instanceArr == null){
			return null;
		}
		for(int i = 0; i < daoList.length; i++)	{
			instanceArr[i] = cast(instanceArr[i], modelName, daoList[i]);
		}
		return instanceArr;
	}
	
	
	private static <T> void copyProperties(T instance, DAO dao){
		
		Map<String, Object> properties = dao.getProperties();
		for(Entry<String, Object> entry : properties.entrySet()){
			String key = entry.getKey();
			Object value = entry.getValue();
			((DAO) instance).set_Value(key, value);
		}
	}


}
