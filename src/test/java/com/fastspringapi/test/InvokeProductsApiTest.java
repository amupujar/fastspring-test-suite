package com.fastspringapi.test;

import static com.fasspringapi.server.ProductApiServerConstants.AUTH_PASSWORD;
import static com.fasspringapi.server.ProductApiServerConstants.AUTH_USERNAME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.junit.Test;

import com.fastspringapi.marshalledobjs.Root;
import com.fastspringapi.util.BaseClass;
import com.google.gson.Gson;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

/**
 * This class wraps unit tests that assert the working of POST and GET requests
 * for the products end-point of the fastspring API suite.
 * 
 * @author amruta
 *
 */
public class InvokeProductsApiTest extends BaseClass {

	private static final String PRODUCT_ID = "amruta-20181212";
	private static final String PRICE_BEFORE_UPDATE = "14.95";
	private static final String PRICE_AFTER_UPDATE = "20.95";
	private static final long WAIT_COUNTER_FOR_PREVENTING_STALE_READS = 180000;
	private static final int STATUS_OK = 200;
	private static final int BAD_REQUEST = 400;

	// TODO: Extract strings to constants

	/**
	 * This method tests the GET request made to the products end-point. This
	 * tests for a true-positive scenario, expecting a status-ok code with a
	 * valid payload.
	 */
	@Test
	public void getAllProductsTest() {
		Response responseAllProducts = RestAssured.given().auth().preemptive().basic(AUTH_USERNAME, AUTH_PASSWORD)
				.when().get(server.getHost() + "/products").then().log().ifValidationFails().extract().response();

		assert (responseAllProducts.getStatusCode() == STATUS_OK);
	}

	/**
	 * This unit test checks if we are able to update an already existing
	 * product
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void updateProductTest() throws FileNotFoundException, InterruptedException {

		Gson gson = new Gson();

		// Step 1: Ensure that the product is available (created)
		String createRequestBody = extractPayloadFromFileHelper("createProduct.json");
		Response responseSingleProduct = getResponseFromPayloadHelper(createRequestBody);
		assertThat("Checking the response code of the request", responseSingleProduct.statusCode(), is(STATUS_OK));

		Thread.sleep(WAIT_COUNTER_FOR_PREVENTING_STALE_READS);
		Response responseProduct = getProductPayloadHelper();
		Root rootObj = gson.fromJson(responseProduct.body().asString(), Root.class);
		String priceBeforeUpdate = rootObj.getProducts()[0].getPricing().getPrice().getUSD();
		assertThat("Price before update was as expected", priceBeforeUpdate.equals(PRICE_BEFORE_UPDATE));

		// Step 2: Update existing product
		String createRequestBodyUpdate = extractPayloadFromFileHelper("updateProduct.json");
		Response responseSingleProductUpdate = getResponseFromPayloadHelper(createRequestBodyUpdate);
		assertThat("Checking the response code of the  update request", responseSingleProductUpdate.statusCode(),
				is(STATUS_OK));

		// TODO: I had to resort to using a Thread sleep here because of
		// eventual consistency
		// related issues. The propogation of write happens asynchornously in
		// the backend
		// and I was reading stale data meanwhile causing the test to fail. Only
		// workaround
		// was to add wait time to allow for the write to propogate across all
		// nodes
		Thread.sleep(WAIT_COUNTER_FOR_PREVENTING_STALE_READS);
		// Step 3: Check if the product was updated
		Response responseProductAfterUpdate = getProductPayloadHelper();
		Root rootObjAfterUpdate = gson.fromJson(responseProductAfterUpdate.body().asString(), Root.class);
		String priceAfterUpdate = rootObjAfterUpdate.getProducts()[0].getPricing().getPrice().getUSD();
		assertThat("Update request completed successfully", responseProductAfterUpdate.getStatusCode() == 200);
		assertThat("Price after update was as expected", priceAfterUpdate.equals(PRICE_AFTER_UPDATE));

	}

	/**
	 * Returns the payload received on making a GET request to the products
	 * endpoint on passing a pre-declared pre-created product-id. This is
	 * leveraged by other unit tests within this wrapper class.
	 * 
	 * @return
	 */
	private Response getProductPayloadHelper() {
		Response responseProduct = RestAssured.given().auth().preemptive().basic(AUTH_USERNAME, AUTH_PASSWORD).when()
				.get(server.getHost() + "/products/" + PRODUCT_ID).then().log().ifValidationFails().extract()
				.response();
		return responseProduct;
	}

	/**
	 * This unit test validates if we are able to create a product using the
	 * fastspring api suite
	 * 
	 * @throws FileNotFoundException
	 */
	@Test
	public void postSingleProductTest() throws FileNotFoundException {

		String createRequestBody = extractPayloadFromFileHelper("createProduct.json");

		Response responseSingleProduct = getResponseFromPayloadHelper(createRequestBody);
		// LOGGER.info(responseSingleProduct.prettyPrint());

		assertThat("Checking the response code of the request", responseSingleProduct.statusCode(), is(STATUS_OK));

		assertThat("Checking if the response result is success",
				responseSingleProduct.path("products[0].result").toString(), is("success"));

		assertThat("Checking if the product name is right",
				responseSingleProduct.path("products[0].product").toString(), is("amruta-20181212"));

	}

	/**
	 * This is a helper method that extracts the contents of a file placed in
	 * the project directory. This is leveraged by other methods to create body
	 * of the REST API call.
	 * 
	 * @param localFileName
	 *            represents name of the file that is placed in the project
	 *            directory to create body of the REST API.
	 * @return
	 * @throws FileNotFoundException
	 */
	private String extractPayloadFromFileHelper(String localFileName) throws FileNotFoundException {
		ClassLoader classLoader = getClass().getClassLoader();
		Scanner scanner = new Scanner(new File(classLoader.getResource(localFileName).getFile()), "UTF-8");
		String createRequestBody = scanner.useDelimiter("\\A").next();
		scanner.close();
		return createRequestBody;
	}

	/**
	 * This method returns an {@link Response} instance consuming body of the
	 * REST API passed as an argument. This is a utility method leveraged by
	 * other test method within this class
	 * 
	 * @param createRequestBody
	 *            represents the body parameter for rest call made to the
	 *            products end-point
	 * @return
	 */
	private Response getResponseFromPayloadHelper(String createRequestBody) {
		Response responseSingleProduct = RestAssured.given().auth().preemptive().basic(AUTH_USERNAME, AUTH_PASSWORD)
				.when().body(createRequestBody).post(server.getHost() + "/products").then().log().ifValidationFails()
				.extract().response();
		return responseSingleProduct;
	}

	/**
	 * This method tests for true negative scenario. We are expecting to get a
	 * bad-request error-code for payload that is malformed.
	 * 
	 * @throws FileNotFoundException
	 */
	@Test
	public void testErrorPostSingleProduct() throws FileNotFoundException {

		ClassLoader classLoader = getClass().getClassLoader();
		Scanner scanner = new Scanner(new File(classLoader.getResource("createProductError.json").getFile()), "UTF-8");
		String createRequestBody = scanner.useDelimiter("\\A").next();
		scanner.close();

		Response responseSingleProduct = getResponseFromPayloadHelper(createRequestBody);

		assertThat("Checking the response code of the request", responseSingleProduct.statusCode(), is(BAD_REQUEST));

		assertThat("Checking if the response result is error",
				responseSingleProduct.path("products[0].result").toString(), is("error"));

		assertThat("Checking if the product name is wrong",
				responseSingleProduct.path("products[0].product").toString(), is("amruta-2018121212"));

	}

	/**
	 * This method tests for true negative scenario. We are expecting to get a
	 * bad-request error-code for payload that is malformed (or empty in this
	 * scenario)
	 * 
	 * @throws FileNotFoundException
	 */
	@Test
	public void emptyProductTest() throws FileNotFoundException {

		ClassLoader classLoader = getClass().getClassLoader();
		Scanner scanner = new Scanner(new File(classLoader.getResource("productname_null.json").getFile()), "UTF-8");
		String createRequestBody = scanner.useDelimiter("\\A").next();
		scanner.close();

		Response responseSingleProduct = getResponseFromPayloadHelper(createRequestBody);
		assertThat("Checking if the product name is empty",
				responseSingleProduct.path("products[0].product").toString(), is(""));
		assertThat(responseSingleProduct.statusCode(), is(BAD_REQUEST));

	}

}
