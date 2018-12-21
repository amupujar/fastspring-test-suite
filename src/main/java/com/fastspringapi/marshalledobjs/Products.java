package com.fastspringapi.marshalledobjs;

public class Products {
	private Pricing pricing;

	private String product;

	private String result;

	private Object fulfillments;

	private Object description;

	private String action;

	private String image;

	private Object parent;

	private Object display;

	private String format;

	private String sku;

	public Pricing getPricing() {
		return pricing;
	}

	public void setPricing(Pricing pricing) {
		this.pricing = pricing;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Object getFulfillments() {
		return fulfillments;
	}

	public void setFulfillments(Object fulfillments) {
		this.fulfillments = fulfillments;
	}

	public Object getDescription() {
		return description;
	}

	public void setDescription(Object description) {
		this.description = description;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Object getParent() {
		return parent;
	}

	public void setParent(Object parent) {
		this.parent = parent;
	}

	public Object getDisplay() {
		return display;
	}

	public void setDisplay(Object display) {
		this.display = display;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	@Override
	public String toString() {
		// Won't be used therefore returning empty string
		return "";
	}
}
