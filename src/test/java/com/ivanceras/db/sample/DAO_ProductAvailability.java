package com.ivanceras.db.sample;
/***
* This is automatically generated by DAOGenerator, based on the database table schema
* 
* 
*/

import com.ivanceras.db.shared.DAO;
import com.ivanceras.db.api.DAO_Operator;
public class DAO_ProductAvailability extends DAO{
/**
 *
 */


	public DAO_ProductAvailability(){
		super("ProductAvailability");
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

	public java.util.UUID getProductId(){
		return (java.util.UUID)get_Value(Column.product_id);
	}

	public void setProductId(java.util.UUID productId){
		set_Value(Column.product_id, productId);
	}

	public Boolean getAvailable(){
		return (Boolean)get_Value(Column.available);
	}

	public void setAvailable(Boolean available){
		set_Value(Column.available, available);
	}

	public Boolean getAlwaysAvailable(){
		return (Boolean)get_Value(Column.always_available);
	}

	public void setAlwaysAvailable(Boolean alwaysAvailable){
		set_Value(Column.always_available, alwaysAvailable);
	}

	public java.math.BigDecimal getStocks(){
		return (java.math.BigDecimal)get_Value(Column.stocks);
	}

	public void setStocks(java.math.BigDecimal stocks){
		set_Value(Column.stocks, stocks);
	}

	public java.util.Date getAvailableFrom(){
		return (java.util.Date)get_Value(Column.available_from);
	}

	public void setAvailableFrom(java.util.Date availableFrom){
		set_Value(Column.available_from, availableFrom);
	}

	public java.util.Date getAvailableUntil(){
		return (java.util.Date)get_Value(Column.available_until);
	}

	public void setAvailableUntil(java.util.Date availableUntil){
		set_Value(Column.available_until, availableUntil);
	}

	public String getAvailableDay(){
		return (String)get_Value(Column.available_day);
	}

	public void setAvailableDay(String availableDay){
		set_Value(Column.available_day, availableDay);
	}

	public java.util.Date getOpenTime(){
		return (java.util.Date)get_Value(Column.open_time);
	}

	public void setOpenTime(java.util.Date openTime){
		set_Value(Column.open_time, openTime);
	}

	public java.util.Date getCloseTime(){
		return (java.util.Date)get_Value(Column.close_time);
	}

	public void setCloseTime(java.util.Date closeTime){
		set_Value(Column.close_time, closeTime);
	}


	public void setProduct(DAO_Product product){
		set_Value("product", product);
	}

	public DAO_Product getProduct(){
		return (DAO_Product)get_Value("product");
	}
	
}