package com.fastspringapi.marshalledobjs;

public class Price
{
    private String USD;

    public String getUSD ()
    {
        return USD;
    }

    public void setUSD (String USD)
    {
        this.USD = USD;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [USD = "+USD+"]";
    }
}