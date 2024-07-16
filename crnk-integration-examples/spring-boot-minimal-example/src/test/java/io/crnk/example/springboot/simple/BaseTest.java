package io.crnk.example.springboot.simple;

import io.crnk.client.CrnkClient;
import io.crnk.example.springboot.microservice.MinimalSpringBootApplication;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.InputStream;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MinimalSpringBootApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
public abstract class BaseTest {

	@Value("${local.server.port}")
	protected int port;

	protected String jsonApiSchema;

	protected CrnkClient client;

	private static String loadFile(String filename) throws Exception {
		InputStream inputStream = BaseTest.class.getClassLoader().getResourceAsStream(filename);
		return IOUtils.toString(inputStream);
	}

	@Before
	public final void before() {
		RestAssured.port = port;
		loadJsonApiSchema();

		client = new CrnkClient("http://localhost:" + port);
	}

	private void loadJsonApiSchema() {
		try {
			jsonApiSchema = loadFile("json-api-schema.json");
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	protected void testFindOne(String url) {
		ValidatableResponse response = RestAssured.given().contentType("application/json").when().get(url).then()
				.statusCode(OK.value());
		response.assertThat().body(matchesJsonSchema(jsonApiSchema));
	}

	protected void testFindOne_NotFound(String url) {
		RestAssured.given().contentType("application/json").when().get(url).then().statusCode(NOT_FOUND.value());
	}

	protected void testFindMany(String url) {
		ValidatableResponse response = RestAssured.given().contentType("application/json").when().get(url).then()
				.statusCode(OK.value());
		response.assertThat().body(matchesJsonSchema(jsonApiSchema));
	}

	protected void testDelete(String url) {
		RestAssured.given().contentType("application/json").when().delete(url).then().statusCode(NO_CONTENT.value());
	}
}
