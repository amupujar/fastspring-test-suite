package com.fastspringapi.test;
import static com.fasspringapi.server.ProductApiServerConstants.AUTH_PASSWORD;
import static com.fasspringapi.server.ProductApiServerConstants.AUTH_USERNAME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import com.fastspringapi.marshalledobjs.Root;
import com.fastspringapi.util.BaseClass;
import com.google.gson.Gson;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

import groovy.json.JsonParser;

public class InvokeProductsApi extends BaseClass{

  //  private static final Logger LOGGER = LoggerFactory.getLogger(InvokeProductsApi.class);
	
	private static final String PRODUCT_ID = "amruta-20181212";
	private static final String PRICE_BEFORE_UPDATE = "14.95";
	private static final String PRICE_AFTER_UPDATE = "20.95";

    @Test
    public void testGetAllProducts() {
        Response responseAllProducts = RestAssured.given()
        		.auth()
        		.preemptive()
                .basic(AUTH_USERNAME, AUTH_PASSWORD)
                .when()
                .get(server.getHost() + "/products")
                .then()
                .log()
                .ifValidationFails()
                .extract()
                .response();
        
        assert(responseAllProducts.getStatusCode()==200);
    }
    
    /**
     * This unit test checks if we are able to update an
     * already existing product
     * @throws InterruptedException 
     */
    @Test
    public void testProductUpdate() throws FileNotFoundException, InterruptedException {
    	
    	Gson gson = new Gson();

    	// Step 1: Ensure that the product is available (created)
    	String createRequestBody = extractPayloadFromFile("createProduct.json");
        Response responseSingleProduct = getResponseFromPayload(createRequestBody);
        assertThat("Checking the response code of the request", responseSingleProduct.statusCode(), is(200));
        
        Thread.sleep(180000);
        Response responseProduct = getStaticProductPayload();
        Root rootObj = gson.fromJson(responseProduct.body().asString(), Root.class);
        String priceBeforeUpdate = rootObj.getProducts()[0].getPricing().getPrice().getUSD();
        assertThat("Price before update was as expected", priceBeforeUpdate.equals(PRICE_BEFORE_UPDATE));
        
        // Step 2: Update existing product     
        String createRequestBodyUpdate = extractPayloadFromFile("updateProduct.json");
    	Response responseSingleProductUpdate = getResponseFromPayload(createRequestBodyUpdate);
        assertThat("Checking the response code of the  update request", responseSingleProductUpdate.statusCode(), is(200));
        
        // TODO: I had to resort to using a Thread sleep here because of eventual consistency
        // related issues. The propogation of write happens asynchornously in the backend
        // and I was reading stale data meanwhile causing the test to fail. Only workaround
        // was to add wait time to allow for the write to propogate across all nodes
        Thread.sleep(180000);
        // Step 3: Check if the product was updated       
        Response responseProductAfterUpdate = getStaticProductPayload();
        Root rootObjAfterUpdate = gson.fromJson(responseProductAfterUpdate.body().asString(), Root.class);
        String priceAfterUpdate = rootObjAfterUpdate.getProducts()[0].getPricing().getPrice().getUSD();
        assertThat("Update request completed successfully", responseProductAfterUpdate.getStatusCode()==200);
        assertThat("Price after update was as expected", priceAfterUpdate.equals(PRICE_AFTER_UPDATE));
        
    }

	private Response getStaticProductPayload() {
		Response responseProduct = RestAssured.given()
        		.auth()
        		.preemptive()
                .basic(AUTH_USERNAME, AUTH_PASSWORD)
                .when()
                .get(server.getHost() + "/products/" + PRODUCT_ID)
                .then()
                .log()
                .ifValidationFails()
                .extract()
                .response();
		return responseProduct;
	}

    /**
     * This unit test validates if we are able
     * to create a product using the fastsprint
     * api suite
     * @throws FileNotFoundException
     */
    @Test
    public void testPostSingleProduct() throws FileNotFoundException {


        String createRequestBody = extractPayloadFromFile("createProduct.json");

        Response responseSingleProduct = getResponseFromPayload(createRequestBody);
       // LOGGER.info(responseSingleProduct.prettyPrint());

        assertThat("Checking the response code of the request", responseSingleProduct.statusCode(), is(200));

        assertThat("Checking if the response result is success", responseSingleProduct.path("products[0].result").toString(), is("success"));

        assertThat("Checking if the product name is right", responseSingleProduct.path("products[0].product").toString(), is("amruta-20181212"));

    }

	private String extractPayloadFromFile(String localFileName) throws FileNotFoundException {
		ClassLoader classLoader = getClass().getClassLoader();
        Scanner scanner = new Scanner( new File(classLoader.getResource(localFileName).getFile()), "UTF-8" );
        String createRequestBody = scanner.useDelimiter("\\A").next();
        scanner.close();
		return createRequestBody;
	}

	private Response getResponseFromPayload(String createRequestBody) {
		Response responseSingleProduct = RestAssured.given()
                .auth()
                .preemptive()
                .basic(AUTH_USERNAME, AUTH_PASSWORD)
                .when()
                .body(createRequestBody)
                .post(server.getHost() + "/products")
                .then()
                .log()
                .ifValidationFails()
                .extract()
                .response();
		return responseSingleProduct;
	}


    @Test
    public void testErrorPostSingleProduct() throws FileNotFoundException {


        ClassLoader classLoader = getClass().getClassLoader();
        Scanner scanner = new Scanner( new File(classLoader.getResource("createProductError.json").getFile()), "UTF-8" );
        String createRequestBody = scanner.useDelimiter("\\A").next();
        scanner.close();

        Response responseSingleProduct = getResponseFromPayload(createRequestBody);

        assertThat("Checking the response code of the request", responseSingleProduct.statusCode(), is(400));

        assertThat("Checking if the response result is error", responseSingleProduct.path("products[0].result").toString(), is("error"));

        assertThat("Checking if the product name is wrong", responseSingleProduct.path("products[0].product").toString(), is("amruta-2018121212"));

    }
    
    @Test
    public void negativeTest() throws FileNotFoundException {


        ClassLoader classLoader = getClass().getClassLoader();
        Scanner scanner = new Scanner( new File(classLoader.getResource("productname_null.json").getFile()), "UTF-8" );
        String createRequestBody = scanner.useDelimiter("\\A").next();
        scanner.close();

        Response responseSingleProduct = getResponseFromPayload(createRequestBody);
        assertThat("Checking if the product name is empty", responseSingleProduct.path("products[0].product").toString(), is(""));
       


    }

	
}
