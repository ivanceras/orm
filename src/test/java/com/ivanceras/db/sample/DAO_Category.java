package com.ivanceras.db.sample;
/***
* This is automatically generated by DAOGenerator, based on the database table schema
* 
* 
*/

import com.ivanceras.db.shared.DAO;
public class DAO_Category extends DAO{
/**
 *
 */


	public DAO_Category(){
		super("Category");
	}

	public java.util.UUID getOrganizationId(){
		return (java.util.UUID)get_Value(Column.organization_id);
	}

	public void setOrganizationId(java.util.UUID organizationId){
		set_Value(Column.organization_id, organizationId);
	}

	public java.util.UUID getClientId(){
		return (java.util.UUID)get_Value(Column.client_id);
	}

	public void setClientId(java.util.UUID clientId){
		set_Value(Column.client_id, clientId);
	}

	public java.util.Date getCreated(){
		return (java.util.Date)get_Value(Column.created);
	}

	public void setCreated(java.util.Date created){
		set_Value(Column.created, created);
	}

	public java.util.UUID getCreatedby(){
		return (java.util.UUID)get_Value(Column.createdby);
	}

	public void setCreatedby(java.util.UUID createdby){
		set_Value(Column.createdby, createdby);
	}

	public java.util.Date getUpdated(){
		return (java.util.Date)get_Value(Column.updated);
	}

	public void setUpdated(java.util.Date updated){
		set_Value(Column.updated, updated);
	}

	public java.util.UUID getUpdatedby(){
		return (java.util.UUID)get_Value(Column.updatedby);
	}

	public void setUpdatedby(java.util.UUID updatedby){
		set_Value(Column.updatedby, updatedby);
	}

	public String getName(){
		return (String)get_Value(Column.name);
	}

	public void setName(String name){
		set_Value(Column.name, name);
	}

	public String getDescription(){
		return (String)get_Value(Column.description);
	}

	public void setDescription(String description){
		set_Value(Column.description, description);
	}

	public String getHelp(){
		return (String)get_Value(Column.help);
	}

	public void setHelp(String help){
		set_Value(Column.help, help);
	}

	public Boolean getActive(){
		return (Boolean)get_Value(Column.active);
	}

	public void setActive(Boolean active){
		set_Value(Column.active, active);
	}

	public java.util.UUID getCategoryId(){
		return (java.util.UUID)get_Value(Column.category_id);
	}

	public void setCategoryId(java.util.UUID categoryId){
		set_Value(Column.category_id, categoryId);
	}


	public void setCategory(DAO_Category category){
		set_Value("category", category);
	}

	public DAO_Category getCategory(){
		return (DAO_Category)get_Value("category");
	}

	public void setCategoryList(DAO_Category[] categoryList){
		set_Value("categoryList", categoryList);
	}

	public DAO_Category[] getCategoryList(){
		return (DAO_Category[])get_Value("categoryList");
	}

	public void setProductList(DAO_Product[] productList){
		set_Value("productList", productList);
	}

	public DAO_Product[] getProductList(){
		return (DAO_Product[])get_Value("productList");
	}
	
}
