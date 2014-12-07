package com.ivanceras.db.test;


import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Statement;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.ivanceras.db.api.EntityManager;
import com.ivanceras.db.api.ModelDef;
import com.ivanceras.db.api.SynchronousEntityManager;
import com.ivanceras.db.sample.SampleModelMetaData;
import com.ivanceras.db.sample.DAO_Product;
import com.ivanceras.db.sample.DAO_Users;
import com.ivanceras.db.sample.Table;
import com.ivanceras.db.sample.TableColumns.users;
import com.ivanceras.db.server.core.DB_PostgreSQL;
import com.ivanceras.db.shared.exception.DBConnectionException;
import com.ivanceras.db.shared.exception.DatabaseException;
import com.ivanceras.keyword.sql.SQL;

import static com.ivanceras.keyword.sql.SQLStatics.*;
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
		when(jdbc.getModelMetaDataDefinition()).thenReturn(new SampleModelMetaData());
		
		DAO_Product daoProduct = new DAO_Product();
		daoProduct.setName("iphone3gs");
		daoProduct.setDescription("iphone by Apple");
		
		ModelDef model = em.getDefinition(DAO_Product.class);

		System.out.println(model);
		
		
		
		em.insert(daoProduct);
		jdbc.insert(daoProduct, null, model, null);
		
	}
	
	@Test
	public void test3() throws DatabaseException{
		EntityManager em = EMF.get();
		String userNameOrEmail = "ivanceras";
		//use import static com.ivanceras.fluent.sql.SQL.Statics.*;
		SQL sql = SELECT(users.username, users.user_id, users.email)
					.FROM(Table.users)
					.WHERE(users.username).EQUAL(userNameOrEmail)
					.OR(users.email).EQUAL(userNameOrEmail);
		
		DAO_Users[] users = em.execute(sql);
		DAO_Users user0 = users[0];
		
		System.out.println(user0);
		
	}
}
