package com.ivanceras.db.server.util.generators;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ivanceras.commons.conf.Configuration;
import com.ivanceras.commons.strings.CStringUtils;
import com.ivanceras.commons.writer.FileUtil;
import com.ivanceras.commons.writer.SourceWriter;
import com.ivanceras.commons.writer.StringSourceWriter;
import com.ivanceras.db.api.ModelDef;
import com.ivanceras.db.shared.DAO;

public class ModelFactoryGenerator {

	public void start(ModelDef[] modelListChilded, Configuration conf) {
		generateModelMetaData(modelListChilded, conf);
	}

	private boolean generateModelMetaData(ModelDef[] modeldefList, Configuration conf) {

		Set<String> columnSet = new HashSet<String>();
		Map<String, Set<String>> usedInTable = new HashMap<String, Set<String>>();
		Set<String> uniquetableSet = new HashSet<String>();
		boolean useCamelCase = conf.useCamelCase;

		SourceWriter sw = new StringSourceWriter();
		sw.println("package "+conf.metaDataPackageName+";");
		sw.println();
		sw.println("import java.util.Map;");
		sw.println("import java.util.LinkedHashMap;");
		sw.println("import "+DAO.class.getCanonicalName()+";");
		sw.println("import "+conf.daopackageName+".*;");
		sw.println("");
		sw.println("import "+com.ivanceras.db.api.ModelDef.class.getCanonicalName()+";");
		sw.println("");
		sw.println("public class "+conf.metaDataClassName+" extends "+com.ivanceras.db.model.ModelMetaData.class.getCanonicalName()+"{");
		sw.println("");
		sw.println("");
		sw.println("\tpublic static Map<Class<? extends DAO>, ModelDef> modelDefList = new LinkedHashMap<Class<? extends DAO>, ModelDef>();");
		sw.println("\tpublic static Map<String, ModelDef> namedModelDefList = new LinkedHashMap<String, ModelDef>();");
		sw.println("\tpublic static Map<Class<? extends DAO>, String> modelNameListNdx = new LinkedHashMap<Class<? extends DAO>, String>();");
		sw.println("\tpublic static Map<String, Class<? extends DAO>> classListNdx = new LinkedHashMap<String, Class<? extends DAO>>();");
		sw.println("");
		for(int i = 0; i < modeldefList.length; i++){
			ModelDef modeldef = modeldefList[i];
			String className = CStringUtils.capitalize(modeldef.getModelName(), useCamelCase);
			uniquetableSet.add(modeldef.getTableName());
			List<String> attrList = Arrays.asList(modeldef.getAttributes());
			for(String att : attrList){
				columnSet.add(att);
				if(usedInTable.containsKey(att)){
					usedInTable.get(att).add(modeldef.getModelName());
				}
				else{
					HashSet<String> tableSet = new HashSet<String>();
					tableSet.add(modeldef.getModelName());
					usedInTable.put(att, tableSet);
				}
			}
			sw.println("");
			sw.println("\tvoid init"+i+"(){");
			sw.println("");
			sw.println("\t\tString   namespace                               = \""+modeldef.getNamespace()+"\";");
			sw.println("\t\tString   modelName                               = \""+className+"\";");
			sw.println("\t\tString[] attributes                              = "+getStringListRepresentation(modeldef.getAttributes())+";");
			sw.println("\t\tString   generatedAttribute                      = "+(modeldef.getGeneratedAttribute()!=null?("\""+modeldef.getGeneratedAttribute()+"\""):null)+";");
			sw.println("\t\tString[] primaryAttributes                       = "+getStringListRepresentation(modeldef.getPrimaryAttributes())+";");
			sw.println("\t\tString[] uniqueAttributes                        = "+getStringListRepresentation(modeldef.getUniqueAttributes())+";");
			sw.println("\t\tString[] dataTypes                               = "+getStringListRepresentation(modeldef.getDataTypes())+";");
			//			buff.println("\t\tString[] attributeComments                       = "+getStringListRepresentation(modeldef.getAttributeComments())+";");
			sw.println("");
			sw.println("\t\tString[] hasOneLocalColumn                       = "+getStringListRepresentation(modeldef.getHasOneLocalColumn())+";");
			sw.println("\t\tString[] hasOne                                  = "+getStringListRepresentation(modeldef.getHasOne())+";");
			sw.println("\t\tString[] hasOneReferencedColumn                  = "+getStringListRepresentation(modeldef.getHasOneReferencedColumn())+";");
			sw.println("");

			sw.println("\t\tString[] hasMany                                 = "+getStringListRepresentation(modeldef.getHasMany())+";");
			sw.println("\t\tString[] hasManyReferencedColumn                 = "+getStringListRepresentation(modeldef.getHasManyReferencedColumn())+";");
			sw.println("\t\tString[] hasManyLocalColumn                      = "+getStringListRepresentation(modeldef.getHasManyLocalColumn())+";");
			sw.println("");
			if(modeldef.getOwners() != null){
				sw.println("\t\tString[] owners                                  = "+getStringListRepresentation(modeldef.getOwners())+";");
			}
			if(modeldef.getPrimaryOwner() != null){
				sw.println("\t\tString primaryOwner                              =  \""+modeldef.getPrimaryOwner()+"\";");
			}
			if(modeldef.getDirectChildren() != null){
				sw.println("\t\tString[] directChildren                          = "+getStringListRepresentation(modeldef.getDirectChildren())+";");
			}
			if(modeldef.getParentClass() != null){
				sw.println("\t\tString parentClass                         		 = \""+modeldef.getParentClass()+"\";");
			}
			if(modeldef.getSubClass() != null && modeldef.getSubClass().length > 0){
				sw.println("\t\tString[] subClasses								 = "+getStringListRepresentation(modeldef.getSubClass())+";");
			}
			sw.println("");

			sw.println("\t\tboolean  caseSensitive								= "+modeldef.isCaseSensitive()+";");
			sw.println("\t\t");

			sw.println("\t\tModelDef modelDef = new ModelDef(");
			sw.println("\t\t\tnamespace, ");
			sw.println("\t\t\tmodelName, ");
			sw.println("\t\t\tattributes,");
			sw.println("\t\t\tgeneratedAttribute,");
			sw.println("\t\t\tdataTypes, ");
			sw.println("\t\t\tprimaryAttributes, ");
			sw.println("\t\t\tuniqueAttributes, ");
			sw.println("\t\t\thasOne, ");
			sw.println("\t\t\thasOneLocalColumn, ");
			sw.println("\t\t\thasOneReferencedColumn, ");
			sw.println("\t\t\thasMany, ");
			sw.println("\t\t\thasManyReferencedColumn, ");
			sw.println("\t\t\thasManyLocalColumn, ");
			sw.println("\t\t\tcaseSensitive);");
			sw.println("\t\t\t\t");
			sw.println("\t\tmodelDef.setPolymorphic("+modeldef.isPolymorphic()+");");
			sw.println("\t\tmodelDef.setTableName(\""+modeldef.getTableName()+"\");");
			sw.println("\t\tmodelDef.setDescription(\""+modeldef.getDescription()+"\");");
			//			buff.println("\t\tmodelDef.setAttributeComments(attributeComments);");
			if(modeldef.getOwners() != null){
				sw.println("\t\tmodelDef.setOwners(owners);");
			}
			if(modeldef.getPrimaryOwner() != null){
				sw.println("\t\tmodelDef.setPrimaryOwner(primaryOwner);");
			}
			if(modeldef.getDirectChildren() != null){
				sw.println("\t\tmodelDef.setDirectChildren(directChildren);");
			}
			if(modeldef.getParentClass() != null){
				sw.println("\t\tmodelDef.setParentClass(parentClass);");
			}
			if(modeldef.getSubClass() != null && modeldef.getSubClass().length > 0){
				sw.println("\t\tmodelDef.setSubClass(subClasses);");
			}
			sw.println("\t\tmodelDefList.put(DAO_"+className+".class, modelDef);");
			sw.println("\t\tnamedModelDefList.put(\""+className+"\", modelDef);");
			sw.println("\t\tmodelNameListNdx.put(DAO_"+className+".class, \""+className+"\");");
			sw.println("\t\tclassListNdx.put(\""+className+"\", DAO_"+className+".class);");
			sw.println("");
			sw.println("");
			sw.println("\t}");
		}
		sw.println("");
		sw.println("");
		sw.println("\tpublic "+conf.metaDataClassName+"(){");
		sw.println("\t\tsuper(modelDefList, namedModelDefList, modelNameListNdx, classListNdx);");
		for(int i = 0; i < modeldefList.length; i++){
			sw.println("\t\tinit"+i+"();");
		}
		sw.println("\t}");
		sw.println("}");
		//		System.out.println("There are "+columnSet.size()+" unique column names...");
		FileUtil.writeToFile(sw.toString(), conf.metaDataDirectory, conf.metaDataClassName+".java");
		return true;
	}

	private String getStringListRepresentation(String[] list){
		if(list == null){
			return null;
		}
		StringBuffer buff = new StringBuffer();
		buff.append("{");
		boolean doComma = false;
		for(String l : list){
			if(doComma){buff.append(", ");}else{doComma=true;}
			buff.append("\""+l+"\"");
		}
		buff.append("}");
		return buff.toString();
	}
}
