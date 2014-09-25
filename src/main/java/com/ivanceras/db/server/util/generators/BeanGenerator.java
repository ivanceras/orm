package com.ivanceras.db.server.util.generators;

import static com.ivanceras.db.server.util.DAOGenerator.Array;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.ivanceras.commons.conf.Configuration;
import com.ivanceras.commons.strings.CStringUtils;
import com.ivanceras.commons.writer.FileUtil;
import com.ivanceras.commons.writer.SourceWriter;
import com.ivanceras.commons.writer.StringSourceWriter;
import com.ivanceras.db.api.IDatabase;
import com.ivanceras.db.api.ModelDef;
import com.ivanceras.db.shared.datatype.DataTypeGeneric;
import com.ivanceras.db.shared.datatype.DataTypeJava;
import com.ivanceras.db.shared.util.SpecialCase;

public class BeanGenerator {


	public boolean start(ModelDef[] modeldef, Configuration conf){
		for(ModelDef model : modeldef){
			generate(model, conf);
		}
		generateAllBeansClasses(modeldef, conf);
		return true;
	}

	public boolean generateAllBeansClasses(ModelDef[] modeldef, Configuration conf){
		String directory = conf.bodirectory;
		String packageName = conf.bopackageName; 
		
		SourceWriter sw = new StringSourceWriter();
		
		boolean useCamelCase = conf.useCamelCase;
		String className = CStringUtils.capitalize("AllBeans", useCamelCase);
		sw.print("package "+packageName+";");
		sw.lnprint("");
		sw.lnprint("/***");
		sw.lnprint("* This is automatically generated by "+this.getClass().getCanonicalName()+", based on the database table schema");
		sw.lnprint("* ");
		sw.lnprint("* ");
		sw.lnprint("*/");
		sw.lnprint("public class "+className+"{");
		sw.lnprint("");
		sw.lnprint("");
		sw.lnprint("\tpublic static Class<?>[] allClasses(){");
		sw.lnprint("\t\treturn new Class[]{");
		boolean doComma = false;
		for(ModelDef model : modeldef){
			String beanName = CStringUtils.capitalize(model.getModelName(), useCamelCase);
			if(doComma){sw.print(",");}else{doComma=true;}
			sw.lnprint("\t\t\t"+beanName+".class");
		}
		sw.lnprint("\t\t};");
		sw.lnprint("\t}");
		sw.lnprint("");
		sw.lnprint("}");
		FileUtil.writeToFile(sw.toString(), directory, ""+className+".java");
		return true;
	}
	
	public boolean generate(ModelDef modeldef, Configuration conf){
		String directory = conf.bodirectory;
		String packageName = conf.bopackageName; 
		
		SourceWriter sw = new StringSourceWriter();
		
		boolean useCamelCase = conf.useCamelCase;
		String className = CStringUtils.capitalize(modeldef.getModelName(), useCamelCase);
		sw.print("package "+packageName+";");
		sw.lnprint("");
		sw.lnprint("/***");
		sw.lnprint("* This is automatically generated by "+BeanGenerator.class.getCanonicalName()+", based on the database table schema");
		sw.lnprint("* ");
		sw.lnprint("* ");
		sw.lnprint("*/");
		sw.lnprint("import java.io.Serializable;");
		sw.lnprint("");
		sw.lnprint("public class "+className+" implements Serializable{");
		sw.lnprint("/**");
		sw.lnprint(" *"); 
		sw.lnprint(" */");
		sw.lnprint("\tprivate static final long serialVersionUID = "+UUID.randomUUID().getLeastSignificantBits()+"L;");
		sw.lnprint("");

		String[] attributes = modeldef.getAttributes();
		String[] dataTypes = DataTypeJava.fromGenericDataType(modeldef.getDataTypes());
		if(modeldef.getSubClass() != null && modeldef.getSubClass().length > 0){
			String[] subclassAttributes = {IDatabase.SUBCLASSTABLE};
			attributes = CStringUtils.mergeString(attributes, subclassAttributes);
			String[] subclassDatatypes = {DataTypeGeneric.STRING};
			dataTypes = CStringUtils.mergeString(dataTypes, subclassDatatypes);
		}
		String[] hasOne = modeldef.getHasOne();
		String[] distinctHasOne = getDistinctString(hasOne);
		String[] hasMany = modeldef.getHasMany();

		for(int i = 0; i < attributes.length; i++){
			String att = SpecialCase.getEquiv(attributes[i]);
			att = CStringUtils.toVariableName(att.toLowerCase(), useCamelCase);
			String datatype = dataTypes[i];
			sw.lnprint("\tprivate "+datatype+" "+att+";");
		}
		for(int i = 0; i < distinctHasOne.length; i++){
			String distinctHasOneStr = CStringUtils.capitalize(distinctHasOne[i].toLowerCase(), useCamelCase);
			String distinctHasOneVar = CStringUtils.toVariableName(distinctHasOneStr, useCamelCase);
			sw.lnprint("");
			sw.lnprint("\tprivate "+distinctHasOneStr+" "+distinctHasOneVar+";");
		}
		for(int i = 0; i < hasMany.length; i++){
			String hasManyStr = CStringUtils.capitalize(hasMany[i].toLowerCase(), useCamelCase);
			String hasManyVar = CStringUtils.toVariableName(hasManyStr, useCamelCase);
			sw.lnprint("");
			sw.lnprint("\tprivate "+hasManyStr+"[] "+hasManyVar+Array+";");
		}


		sw.lnprint("");
		sw.lnprint("");
		sw.lnprint("\tpublic "+className+"(){");
		sw.lnprint("\t\t");
		sw.lnprint("\t}");
		sw.lnprint("");
		for(int i = 0; i < attributes.length; i++){
			String att = SpecialCase.getEquiv(attributes[i]);
			att = CStringUtils.capitalize(att.toLowerCase(), useCamelCase);
			String attVar = CStringUtils.toVariableName(att, useCamelCase); 
			String datatype = dataTypes[i];
			sw.lnprint("\tpublic "+datatype+" get"+att+"(){");
			sw.lnprint("\t\treturn "+attVar+";");
			sw.lnprint("\t}");
			sw.lnprint("");
			sw.lnprint("\tpublic void set"+att+"("+datatype+" "+attVar+"){");
			sw.lnprint("\t\tthis."+attVar+" = "+attVar+";");
			sw.lnprint("\t}");
			sw.lnprint("");
		}

		for(int i = 0; i < distinctHasOne.length; i++){
			String distinctHasOneStr = CStringUtils.capitalize(distinctHasOne[i].toLowerCase(), useCamelCase);
			String distinctHasOneVar = CStringUtils.toVariableName(distinctHasOneStr, useCamelCase);
			sw.lnprint("");
			sw.lnprint("\tpublic void set"+distinctHasOneStr+"("+distinctHasOneStr+" "+distinctHasOneVar+"){");
			sw.lnprint("\t\tthis."+distinctHasOneVar+" = "+distinctHasOneVar+";");
			sw.lnprint("\t}");
			sw.lnprint("");
			sw.lnprint("\tpublic "+distinctHasOneStr+" get"+distinctHasOneStr+"(){");
			sw.lnprint("\t\treturn "+distinctHasOneVar+";");
			sw.lnprint("\t}");
		}


		for(int i = 0; i < hasMany.length; i++){
			String hasManyStr = CStringUtils.capitalize(hasMany[i].toLowerCase(), useCamelCase);
			String hasManyVar = CStringUtils.toVariableName(hasManyStr, useCamelCase);
			sw.lnprint("");
			sw.lnprint("\tpublic void set"+hasManyStr+Array+"("+hasManyStr+"[] "+hasManyVar+Array+"){");
			sw.lnprint("\t\tthis."+hasManyVar+Array+" = "+hasManyVar+Array+";");
			sw.lnprint("\t}");
			sw.lnprint("");
			sw.lnprint("\tpublic "+hasManyStr+"[] get"+hasManyStr+Array+"(){");
			sw.lnprint("\t\treturn "+hasManyVar+Array+";");
			sw.lnprint("\t}");
		}
		//Override the toString
		sw.lnprint("\t@Override");
		sw.lnprint("\tpublic String toString(){");
		sw.lnprint("\t\tStringBuilder sb = new StringBuilder();");
		sw.lnprint("\t\tsb.append("+className+".class+\"->[\");");
		for(int i = 0; i < attributes.length; i++){
			String att = SpecialCase.getEquiv(attributes[i]);
			att = CStringUtils.capitalize(att.toLowerCase(), useCamelCase);
			String attVar = CStringUtils.toVariableName(att, useCamelCase); 
			sw.lnprint("\t\tsb.append("+attVar+"!=null ? "+attVar+"+\", \":\"\");");
		}
		sw.lnprint("\t\tsb.append(\"]\");");
		sw.lnprint("\t\treturn sb.toString();");
		sw.lnprint("\t}");
		sw.lnprint("}");
		FileUtil.writeToFile(sw.toString(), directory, ""+className+".java");
		return true;
	}

	public static String[] getDistinctString(String[] hasOne) {
		List<String> distinctString = new ArrayList<String>();
		for(String h : hasOne){
			if(distinctString.contains(h)){
				;
			}else{
				distinctString.add(h);
			}
		}
		return distinctString.toArray(new String[distinctString.size()]);
	}
}
