package com.ivanceras.db.api;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ivanceras.commons.conf.DBConfig;
import com.ivanceras.db.server.core.DB_Jdbc;
import com.ivanceras.db.server.core.DB_PostgreSQL;
import com.ivanceras.db.shared.DAO;
import com.ivanceras.db.shared.exception.DatabaseException;

public class TestEntityManager {

	private static IDatabase db;
	private SynchronousEntityManager em;
	private ModelDef model;
	
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
		em = new SynchronousEntityManager(db);
		model = TestCommons.getUserModel();
		if(em.existModel(model)){
			em.getDB().drop(model, true);
		}
		if(((DB_Jdbc)em.getDB()).schemaExist(model.getNamespace())){
			em.dropNameSpace(model.getNamespace(), true);
		}
		em.createModel(model);
	}

//	@After
	public void tearDown() throws Exception {
		em = null;
	}
	
//	@Test
	public void testInsert() throws DatabaseException{
		DAO dao = new DAO("User");
		dao.set_Value("name", "lee");
		dao.set_Value("password", "leepwd");
		dao.set_Value("firstname", "lastname");
		em.insertRecord(dao, model, false);
	}

}
