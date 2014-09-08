package com.ivanceras.db.shared.util;

import java.util.ArrayList;
import java.util.List;

import com.ivanceras.db.shared.DAO;

public class DAOUtils {

	public static DAO[] treeMappDAO(DAO[] daoList, String childColumn, String parentColumn){
		List<DAO> treeDao = new ArrayList<DAO>();
		if(daoList != null){
			for(DAO tdao : daoList){
				Object thisKey = tdao.get_Value(childColumn);
				//			System.out.println(thisKey);
				Object parentKey = tdao.get_Value(parentColumn);
				if(parentKey == null){
					treeDao.add(tdao);
				}
				List<DAO> childDaoList = new ArrayList<DAO>();
				for(DAO cdao : daoList){
					Object childParentKey = cdao.get_Value(parentColumn);
					if(childParentKey != null && childParentKey.equals(thisKey)){
						//					System.out.println("\t"+cdao.get_Value(childColumn));
						childDaoList.add(cdao);
					}
				}
				tdao.setDaoList(childDaoList.toArray(new DAO[childDaoList.size()]));
			}
		}
		return treeDao.toArray(new DAO[treeDao.size()]);
	}
	

}
