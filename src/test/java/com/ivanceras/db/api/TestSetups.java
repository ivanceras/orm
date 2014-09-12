package com.ivanceras.db.api;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ivanceras.commons.conf.DBConfig;
import com.ivanceras.db.server.core.DB_PostgreSQL;

/**
 * This would have to set-up the database before tests that involved data will be tested
 * @author lee
 *
 */
public class TestSetups {
	
	private static EntityManager em;
	private static IDatabase db;

//	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DBConfig config = TestCommons.getDBConfig();
		db = new DB_PostgreSQL(config);
		em = new SynchronousEntityManager(db);
		em.createModel(TestCommons.getUserModel());
	}

//	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		em.resetDB();
	}

	
//	@Before
	public void setUp() throws Exception {
	}

//	@After
	public void tearDown() throws Exception {
	}

//	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
