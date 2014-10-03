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
		ColumnPair pair = dao.getRenamedColumns(modelName);//This is the name of the model to cast the dao to
		if(pair != null){
			ColumnPair[] pairs = pair.getPairs();
			for(ColumnPair p : pairs){
				if(dao.getProperties().containsKey(p.getColumn2())){
					instance.set_Value(p.getColumn1(), dao.get_Value(p.getColumn2()));
				}
			}
		}
		instance.setRenamedColumns(dao.getRenamedColumns());
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
