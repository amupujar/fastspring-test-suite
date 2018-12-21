package com.fastspringapi.marshalledobjs;

public class Pricing {
	private Price price;

	private Object quantityDiscounts;

	private Object dateLimits;

	private Object discountReason;

	private String quantityBehavior;

	private String dateLimitsEnabled;

	private String quantityDefault;

	public Price getPrice() {
		return price;
	}

	public void setPrice(Price price) {
		this.price = price;
	}

	public Object getQuantityDiscounts() {
		return quantityDiscounts;
	}

	public void setQuantityDiscounts(Object quantityDiscounts) {
		this.quantityDiscounts = quantityDiscounts;
	}

	public Object getDateLimits() {
		return dateLimits;
	}

	public void setDateLimits(Object dateLimits) {
		this.dateLimits = dateLimits;
	}

	public Object getDiscountReason() {
		return discountReason;
	}

	public void setDiscountReason(Object discountReason) {
		this.discountReason = discountReason;
	}

	public String getQuantityBehavior() {
		return quantityBehavior;
	}

	public void setQuantityBehavior(String quantityBehavior) {
		this.quantityBehavior = quantityBehavior;
	}

	public String getDateLimitsEnabled() {
		return dateLimitsEnabled;
	}

	public void setDateLimitsEnabled(String dateLimitsEnabled) {
		this.dateLimitsEnabled = dateLimitsEnabled;
	}

	public String getQuantityDefault() {
		return quantityDefault;
	}

	public void setQuantityDefault(String quantityDefault) {
		this.quantityDefault = quantityDefault;
	}

	@Override
	public String toString() {
		// This property won't be used therefore returning empty string
		return "";
	}
}
