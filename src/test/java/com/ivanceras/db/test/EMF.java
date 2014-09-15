package com.ivanceras.db.test;

import com.ivanceras.db.api.EntityManager;
import com.ivanceras.db.api.SynchronousEntityManager;
import com.ivanceras.db.sample.BazaarModelMetaData;
import com.ivanceras.db.server.core.DB_PostgreSQL;
import com.ivanceras.db.shared.exception.DatabaseException;

public class EMF {
	
	public static EntityManager get() throws DatabaseException{
		DB_PostgreSQL pg = new DB_PostgreSQL();
		EntityManager em = new SynchronousEntityManager(pg);
		em.getDB().setModelMetaDataDefinition(new BazaarModelMetaData());
		return em;
	}

}
