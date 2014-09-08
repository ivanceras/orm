package com.ivanceras.db.api;

import com.ivanceras.db.server.util.DAOGenerator;
import com.ivanceras.db.shared.DAO;
import com.ivanceras.db.shared.exception.DAOInstanceFactoryException;

/**
 * Will be inherited from a generated class from {@link DAOGenerator#generateModelMetaData}This is only useful for GWT classes, since GWT doesn't support reflection, we will have to make a way to create
 * an instance of the classes.
 * @author lee
 *
 */
public interface DAOFactory {
	
	public <T> T getInstance(Class<? extends DAO> daoClass) throws DAOInstanceFactoryException;

	public <T> T[] getArrayInstance(Class<? extends DAO> daoClass, int n) throws DAOInstanceFactoryException;

}
