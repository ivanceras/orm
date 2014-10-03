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
		sw.lnTabPrint("@Override");
		sw.lnTabPrint("@SuppressWarnings(\"unchecked\")");
		sw.lnTabPrint("public <T> T getInstance(Class<? extends "+DAO.class.getSimpleName()+"> daoClass) throws "+DAOInstanceFactoryException.class.getSimpleName()+"{");
		boolean doElse = false;
		for(ModelDef model : modelList){
			String className = CStringUtils.capitalize(model.getModelName(), useCamelCase);
			if(doElse){sw.lnTabPrint("    else ");}else{doElse = true;}
			sw.lnTabPrint("    if(daoClass.equals(DAO_"+className+".class)){");
			sw.lnTabPrint("        return (T)new DAO_"+className+"();");
			sw.lnTabPrint("    }");
		}
		sw.lnTabPrint("    else{");
		sw.lnTabPrint("        throw new "+DAOInstanceFactoryException.class.getSimpleName()+"(\"No class for \"+daoClass+\"]\");");
		sw.lnTabPrint("    }");
		sw.lnTabPrint("}");
		//The array instances
		sw.lnTabPrint("@Override");
		sw.lnTabPrint("@SuppressWarnings(\"unchecked\")");
		sw.lnTabPrint("public <T> T[] getArrayInstance(Class<? extends DAO> daoClass, int n)  throws "+DAOInstanceFactoryException.class.getSimpleName()+"{");
		boolean doElse2 = false;
		for(ModelDef model : modelList){
			String className = CStringUtils.capitalize(model.getModelName(), useCamelCase);
			if(doElse2){sw.println("        else ");}else{doElse2 = true;}
			sw.lnTabPrint("    if(daoClass.equals(DAO_"+className+".class)){");
			sw.lnTabPrint("        return (T[])new DAO_"+className+"[n];");
			sw.lnTabPrint("    }");
		}
		sw.lnTabPrint("    else{");
		sw.lnTabPrint("        throw new "+DAOInstanceFactoryException.class.getSimpleName()+"(\"No class for \"+daoClass+\"]\");");
		sw.lnTabPrint("    }");
		sw.lnTabPrint("}");
		sw.lnTabPrint("");
		sw.lnprint("}");
		FileUtil.writeToFile(sw.toString(), dir, fileName);
	}

	
}
