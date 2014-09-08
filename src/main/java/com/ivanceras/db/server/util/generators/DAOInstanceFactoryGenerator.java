package com.ivanceras.db.server.util.generators;

import com.ivanceras.commons.conf.Configuration;
import com.ivanceras.commons.strings.CStringUtils;
import com.ivanceras.commons.writer.FileUtil;
import com.ivanceras.commons.writer.SourceWriter;
import com.ivanceras.commons.writer.StringSourceWriter;
import com.ivanceras.db.api.DAOFactory;
import com.ivanceras.db.api.ModelDef;
import com.ivanceras.db.shared.DAO;
import com.ivanceras.db.shared.exception.DAOInstanceFactoryException;

public class DAOInstanceFactoryGenerator {
	
	public void start(ModelDef[] modelListChilded, Configuration conf) {
		generateDAOInstanceFactoryClass(modelListChilded, conf);
		
	}
	
	private void generateDAOInstanceFactoryClass(ModelDef[] modelList,
			Configuration conf2) {
		String fileClassName = "DefaultDAOInstanceProvider";
		String fileName  = fileClassName+".java";
		String dir = conf2.metaDataDirectory;
		boolean useCamelCase = conf2.useCamelCase;

		SourceWriter sw = new StringSourceWriter();
		sw.print("package "+conf2.metaDataPackageName+";");
		sw.println("");
		sw.println("import "+conf2.daopackageName+".*;");
		sw.println("import "+DAO.class.getCanonicalName()+";");
		sw.println("import "+DAOFactory.class.getCanonicalName()+";");
		sw.println("import "+DAOInstanceFactoryException.class.getCanonicalName()+";");
		sw.println("");
		sw.println("public class "+fileClassName+" implements "+DAOFactory.class.getSimpleName()+"{");
		sw.println("");
		sw.println("\t@Override");
		sw.println("\t@SuppressWarnings(\"unchecked\")");
		sw.println("\tpublic <T> T getInstance(Class<? extends "+DAO.class.getSimpleName()+"> daoClass) throws "+DAOInstanceFactoryException.class.getSimpleName()+"{");
		boolean doElse = false;
		for(ModelDef model : modelList){
			String className = CStringUtils.capitalize(model.getModelName(), useCamelCase);
			sw.print("\t\t");
			if(doElse){sw.print("else ");}else{doElse = true;}
			sw.println("if(daoClass.equals(DAO_"+className+".class)){");
			sw.println("\t\t\treturn (T)new DAO_"+className+"();");
			sw.println("\t\t}");
		}
		sw.println("\t\telse{");
		sw.println("\t\t\tthrow new "+DAOInstanceFactoryException.class.getSimpleName()+"(\"No class for \"+daoClass+\"]\");");
		sw.println("\t\t}");
		sw.println("\t}");
		//The array instances
		sw.println("\t@Override");
		sw.println("\t@SuppressWarnings(\"unchecked\")");
		sw.println("\tpublic <T> T[] getArrayInstance(Class<? extends DAO> daoClass, int n)  throws "+DAOInstanceFactoryException.class.getSimpleName()+"{");
		boolean doElse2 = false;
		for(ModelDef model : modelList){
			String className = CStringUtils.capitalize(model.getModelName(), useCamelCase);
			sw.print("\t\t");
			if(doElse2){sw.print("else ");}else{doElse2 = true;}
			sw.println("if(daoClass.equals(DAO_"+className+".class)){");
			sw.println("\t\t\treturn (T[])new DAO_"+className+"[n];");
			sw.println("\t\t}");
		}
		sw.println("\t\telse{");
		sw.println("\t\t\tthrow new "+DAOInstanceFactoryException.class.getSimpleName()+"(\"No class for \"+daoClass+\"]\");");
		sw.println("\t\t}");
		sw.println("\t}");
		sw.println("\t");
		sw.println("}");
		FileUtil.writeToFile(sw.toString(), dir, fileName);
	}

	
}
