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
		sw.lnprint("");
		sw.lnprint("import "+conf2.daopackageName+".*;");
		sw.lnprint("import "+DAO.class.getCanonicalName()+";");
		sw.lnprint("import "+DAOFactory.class.getCanonicalName()+";");
		sw.lnprint("import "+DAOInstanceFactoryException.class.getCanonicalName()+";");
		sw.lnprint("");
		sw.lnprint("public class "+fileClassName+" implements "+DAOFactory.class.getSimpleName()+"{");
		sw.lnprint("");
		sw.lnprint("\t@Override");
		sw.lnprint("\t@SuppressWarnings(\"unchecked\")");
		sw.lnprint("\tpublic <T> T getInstance(Class<? extends "+DAO.class.getSimpleName()+"> daoClass) throws "+DAOInstanceFactoryException.class.getSimpleName()+"{");
		boolean doElse = false;
		for(ModelDef model : modelList){
			String className = CStringUtils.capitalize(model.getModelName(), useCamelCase);
			if(doElse){sw.lnprint("\t\telse ");}else{doElse = true;}
			sw.lnprint("\t\tif(daoClass.equals(DAO_"+className+".class)){");
			sw.lnprint("\t\t\treturn (T)new DAO_"+className+"();");
			sw.lnprint("\t\t}");
		}
		sw.lnprint("\t\telse{");
		sw.lnprint("\t\t\tthrow new "+DAOInstanceFactoryException.class.getSimpleName()+"(\"No class for \"+daoClass+\"]\");");
		sw.lnprint("\t\t}");
		sw.lnprint("\t}");
		//The array instances
		sw.lnprint("\t@Override");
		sw.lnprint("\t@SuppressWarnings(\"unchecked\")");
		sw.lnprint("\tpublic <T> T[] getArrayInstance(Class<? extends DAO> daoClass, int n)  throws "+DAOInstanceFactoryException.class.getSimpleName()+"{");
		boolean doElse2 = false;
		for(ModelDef model : modelList){
			String className = CStringUtils.capitalize(model.getModelName(), useCamelCase);
			if(doElse2){sw.println("\t\telse ");}else{doElse2 = true;}
			sw.lnprint("\t\tif(daoClass.equals(DAO_"+className+".class)){");
			sw.lnprint("\t\t\treturn (T[])new DAO_"+className+"[n];");
			sw.lnprint("\t\t}");
		}
		sw.lnprint("\t\telse{");
		sw.lnprint("\t\t\tthrow new "+DAOInstanceFactoryException.class.getSimpleName()+"(\"No class for \"+daoClass+\"]\");");
		sw.lnprint("\t\t}");
		sw.lnprint("\t}");
		sw.lnprint("\t");
		sw.lnprint("}");
		FileUtil.writeToFile(sw.toString(), dir, fileName);
	}

	
}
