package com.ivanceras.db.api;

import org.junit.Test;

import com.ivanceras.db.shared.DAO;

public class DAOUtilsTest {

//	@Test
	public void test() {
		
		ModelDef model = new ModelDef("user");
		model.setAttributes(new String[]{"firstname", "lastname"});
		model.setDataTypes(new String[]{"string", "string"});
		
		DAO[] daoList = new DAO[2];
		DAO dao0 = new DAO("user");
		dao0.set_Value("firstname", "lee");
		dao0.set_Value("lastname", "cesar");
		daoList[0] = dao0;
		
		DAO dao1 = new DAO("user");
		dao1.set_Value("firstname", "ivanceras");
		dao1.set_Value("lastname", "anthony");
		daoList[1] = dao1;
		
	}
	
	@Test
	public void test2() {
		
		ModelDef model = new ModelDef("series");
		model.setAttributes(new String[]{"latitude", "longitude", "magnitude"});
		model.setDataTypes(new String[]{"float", "float", "float"});
		
		
		
		DAO[] daoList = new DAO[2];
		DAO dao0 = new DAO("series");
		dao0.set_Value("latitude", 38.0f);
		dao0.set_Value("longitude", -97.0f);
		dao0.set_Value("magnitude", 2.5084888E8f);
		daoList[0] = dao0;
		
		
		DAO dao1 = new DAO("series");
		dao1.set_Value("latitude", 54.0f);
		dao1.set_Value("longitude", -2.0f);
		dao1.set_Value("magnitude", 2.8366332E7f);
		daoList[1] = dao1;
		
	}

}
