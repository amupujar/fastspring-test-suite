REST Assured Framework
Project - This project makes use of Rest Api and makes use of GET and POSt
Get- It helps to retrieve all the products from FastSpring API
Using postman can see the GET request and Response got
It helps to get whatevere data is available without creating or updating
POST- this method is used to create a product- amruta-20181212
and if its present, next time it updates it
Issues faced while automating the first project in eclipse
Due to which it is done in VM
Creatingnew product-amruta-20181212


1. InvokeProductsApi.java
This file has different testcases
first one is getallproducts which onvokes all the products
2.Testpostsingleproduct
This test creates product and also checks the asserts thrown when user enters wrong productname created or if it is empty
2. testproductupdate and if product is created, next time it updates the product.
So this was challenging where the price was not updating soon after the request made, because of which thread.sleep with 18000 ms wait time was given.
initial price was 14.95usd and after price changed is 20.95$

Postman link
https://www.getpostman.com/collections/6c0c46f947c2cba3fda1

