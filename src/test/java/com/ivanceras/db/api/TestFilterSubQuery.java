package com.ivanceras.db.api;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ivanceras.commons.conf.DBConfig;
import com.ivanceras.db.server.core.DB_PostgreSQL;
import com.ivanceras.db.shared.Filter;
import com.ivanceras.db.shared.exception.DatabaseException;


public class TestFilterSubQuery {
	private static EntityManager em;
	private static IDatabase db;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DBConfig config = TestCommons.getDaxDBConfig();
		db = new DB_PostgreSQL(config);
		em = new SynchronousEntityManager(db);
		em.startPrintSqls();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		em.resetDB();
	}

	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void test() throws DatabaseException{
		ModelDef option = new ModelDef("Option");
		option.setNamespace("dax");
		Query query = new Query(option);
		query.addAggregate(new Aggregate("name", Aggregate.MAX));
		query.setSelectAllColumns();
		Query subQuery = new Query(option.copy());
		subQuery.addFilter(new Filter("parent_option_id", Filter.NOT_NULL));
		query.addFilter(new Filter("option_id", Filter.NOT_IN, subQuery));
		em.retrieveRecords(query, false);
	}

}
