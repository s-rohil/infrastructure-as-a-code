package com.csye6225.spring2018;

import io.restassured.RestAssured;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

public class SpringBootWebApplicationTest {

  /*@Ignore
  @Test
  public void contextLoads() {
  }*/


  @Test
  public void simpleTest() throws Exception {
    System.out.println("Test Successful");
  }

 // @Ignore
  @Test
  public void testGetHomePage() throws URISyntaxException {
    RestAssured.when().get(new URI("/")).then().statusCode(200);
  }

}
