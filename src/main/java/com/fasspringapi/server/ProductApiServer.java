package com.fasspringapi.server;

public class ProductApiServer {

    private final String protocol;
    private final String host;

    public ProductApiServer(String protocol, String host) {
        this.protocol = protocol;
        this.host = host;
        //this.port = port;
    }

	public String getHost() {
        return String.format(protocol + "://" + host);
    }

}