package com.ivanceras.db.api;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ivanceras.commons.conf.DBConfig;
import com.ivanceras.db.model.ModelMetaData;
import com.ivanceras.db.server.core.DB_Jdbc;
import com.ivanceras.db.server.core.DB_PostgreSQL;
import com.ivanceras.db.shared.DAO;
import com.ivanceras.db.shared.Filter;
import com.ivanceras.db.shared.Order;
import com.ivanceras.db.shared.exception.DatabaseException;

public class TestEntityManagerDataManipulation {

	private SynchronousEntityManager em;
	private ModelDef model;
	private static IDatabase db;
	private DAO dao;
	private ModelMetaData meta;
	
//	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DBConfig config = TestCommons.getDBConfig();
		db = new DB_PostgreSQL(config);
	}

//	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

//	@Before
	public void setUp() throws Exception {
		meta = new ModelMetaData();
		db.setModelMetaDataDefinition(meta);
		em = new SynchronousEntityManager(db);
		em.startPrintSqls();
		model = TestCommons.getUserModel();
		meta.addModel(model, false);
		if(em.existModel(model)){
			em.getDB().drop(model, true);
		}
		if(((DB_Jdbc)em.getDB()).schemaExist(model.getNamespace())){
			em.dropNameSpace(model.getNamespace(), true);
		}
		em.createModel(model);
		
		dao = new DAO("User");
		dao.set_Value("name", "lee");
		dao.set_Value("password", "leepwd");
		dao.set_Value("firstname", "lastname");
		em.insertRecord(dao, model, false);
		
	}

//	@After
	public void tearDown() throws Exception {
		em.resetDB();
	}

//	@Test
	public void testRetrieveData() throws DatabaseException {
		Query query = new Query(model);
		DAO[] daoList = em.retrieveRecords(query, false);
		assertNotNull(daoList);
		System.out.println(daoList.length);
		System.out.println(daoList[0]);
		assertEquals(1, daoList.length);
		
	}
	
	//@Test
	public void testDeleteData() throws DatabaseException{
		em.delete(dao);
		Query query = new Query(model);
		DAO[] daoList = em.retrieveRecords(query, false);
		assertNotNull(daoList);
		System.out.println(daoList.length);
		assertEquals(0, daoList.length);
	}
	
//	@Test
	public void testSearch() throws DatabaseException{
		Query q1 = null;
		DAO[] result = em.search(q1, "lee");
		assertNotNull(result);
		assertTrue(result.length >= 1);
		for(DAO dao : result){
			System.out.println(dao);
		}
	}
	
}
