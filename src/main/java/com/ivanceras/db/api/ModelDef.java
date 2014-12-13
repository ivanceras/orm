package com.ivanceras.db.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.ivanceras.commons.strings.CStringUtils;

public class ModelDef implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7962506725506610169L;

	private String namespace;

	/**A more pleasant representation of this model*/
	private String modelName;

	/**The actual table name of this model when used in the database*/
	private String tableName;
	private String description; //additional info for this model, will become comments in the actual table
	private String[] attributes;
	private String generatedAttribute;
	private String[] primaryAttributes;
	private String[] uniqueAttributes;
	private String[] dataTypes;
	private String[] attributeComments; //comments of the each attribute, to additional info such as the default length, and data format

	private String[] hasOneLocalColumn; //local column of this model
	private String[] hasOne;
	private String[] hasOneReferencedColumn; // referred column from the referred table

	private String[] hasMany;
	private String[] hasManyReferencedColumn; //local column
	private String[] hasManyLocalColumn; //referred column from the referred table


	private String[] owners; //classes that points to this entity, that has a hasMany field pointing to this

	private String primaryOwner; //the primary owner of this entity, claims the entity in the entity group when stored, used as the parent kind

	private boolean caseSensitive = false;
	private boolean polymorphic;

	private String[] directChildren;
	private String parentClass;
	private String[] subClass;


	public ModelDef(
			String namespace, 
			String modelName, 
			String[] attributes, 
			String generatedAttribute,
			String[] dataTypes, 
			String[] primaryAttributes, 
			String[] uniqueAttributes, 
			String[] hasOne, 
			String[] hasOneLocalColumn, 
			String[] hasOneReferencedColumn,
			String[] hasMany, 
			String[] hasManyReferencedColumn, 
			String[] hasManyLocalColumn,
			boolean caseSensitive) {
		this.namespace = namespace;
		this.modelName = modelName;
		this.attributes = attributes;
		this.generatedAttribute = generatedAttribute;
		this.dataTypes = dataTypes;
		this.primaryAttributes = primaryAttributes;
		this.uniqueAttributes = uniqueAttributes;

		this.hasOne = hasOne;
		this.hasOneLocalColumn = hasOneLocalColumn;
		this.hasOneReferencedColumn = hasOneReferencedColumn;

		this.hasMany = hasMany;
		this.hasManyReferencedColumn = hasManyReferencedColumn;
		this.hasManyLocalColumn = hasManyLocalColumn;

		this.caseSensitive = caseSensitive;	
	}



	public ModelDef() {

	}
	
	public ModelDef(String modelName) {
		setModelName(modelName);
	}


	public String getNamespace() {
		return namespace;
	}

	public String getModelName() {
		return modelName;
	}

	public String[] getAttributes() {
		return attributes;
	}

	public String getGeneratedAttribute(){
		return generatedAttribute;
	}


	public String[] getDataTypes() {
		return dataTypes;
	}




	public String[] getHasMany() {
		return hasMany;
	}


	public String[] getHasManyLocalColumn() {
		return hasManyReferencedColumn;
	}


	public String[] getHasManyReferencedColumn(){
		return hasManyLocalColumn;
	}


	public String[] getPrimaryAttributes() {
		return primaryAttributes;
	}


	public String[] getUniqueAttributes() {
		return uniqueAttributes;
	}


	public String[] getHasOne() {
		return hasOne;
	}


	public String[] getHasOneLocalColumn() {
		return hasOneLocalColumn;
	}


	public String[] getHasOneReferencedColumn(){
		return hasOneReferencedColumn;
	}


	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}


	public void setModelName(String table) {
		this.modelName = table;
	}


	public void setAttributes(String[] columns) {
		this.attributes = columns;
	}


	public void setDataTypes(String[] dataTypes) {
		this.dataTypes = dataTypes;
	}


	public void setPrimaryAttributes(String[] primaryColumns) {
		this.primaryAttributes = primaryColumns;
	}


	public void setUniqueAttributes(String[] uniqueColumns) {
		this.uniqueAttributes = uniqueColumns;
	}


	public void setHasMany(String[] hasMany) {
		this.hasMany = hasMany;
	}


	public void setHasManyForeignColumn(String[] hasManyForeignColumn) {
		this.hasManyReferencedColumn = hasManyForeignColumn;
	}


	public void setHasManyReferredColumn(String[] hasManyReferredColumn) {
		this.hasManyLocalColumn = hasManyReferredColumn;
	}



	public void setHasOne(String[] hasOne) {
		this.hasOne = hasOne;
	}


	public void setHasOneLocalColumn(String[] hasOneForeignColumn) {
		this.hasOneLocalColumn = hasOneForeignColumn;
	}


	public void setHasOneReferredColumn(String[] hasOneReferredColumn){
		this.hasOneReferencedColumn = hasOneReferredColumn;
	}

	public String getForeignKeyColumn(String modelName){
		for(int i = 0; i < this.hasOne.length; i++){
			if(modelName.equalsIgnoreCase(this.hasOne[i])){
				return this.hasOneLocalColumn[i];
			}
		}
		return null;
	}


	/**
	 * get the foreign key column of this table definition from this refered table
	 */
	public String[] getLocalColumns(String modelName) {
		List<String> columnList = new ArrayList<String>();
		for(int i = 0; i < this.hasOne.length; i++){
			if(modelName.equalsIgnoreCase(this.hasOne[i])){
				columnList.add(hasOneLocalColumn[i]);
			}
		}
		for(int j = 0; j < this.hasMany.length; j++){
			if(modelName.equalsIgnoreCase(hasMany[j])){
				columnList.add(hasManyLocalColumn[j]);
			}
		}
		if(columnList.size() == 0){
			return null;
		}
		return columnList.toArray(new String[columnList.size()]);
	}


	public String[] getReferencedColumns(String modelName) {
		List<String> columnList = new ArrayList<String>();
		for(int i = 0; i < this.hasOne.length; i++){
			if(modelName.equalsIgnoreCase(this.hasOne[i])){
				columnList.add(hasOneReferencedColumn[i]);
			}
		}
		for(int j = 0; j < this.hasMany.length; j++){
			if(modelName.equalsIgnoreCase(hasMany[j])){
				columnList.add(hasManyReferencedColumn[j]);
			}
		}
		if(columnList.size() == 0){
			return null;
		}
		return columnList.toArray(new String[columnList.size()]);
	}

	public String[] getReferencedColumns(){
		List<String> columnList = new ArrayList<String>();
		for(String one : hasOneLocalColumn){
			columnList.add(one);
		}
		for(String many : hasManyLocalColumn){
			columnList.add(many);
		}
		return columnList.toArray(new String[columnList.size()]);
	}

	public void setGeneratedAttribute(String generatedAttribute) {
		this.generatedAttribute = generatedAttribute;
	}


	public boolean isCaseSensitive() {
		return caseSensitive;
	}


	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}


	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		Map<String, Object> map = toHashMap();
		sb.append("\n"+getTableName()+": {");
		for(Entry<String, Object> entry : map.entrySet()){
			sb.append("\n\t"+entry.getKey() +" : "+entry.getValue());
		}
		sb.append("\n}");
		return sb.toString();
	}


	public String getReferencedTable(String column){
		for(int l = 0; l < hasOneLocalColumn.length; l++){
			String refColumn = hasOneLocalColumn[l];
			if(refColumn.equalsIgnoreCase(column)){
				return hasOne[l];
			}
		}
		return null;
	}


	public boolean isPrimaryColumn(String column) {
		if(primaryAttributes != null){
			for(int i = 0; i < primaryAttributes.length; i++){
				String attr = primaryAttributes[i];
				if(attr.equals(column)){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Get the properties that are pertaining to the model, this does not include linker columns
	 * @return
	 */
	public String[] getPlainProperties(){
		String[] commonColumns = {"created", "createdby", "updated","updatedby", "isactive"};
		String[] referencedProperties = getReferencedColumns();
		List<String> plainProperties = new ArrayList<String>();
		for(String att : attributes){
			if(CStringUtils.indexOf(referencedProperties, att) >= 0 ){
				//do not include referenced columns
			}
			else if(att.endsWith("_id")){
				;//do not include implied columns
			}
			else if(CStringUtils.indexOf(commonColumns, att) >= 0){
				;//do not include common columns
			}
			else{
				plainProperties.add(att);
			}
		}
		return plainProperties.toArray(new String[plainProperties.size()]);
	}




	public void setPolymorphic(boolean polymorphic) {
		this.polymorphic = polymorphic;
	}



	public boolean isPolymorphic() {
		return polymorphic;
	}

	public String[] getReferencedModels(){
		List<String> refModels = new ArrayList<String>();
		for(String has : hasOne){
			refModels.add(has);
		}
		for(String many : hasMany){
			refModels.add(many);
		}
		return refModels.toArray(new String[refModels.size()]);
	}



	public String getTableName() {
		return tableName;
	}



	public void setTableName(String tableName) {
		this.tableName = tableName;
	}



	public String[] getOwners() {
		return owners;
	}



	public void setOwners(String[] owners) {
		this.owners = owners;
	}



	public String getPrimaryOwner() {
		return primaryOwner;
	}



	public void setPrimaryOwner(String primaryOwner) {
		this.primaryOwner = primaryOwner;
	}



	public String[] getDirectChildren() {
		return directChildren;
	}


	public void setDirectChildren(String[] directChildren) {
		this.directChildren = directChildren;
	}

	public String getParentClass() {
		return parentClass;
	}



	public String[] getSubClass() {
		return subClass;
	}



	public void setParentClass(String parentClass) {
		this.parentClass = parentClass;
	}



	public void setSubClass(String[] subClass) {
		this.subClass = subClass;
	}

	@Override
	public boolean equals(Object model2){
		if(model2 == null){
			return false;
		}
		String modelName = getModelName();
		if(modelName == null){
			return false;
		}
		return modelName.equals(((ModelDef)model2).getModelName());
	}

	public String[] getSameAttributes(ModelDef model2){
		if(model2 != null){
			List<String> matches = new ArrayList<String>();
			String[] att2 = model2.getAttributes();
			if(attributes != null){
				for(String att : attributes){
					int index = CStringUtils.indexOf(att2, att);
					if(index >= 0){
						matches.add(att);
					}
				}
			}
			return matches.toArray(new String[matches.size()]);
		}
		return null;
	}



	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
	}



	public String[] getAttributeComments() {
		return attributeComments;
	}



	public void setAttributeComments(String[] attributeComments) {
		this.attributeComments = attributeComments;
	}

	public Map<String, Object> toHashMap(){
		Map<String, Object> map = new TreeMap<String, Object>();
		if(namespace != null){
			map.put("namespace", namespace);
		}
		if(modelName != null){
			map.put("modelName", modelName);
		}
		if(tableName != null){
			map.put("tableName", tableName);
		}
		//if(description != null){
		//	map.put("description", description);
		//}
		if(attributes != null && attributes.length > 0){
			map.put("attributes", Arrays.asList(attributes));
		}
		if(generatedAttribute != null){
			map.put("generatedAttribute", generatedAttribute);
		}
		if(primaryAttributes != null && primaryAttributes.length > 0){
			map.put("primaryAttributes", Arrays.asList(primaryAttributes));
		}
		if(uniqueAttributes != null && uniqueAttributes.length > 0){
			map.put("uniqueAttributes", Arrays.asList(uniqueAttributes));
		}
		if(dataTypes != null && dataTypes.length > 0){
			map.put("dataTypes", Arrays.asList(dataTypes));
		}
		//if(attributeComments != null && attributeComments.length > 0){
		//	map.put("attributeComments", Arrays.asList(attributeComments));
		//}
		if(hasOneLocalColumn != null && hasOneLocalColumn.length > 0){
			map.put("hasOneLocalColumn", Arrays.asList(hasOneLocalColumn));
		}
		if(hasOne != null && hasOne.length > 0){
			map.put("hasOne", Arrays.asList(hasOne));
		}
		if(hasOneReferencedColumn != null && hasOneReferencedColumn.length > 0){
			map.put("hasOneReferencedColumn", Arrays.asList(hasOneReferencedColumn));
		}
		if(hasMany != null && hasMany.length > 0){
			map.put("hasMany", Arrays.asList(hasMany));
		}
		if(hasManyReferencedColumn != null && hasManyReferencedColumn.length > 0){
			map.put("hasManyReferencedColumn", Arrays.asList(hasManyReferencedColumn));
		}
		if(hasManyLocalColumn != null && hasManyLocalColumn.length > 0){
			map.put("hasManyLocalColumn", Arrays.asList(hasManyLocalColumn));
		}
		if(owners != null && owners.length > 0){
			map.put("owners", owners);
		}
		if(primaryOwner != null){
			map.put("primaryOwner", primaryOwner);
		}
		map.put("caseSensitive", caseSensitive);
		map.put("polymorphic", polymorphic);
		if(directChildren != null && directChildren.length > 0){
			map.put("directChildren", Arrays.asList(directChildren));
		}
		if(parentClass != null){
			map.put("parentClass", parentClass);
		}
//		if(subClass != null){
//			map.put("subClass", subClass);
//		}
		return map;
	}


}
