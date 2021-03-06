package com.ivanceras.db.server.util.generators;

import com.ivanceras.commons.conf.Configuration;
import com.ivanceras.commons.strings.CStringUtils;
import com.ivanceras.commons.writer.FileUtil;
import com.ivanceras.commons.writer.SourceWriter;
import com.ivanceras.commons.writer.StringSourceWriter;
import com.ivanceras.db.api.ModelDef;
import com.ivanceras.db.shared.DAO;
import com.ivanceras.db.shared.exception.DAOInstanceFactoryException;

/**
 * Converts a Model to DAO using the ModelMapper
 * @author lee
 *
 */
public class ModelToDAOConversionGenerator {

	public boolean start(ModelDef[] modeldef, Configuration conf){
		generate(modeldef, conf);
		return true;
	}


	public boolean generate(ModelDef[] modelList, Configuration conf){
		String directory = conf.metaDataDirectory;
		String packageName = conf.metaDataPackageName;


		SourceWriter sw = new StringSourceWriter();

		boolean useCamelCase = conf.useCamelCase;
		String className =  "ModelDAOConverter";
		String fileName = className+".java";
		sw.print("package "+packageName+";");
		sw.lnprint("");
		sw.lnprint("/***");
		sw.lnprint("* This is automatically generated by "+this.getClass().getCanonicalName()+", based on the database table schema");
		sw.lnprint("* ");
		sw.lnprint("* ");
		sw.lnprint("*/");

		sw.lnprint("import "+DAO.class.getCanonicalName()+";");
		sw.lnprint("import "+DAOInstanceFactoryException.class.getCanonicalName()+";");
		sw.lnprint("import "+conf.mapperpackageName+".*;");
		sw.lnprint("import "+conf.bopackageName+".*;");
		sw.lnprint("");
		sw.lnprint("public class "+className+"{");
		sw.lnTabPrint("/**");
		sw.lnTabPrint("*"); 
		sw.lnTabPrint("*"); 
		sw.lnTabPrint("*/");
		sw.lnTabPrint("@SuppressWarnings(\"unchecked\")"); 
		sw.lnTabPrint("public <D extends DAO, M> D convert(M obj) throws "+DAOInstanceFactoryException.class.getSimpleName()+"{");
		sw.lnTabPrint("    if(obj == null){return null;}"); 
		boolean doElse = false;
		for(ModelDef model : modelList){
			String modelClassName = CStringUtils.capitalize(model.getModelName(), useCamelCase);
			if(doElse){sw.lnTabPrint("    else ");}else{doElse = true;}
			sw.lnTabPrint("    if(obj.getClass().equals("+modelClassName+".class)){"); 
			sw.lnTabPrint("        return (D)"+modelClassName+"Mapper.map(("+modelClassName+")obj);"); 
			sw.lnTabPrint("    }"); 
		}
		sw.lnTabPrint("    else{");
		sw.lnTabPrint("        throw new "+DAOInstanceFactoryException.class.getSimpleName()+"(\"No coversion mapper class for \"+obj.getClass()+\"]\");");
		sw.lnTabPrint("    }");
		sw.lnTabPrint("}"); 
		sw.lnprint("}"); 
		FileUtil.writeToFile(sw.toString(), directory, fileName);
		return true;
	}

}
