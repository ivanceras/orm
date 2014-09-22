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
		sw.lnprint("package "+conf.metaDataPackageName+";");
		sw.lnprint();
		sw.lnprint("import java.util.Map;");
		sw.lnprint("import java.util.LinkedHashMap;");
		sw.lnprint("import "+DAO.class.getCanonicalName()+";");
		sw.lnprint("import "+conf.daopackageName+".*;");
		sw.lnprint("");
		sw.lnprint("import "+com.ivanceras.db.api.ModelDef.class.getCanonicalName()+";");
		sw.lnprint("");
		sw.lnprint("public class "+conf.metaDataClassName+" extends "+com.ivanceras.db.model.ModelMetaData.class.getCanonicalName()+"{");
		sw.lnprint("");
		sw.lnprint("");
		sw.lnprint("\tpublic static Map<Class<? extends DAO>, ModelDef> modelDefList = new LinkedHashMap<Class<? extends DAO>, ModelDef>();");
		sw.lnprint("\tpublic static Map<String, ModelDef> namedModelDefList = new LinkedHashMap<String, ModelDef>();");
		sw.lnprint("\tpublic static Map<Class<? extends DAO>, String> modelNameListNdx = new LinkedHashMap<Class<? extends DAO>, String>();");
		sw.lnprint("\tpublic static Map<String, Class<? extends DAO>> classListNdx = new LinkedHashMap<String, Class<? extends DAO>>();");
		sw.lnprint("");
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
			sw.lnprint("");
			sw.lnprint("\tvoid init"+i+"(){");
			sw.lnprint("");
			sw.lnprint("\t\tString   namespace                               = \""+modeldef.getNamespace()+"\";");
			sw.lnprint("\t\tString   modelName                               = \""+className+"\";");
			sw.lnprint("\t\tString[] attributes                              = "+getStringListRepresentation(modeldef.getAttributes())+";");
			sw.lnprint("\t\tString   generatedAttribute                      = "+(modeldef.getGeneratedAttribute()!=null?("\""+modeldef.getGeneratedAttribute()+"\""):null)+";");
			sw.lnprint("\t\tString[] primaryAttributes                       = "+getStringListRepresentation(modeldef.getPrimaryAttributes())+";");
			sw.lnprint("\t\tString[] uniqueAttributes                        = "+getStringListRepresentation(modeldef.getUniqueAttributes())+";");
			sw.lnprint("\t\tString[] dataTypes                               = "+getStringListRepresentation(modeldef.getDataTypes())+";");
			//			buff.lnprint("\t\tString[] attributeComments                       = "+getStringListRepresentation(modeldef.getAttributeComments())+";");
			sw.lnprint("");
			sw.lnprint("\t\tString[] hasOneLocalColumn                       = "+getStringListRepresentation(modeldef.getHasOneLocalColumn())+";");
			sw.lnprint("\t\tString[] hasOne                                  = "+getStringListRepresentation(modeldef.getHasOne())+";");
			sw.lnprint("\t\tString[] hasOneReferencedColumn                  = "+getStringListRepresentation(modeldef.getHasOneReferencedColumn())+";");
			sw.lnprint("");

			sw.lnprint("\t\tString[] hasMany                                 = "+getStringListRepresentation(modeldef.getHasMany())+";");
			sw.lnprint("\t\tString[] hasManyReferencedColumn                 = "+getStringListRepresentation(modeldef.getHasManyReferencedColumn())+";");
			sw.lnprint("\t\tString[] hasManyLocalColumn                      = "+getStringListRepresentation(modeldef.getHasManyLocalColumn())+";");
			sw.lnprint("");
			if(modeldef.getOwners() != null){
				sw.lnprint("\t\tString[] owners                                  = "+getStringListRepresentation(modeldef.getOwners())+";");
			}
			if(modeldef.getPrimaryOwner() != null){
				sw.lnprint("\t\tString primaryOwner                              =  \""+modeldef.getPrimaryOwner()+"\";");
			}
			if(modeldef.getDirectChildren() != null){
				sw.lnprint("\t\tString[] directChildren                          = "+getStringListRepresentation(modeldef.getDirectChildren())+";");
			}
			if(modeldef.getParentClass() != null){
				sw.lnprint("\t\tString parentClass                         		 = \""+modeldef.getParentClass()+"\";");
			}
			if(modeldef.getSubClass() != null && modeldef.getSubClass().length > 0){
				sw.lnprint("\t\tString[] subClasses								 = "+getStringListRepresentation(modeldef.getSubClass())+";");
			}
			sw.lnprint("");

			sw.lnprint("\t\tboolean  caseSensitive								= "+modeldef.isCaseSensitive()+";");
			sw.lnprint("\t\t");

			sw.lnprint("\t\tModelDef modelDef = new ModelDef(");
			sw.lnprint("\t\t\tnamespace, ");
			sw.lnprint("\t\t\tmodelName, ");
			sw.lnprint("\t\t\tattributes,");
			sw.lnprint("\t\t\tgeneratedAttribute,");
			sw.lnprint("\t\t\tdataTypes, ");
			sw.lnprint("\t\t\tprimaryAttributes, ");
			sw.lnprint("\t\t\tuniqueAttributes, ");
			sw.lnprint("\t\t\thasOne, ");
			sw.lnprint("\t\t\thasOneLocalColumn, ");
			sw.lnprint("\t\t\thasOneReferencedColumn, ");
			sw.lnprint("\t\t\thasMany, ");
			sw.lnprint("\t\t\thasManyReferencedColumn, ");
			sw.lnprint("\t\t\thasManyLocalColumn, ");
			sw.lnprint("\t\t\tcaseSensitive);");
			sw.lnprint("\t\t\t\t");
			sw.lnprint("\t\tmodelDef.setPolymorphic("+modeldef.isPolymorphic()+");");
			sw.lnprint("\t\tmodelDef.setTableName(\""+modeldef.getTableName()+"\");");
			sw.lnprint("\t\tmodelDef.setDescription(\""+modeldef.getDescription()+"\");");
			//			buff.lnprint("\t\tmodelDef.setAttributeComments(attributeComments);");
			if(modeldef.getOwners() != null){
				sw.lnprint("\t\tmodelDef.setOwners(owners);");
			}
			if(modeldef.getPrimaryOwner() != null){
				sw.lnprint("\t\tmodelDef.setPrimaryOwner(primaryOwner);");
			}
			if(modeldef.getDirectChildren() != null){
				sw.lnprint("\t\tmodelDef.setDirectChildren(directChildren);");
			}
			if(modeldef.getParentClass() != null){
				sw.lnprint("\t\tmodelDef.setParentClass(parentClass);");
			}
			if(modeldef.getSubClass() != null && modeldef.getSubClass().length > 0){
				sw.lnprint("\t\tmodelDef.setSubClass(subClasses);");
			}
			sw.lnprint("\t\tmodelDefList.put(DAO_"+className+".class, modelDef);");
			sw.lnprint("\t\tnamedModelDefList.put(\""+className+"\", modelDef);");
			sw.lnprint("\t\tmodelNameListNdx.put(DAO_"+className+".class, \""+className+"\");");
			sw.lnprint("\t\tclassListNdx.put(\""+className+"\", DAO_"+className+".class);");
			sw.lnprint("");
			sw.lnprint("");
			sw.lnprint("\t}");
		}
		sw.lnprint("");
		sw.lnprint("");
		sw.lnprint("\tpublic "+conf.metaDataClassName+"(){");
		sw.lnprint("\t\tsuper(modelDefList, namedModelDefList, modelNameListNdx, classListNdx);");
		for(int i = 0; i < modeldefList.length; i++){
			sw.lnprint("\t\tinit"+i+"();");
		}
		sw.lnprint("\t}");
		sw.lnprint("}");
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
