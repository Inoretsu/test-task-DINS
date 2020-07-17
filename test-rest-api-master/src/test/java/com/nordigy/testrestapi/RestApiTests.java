package com.nordigy.testrestapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.http.HttpStatus;


import javax.annotation.PostConstruct;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// It allows to refresh context(Database) before an each method. So your tests always will be executed on the same snapshot of DB.
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class RestApiTests {

    @LocalServerPort
    private int port;

    @PostConstruct
    public void init() {
        RestAssured.port = port;
    }

    @Test
    public void shouldReturnCorrectUsersListSize() {
        given().log().all()
               .when().get("/api/users")
               .then().log().ifValidationFails()
               .statusCode(200)
               .body("page.totalElements", is(20));
    }

    @Test
    public void shouldCreateNewUser() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("firstName", "Ivan");
        objectNode.put("lastName", "Ivanov");
        objectNode.put("dayOfBirth", "2000-01-01");
        objectNode.put("email", "asdas@asdas.tr");

        ObjectNode user = given().log().all()
                                 .body(objectNode)
                                 .contentType(ContentType.JSON)
                                 .when().post("/api/users")
                                 .then().log().ifValidationFails()
                                 .statusCode(201)
                                 .extract().body().as(ObjectNode.class);

        assertThat(user).isEqualTo(objectNode);
        assertThat(user.get("id").asLong()).isGreaterThan(20);
    }

    // TODO: The test methods above are examples of test cases.
    //  Please add new cases below, but don't hesitate to refactor the whole class.

    /*--- GET ---*/

    @Test
    public void shouldGetCreatedUser() {

        // iserId = createUser()
        // Ok, user with id 1 definitely exist.
        long userId = 1;
        given().pathParam("id", userId).log().all()
            .contentType(ContentType.JSON)
            .when()
                .get("/api/users/{id}")
            .then()
                .log().body().statusCode(200)
                .and().body("id", equalTo(1));
    }

    @Test
    public void shouldNotGetUser() {

        long userId = 21;
        given().pathParam("id", userId).log().all()
            .contentType(ContentType.JSON)
            .when()
                .get("/api/users/{id}")
            .then()
                .log().body().statusCode(404);
    }

    /* --- */

    /*--- PUT ---*/

    @Test
    public void shouldUpdateExistUser() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("firstName", "Ivan");
        objectNode.put("lastName", "Ivanov");
        objectNode.put("dayOfBirth", "2000-01-01");
        objectNode.put("email", "asdas@asdas.tr");

        long userId = 1;

        ObjectNode user = given().pathParam("id", userId).log().all()
            .contentType("application/json").body(objectNode)
            .when()
                .put("/api/users/{id}")
            .then()
                .log().ifValidationFails().statusCode(200)
                .extract().body().as(ObjectNode.class);
        
        //assertThat(user).isEqualTo(objectNode);
        assertThat(user.get("firstName").asText()).isEqualTo("Ivan");
    }

    @Test
    public void shouldCreateNewUserById() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("firstName", "Ivan");
        objectNode.put("lastName", "Ivanov");
        objectNode.put("dayOfBirth", "2000-01-01");
        objectNode.put("email", "asdas@asdas.tr");

        long userId = 25;

        ObjectNode user = given().pathParam("id", userId).log().all()
            .contentType("application/json").body(objectNode)
            .when()
                .put("/api/users/{id}")
            .then()
                .log().ifValidationFails().statusCode(201)
                .extract().body().as(ObjectNode.class);
        
        //assertThat(user).isEqualTo(objectNode);
        assertThat(user.get("firstName").asText()).isEqualTo("Ivan");
        assertThat(user.get("id").asLong()).isEqualTo(25);
    }
    /* --- */

    /*--- DELETE ---*/
    @Test
    public void shouldDeleteExistUser() {

        long userId = 1;

        given().pathParam("id",  userId).log().all()
            .contentType(ContentType.JSON)
            .when()
                .delete("/api/users/{id}")
            .then()
                .log().ifValidationFails()
                .statusCode(204);
        /*
        given().log().all().pathParam("id",  userId)
            .contentType(ContentType.JSON)
            .when()
                .get("/api/users/{id}")
            .then()
                .log().body()
                .statusCode(404);
        */
    }

    /* --- */
}