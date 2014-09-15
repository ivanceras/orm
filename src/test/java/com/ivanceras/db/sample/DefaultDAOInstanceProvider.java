package com.ivanceras.db.sample;
import com.ivanceras.db.shared.DAO;
import com.ivanceras.db.api.DAOFactory;
import com.ivanceras.db.shared.exception.DAOInstanceFactoryException;

public class DefaultDAOInstanceProvider implements DAOFactory{

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getInstance(Class<? extends DAO> daoClass) throws DAOInstanceFactoryException{
		if(daoClass.equals(DAO_Product.class)){
			return (T)new DAO_Product();
		}
		else if(daoClass.equals(DAO_ProductAvailability.class)){
			return (T)new DAO_ProductAvailability();
		}
		else if(daoClass.equals(DAO_ProductCategory.class)){
			return (T)new DAO_ProductCategory();
		}
		else{
			throw new DAOInstanceFactoryException("No class for "+daoClass+"]");
		}
	}
	@Override
	@SuppressWarnings("unchecked")
	public <T> T[] getArrayInstance(Class<? extends DAO> daoClass, int n)  throws DAOInstanceFactoryException{
		if(daoClass.equals(DAO_Category.class)){
			return (T[])new DAO_Category[n];
		}
		else if(daoClass.equals(DAO_Product.class)){
			return (T[])new DAO_Product[n];
		}
		else if(daoClass.equals(DAO_ProductAvailability.class)){
			return (T[])new DAO_ProductAvailability[n];
		}
		else if(daoClass.equals(DAO_ProductCategory.class)){
			return (T[])new DAO_ProductCategory[n];
		}
		else{
			throw new DAOInstanceFactoryException("No class for "+daoClass+"]");
		}
	}
	
}
