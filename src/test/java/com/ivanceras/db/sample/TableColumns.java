package com.ivanceras.db.sample;

public class TableColumns{

	public class address{
	
		public static final String organization_id = "address.organization_id";
		public static final String client_id = "address.client_id";
		public static final String created = "address.created";
		public static final String createdby = "address.createdby";
		public static final String updated = "address.updated";
		public static final String updatedby = "address.updatedby";
		public static final String name = "address.name";
		public static final String description = "address.description";
		public static final String help = "address.help";
		public static final String active = "address.active";
		public static final String address_id = "address.address_id";
		public static final String latitude = "address.latitude";
		public static final String longitude = "address.longitude";

	}

	public class base{
	
		public static final String organization_id = "base.organization_id";
		public static final String client_id = "base.client_id";
		public static final String created = "base.created";
		public static final String createdby = "base.createdby";
		public static final String updated = "base.updated";
		public static final String updatedby = "base.updatedby";

	}

	public class cart{
	
		public static final String organization_id = "cart.organization_id";
		public static final String client_id = "cart.client_id";
		public static final String created = "cart.created";
		public static final String createdby = "cart.createdby";
		public static final String updated = "cart.updated";
		public static final String updatedby = "cart.updatedby";
		public static final String name = "cart.name";
		public static final String description = "cart.description";
		public static final String help = "cart.help";
		public static final String active = "cart.active";
		public static final String cart_id = "cart.cart_id";

	}

	public class cart_line{
	
		public static final String organization_id = "cart_line.organization_id";
		public static final String client_id = "cart_line.client_id";
		public static final String created = "cart_line.created";
		public static final String createdby = "cart_line.createdby";
		public static final String updated = "cart_line.updated";
		public static final String updatedby = "cart_line.updatedby";
		public static final String name = "cart_line.name";
		public static final String description = "cart_line.description";
		public static final String help = "cart_line.help";
		public static final String active = "cart_line.active";
		public static final String cart_id = "cart_line.cart_id";
		public static final String cart_line_id = "cart_line.cart_line_id";
		public static final String product_id = "cart_line.product_id";
		public static final String qty = "cart_line.qty";

	}

	public class category{
	
		public static final String organization_id = "category.organization_id";
		public static final String client_id = "category.client_id";
		public static final String created = "category.created";
		public static final String createdby = "category.createdby";
		public static final String updated = "category.updated";
		public static final String updatedby = "category.updatedby";
		public static final String name = "category.name";
		public static final String description = "category.description";
		public static final String help = "category.help";
		public static final String active = "category.active";
		public static final String category_id = "category.category_id";

	}

	public class client{
	
		public static final String organization_id = "client.organization_id";
		public static final String client_id = "client.client_id";
		public static final String created = "client.created";
		public static final String createdby = "client.createdby";
		public static final String updated = "client.updated";
		public static final String updatedby = "client.updatedby";
		public static final String name = "client.name";
		public static final String description = "client.description";
		public static final String help = "client.help";
		public static final String active = "client.active";

	}

	public class invoice{
	
		public static final String organization_id = "invoice.organization_id";
		public static final String client_id = "invoice.client_id";
		public static final String created = "invoice.created";
		public static final String createdby = "invoice.createdby";
		public static final String updated = "invoice.updated";
		public static final String updatedby = "invoice.updatedby";
		public static final String name = "invoice.name";
		public static final String description = "invoice.description";
		public static final String help = "invoice.help";
		public static final String active = "invoice.active";
		public static final String invoice_id = "invoice.invoice_id";
		public static final String order_id = "invoice.order_id";
		public static final String is_paid = "invoice.is_paid";

	}

	public class order_line{
	
		public static final String organization_id = "order_line.organization_id";
		public static final String client_id = "order_line.client_id";
		public static final String created = "order_line.created";
		public static final String createdby = "order_line.createdby";
		public static final String updated = "order_line.updated";
		public static final String updatedby = "order_line.updatedby";
		public static final String name = "order_line.name";
		public static final String description = "order_line.description";
		public static final String help = "order_line.help";
		public static final String active = "order_line.active";
		public static final String order_id = "order_line.order_id";
		public static final String product_id = "order_line.product_id";
		public static final String price_momentary = "order_line.price_momentary";
		public static final String freight_amt = "order_line.freight_amt";
		public static final String discount = "order_line.discount";
		public static final String order_line_id = "order_line.order_line_id";
		public static final String qty_ordered = "order_line.qty_ordered";

	}

	public class orders{
	
		public static final String organization_id = "orders.organization_id";
		public static final String client_id = "orders.client_id";
		public static final String created = "orders.created";
		public static final String createdby = "orders.createdby";
		public static final String updated = "orders.updated";
		public static final String updatedby = "orders.updatedby";
		public static final String name = "orders.name";
		public static final String description = "orders.description";
		public static final String help = "orders.help";
		public static final String active = "orders.active";
		public static final String order_id = "orders.order_id";
		public static final String customer_name = "orders.customer_name";
		public static final String total_items = "orders.total_items";
		public static final String grand_total_amount = "orders.grand_total_amount";
		public static final String charges_amount = "orders.charges_amount";
		public static final String processing = "orders.processing";
		public static final String processed = "orders.processed";
		public static final String is_confirmed = "orders.is_confirmed";
		public static final String is_tax_included = "orders.is_tax_included";
		public static final String date_ordered = "orders.date_ordered";
		public static final String is_invoiced = "orders.is_invoiced";
		public static final String date_invoiced = "orders.date_invoiced";
		public static final String is_approved = "orders.is_approved";
		public static final String date_approved = "orders.date_approved";
		public static final String amount_tendered = "orders.amount_tendered";
		public static final String amount_refunded = "orders.amount_refunded";

	}

	public class organization{
	
		public static final String organization_id = "organization.organization_id";
		public static final String client_id = "organization.client_id";
		public static final String created = "organization.created";
		public static final String createdby = "organization.createdby";
		public static final String updated = "organization.updated";
		public static final String updatedby = "organization.updatedby";
		public static final String name = "organization.name";
		public static final String description = "organization.description";
		public static final String help = "organization.help";
		public static final String active = "organization.active";
		public static final String parent_organization_id = "organization.parent_organization_id";
		public static final String address_id = "organization.address_id";

	}

	public class photo{
	
		public static final String organization_id = "photo.organization_id";
		public static final String client_id = "photo.client_id";
		public static final String created = "photo.created";
		public static final String createdby = "photo.createdby";
		public static final String updated = "photo.updated";
		public static final String updatedby = "photo.updatedby";
		public static final String name = "photo.name";
		public static final String description = "photo.description";
		public static final String help = "photo.help";
		public static final String active = "photo.active";
		public static final String photo_id = "photo.photo_id";
		public static final String url = "photo.url";
		public static final String data = "photo.data";
		public static final String seq_no = "photo.seq_no";

	}

	public class product{
	
		public static final String organization_id = "product.organization_id";
		public static final String client_id = "product.client_id";
		public static final String created = "product.created";
		public static final String createdby = "product.createdby";
		public static final String updated = "product.updated";
		public static final String updatedby = "product.updatedby";
		public static final String name = "product.name";
		public static final String description = "product.description";
		public static final String help = "product.help";
		public static final String active = "product.active";
		public static final String product_id = "product.product_id";
		public static final String parent_product_id = "product.parent_product_id";
		public static final String is_service = "product.is_service";
		public static final String price = "product.price";
		public static final String use_parent_price = "product.use_parent_price";
		public static final String unit = "product.unit";
		public static final String stocks = "product.stocks";
		public static final String tags = "product.tags";
		public static final String info = "product.info";
		public static final String currency = "product.currency";
		public static final String seq_no = "product.seq_no";
		public static final String upfront_fee = "product.upfront_fee";

	}

	public class product_availability{
	
		public static final String organization_id = "product_availability.organization_id";
		public static final String client_id = "product_availability.client_id";
		public static final String created = "product_availability.created";
		public static final String createdby = "product_availability.createdby";
		public static final String updated = "product_availability.updated";
		public static final String updatedby = "product_availability.updatedby";
		public static final String product_id = "product_availability.product_id";
		public static final String available = "product_availability.available";
		public static final String always_available = "product_availability.always_available";
		public static final String stocks = "product_availability.stocks";
		public static final String available_from = "product_availability.available_from";
		public static final String available_until = "product_availability.available_until";
		public static final String available_day = "product_availability.available_day";
		public static final String open_time = "product_availability.open_time";
		public static final String close_time = "product_availability.close_time";

	}

	public class product_category{
	
		public static final String organization_id = "product_category.organization_id";
		public static final String client_id = "product_category.client_id";
		public static final String created = "product_category.created";
		public static final String createdby = "product_category.createdby";
		public static final String updated = "product_category.updated";
		public static final String updatedby = "product_category.updatedby";
		public static final String product_id = "product_category.product_id";
		public static final String category_id = "product_category.category_id";

	}

	public class record{
	
		public static final String organization_id = "record.organization_id";
		public static final String client_id = "record.client_id";
		public static final String created = "record.created";
		public static final String createdby = "record.createdby";
		public static final String updated = "record.updated";
		public static final String updatedby = "record.updatedby";
		public static final String name = "record.name";
		public static final String description = "record.description";
		public static final String help = "record.help";
		public static final String active = "record.active";

	}

	public class settings{
	
		public static final String organization_id = "settings.organization_id";
		public static final String client_id = "settings.client_id";
		public static final String created = "settings.created";
		public static final String createdby = "settings.createdby";
		public static final String updated = "settings.updated";
		public static final String updatedby = "settings.updatedby";
		public static final String name = "settings.name";
		public static final String description = "settings.description";
		public static final String help = "settings.help";
		public static final String active = "settings.active";
		public static final String user_id = "settings.user_id";
		public static final String value = "settings.value";
		public static final String settings_id = "settings.settings_id";
		public static final String use_metric = "settings.use_metric";

	}

	public class user_info{
	
		public static final String organization_id = "user_info.organization_id";
		public static final String client_id = "user_info.client_id";
		public static final String created = "user_info.created";
		public static final String createdby = "user_info.createdby";
		public static final String updated = "user_info.updated";
		public static final String updatedby = "user_info.updatedby";
		public static final String name = "user_info.name";
		public static final String description = "user_info.description";
		public static final String help = "user_info.help";
		public static final String active = "user_info.active";
		public static final String user_id = "user_info.user_id";
		public static final String address_id = "user_info.address_id";
		public static final String current_location = "user_info.current_location";
		public static final String displayname = "user_info.displayname";

	}

	public class users{
	
		public static final String organization_id = "users.organization_id";
		public static final String client_id = "users.client_id";
		public static final String created = "users.created";
		public static final String createdby = "users.createdby";
		public static final String updated = "users.updated";
		public static final String updatedby = "users.updatedby";
		public static final String name = "users.name";
		public static final String description = "users.description";
		public static final String help = "users.help";
		public static final String active = "users.active";
		public static final String user_id = "users.user_id";
		public static final String username = "users.username";
		public static final String password = "users.password";
		public static final String email = "users.email";

	}

	public class wishlist{
	
		public static final String organization_id = "wishlist.organization_id";
		public static final String client_id = "wishlist.client_id";
		public static final String created = "wishlist.created";
		public static final String createdby = "wishlist.createdby";
		public static final String updated = "wishlist.updated";
		public static final String updatedby = "wishlist.updatedby";
		public static final String name = "wishlist.name";
		public static final String description = "wishlist.description";
		public static final String help = "wishlist.help";
		public static final String active = "wishlist.active";
		public static final String wishlist_id = "wishlist.wishlist_id";

	}

	public class wishlist_line{
	
		public static final String organization_id = "wishlist_line.organization_id";
		public static final String client_id = "wishlist_line.client_id";
		public static final String created = "wishlist_line.created";
		public static final String createdby = "wishlist_line.createdby";
		public static final String updated = "wishlist_line.updated";
		public static final String updatedby = "wishlist_line.updatedby";
		public static final String name = "wishlist_line.name";
		public static final String description = "wishlist_line.description";
		public static final String help = "wishlist_line.help";
		public static final String active = "wishlist_line.active";
		public static final String wishlist_id = "wishlist_line.wishlist_id";
		public static final String price_momentary = "wishlist_line.price_momentary";
		public static final String product_id = "wishlist_line.product_id";
		public static final String added_to_cart = "wishlist_line.added_to_cart";
		public static final String wishlist_line_id = "wishlist_line.wishlist_line_id";

	}

}
