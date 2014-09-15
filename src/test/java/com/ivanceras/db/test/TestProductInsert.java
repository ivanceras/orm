package com.ivanceras.db.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.ivanceras.db.api.EntityManager;
import com.ivanceras.db.api.IDatabase;
import com.ivanceras.db.api.ModelDef;
import com.ivanceras.db.api.SynchronousEntityManager;
import com.ivanceras.db.sample.BazaarModelMetaData;
import com.ivanceras.db.sample.DAO_Product;
import com.ivanceras.db.server.core.DB_Jdbc;
import com.ivanceras.db.server.core.DB_PostgreSQL;
import com.ivanceras.db.shared.exception.DBConnectionException;
import com.ivanceras.db.shared.exception.DatabaseException;

public class TestProductInsert {
	
	
	@Test
	public void test2() throws DatabaseException, DBConnectionException{
		
		
		DB_PostgreSQL jdbc = mock(DB_PostgreSQL.class);
		when(jdbc.init()).thenAnswer(new Answer<Boolean>(){

			@Override
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				System.out.println("initializing?");
				return true;
			}
			
		});
		
		when(jdbc.getPreparedStatement(anyString(), (Object[])anyObject(), anyBoolean())).thenAnswer(new Answer<Statement>() {

			@Override
			public Statement answer(InvocationOnMock invocation)
					throws Throwable {
				System.out.println("Getting preparedStatement: ");
				return null;
			}
		});
		jdbc.init();
		
		EntityManager em = new SynchronousEntityManager(jdbc);
		when(jdbc.getModelMetaDataDefinition()).thenReturn(new BazaarModelMetaData());
		
		DAO_Product daoProduct = new DAO_Product();
		daoProduct.setName("iphone3gs");
		daoProduct.setDescription("iphone by Apple");
		
		ModelDef model = em.getDefinition(DAO_Product.class);

		System.out.println(model);
		
		
		
		em.insert(daoProduct);
		jdbc.insert(daoProduct, null, model, null);
		
	}
}
