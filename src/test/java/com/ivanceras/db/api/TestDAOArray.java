package com.ivanceras.db.api;

import org.junit.Test;

import com.ivanceras.db.shared.DAO;
import com.ivanceras.db.shared.DAOArray;
import com.ivanceras.db.shared.exception.DAOArrayException;

public class TestDAOArray {

	@Test
	public void test() {
		DAO dao1 = new DAO("User");
		dao1.set_Value("firstName", "Lee");
		dao1.set_Value("lastName", "Cesar");
		
		
		DAO dao2 = new DAO("User");
		dao2.set_Value("firstName", "Jov");
		dao2.set_Value("lastName", "Bano");
		
		
		
		DAOArray arr = new DAOArray();
		try {
			arr.add(dao1);
			arr.add(dao2);
		} catch (DAOArrayException e) {
			e.printStackTrace();
		}
	}

}
