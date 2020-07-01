package org.restop.sample;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.containsInRelativeOrder;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class FruitsEndpointTest {

    @Test
    public void testReadOne() {
        RestAssured
                .given()
                .pathParam("id", 1)
                .when().get("/fruits/{id}")
                .then()
                .statusCode(200)
                .body("id", is(1),
                        "name", is("Cherry"));
    }

    @Test
    public void testReadAll() {
        RestAssured
                .given()
                .when().get("/fruits")
                .then()
                .statusCode(200)
                .body("data.size()", is(3),
                        "data.name", containsInRelativeOrder("Cherry","Apple", "Banana"),
                        "meta.count", is(3),
                        "meta.offset", is(0),
                        "meta.limit", is(25),
                        "meta.sortBy", is("id:Ascending"),
                        "links.size()", is(2));
    }

    @Test
    public void testReadAllPaginated() {
        RestAssured
                .given()
                .queryParam("limit", "10")
                .queryParam("offset", "1")
                .when().get("/fruits")
                .then()
                .statusCode(200)
                .body("data.size()", is(2),
                        "data.name", containsInRelativeOrder("Apple", "Banana"),
                        "meta.count", is(3),
                        "meta.offset", is(1),
                        "meta.limit", is(10),
                        "meta.sortBy", is("id:Ascending"),
                        "links.size()", is(2));
    }

    @Test
    public void testReadAllPaginatedSortBy() {
        Response response = RestAssured
                .given()
                .queryParam("sort_by", "name:Ascending")
                .when().get("/fruits")
                .then().statusCode(200)
                .extract()
                .response();
        System.out.println("Sorted: " + response.asString());
    }

    @Test
    public void testReadablePaginatedByRangeWithWhere() {
        RestAssured
                .given()
                .queryParam("name", "Banana")
                .queryParam("name", "Apple")
                .queryParam("name", "Kiwi")
                .when().get("/fruits")
                .then().statusCode(200).body(
                Matchers.not(Matchers.containsString("Cherry")),
                Matchers.containsString("Apple"),
                Matchers.containsString("Banana")
        );

        Response response = RestAssured
                .given()
                .queryParam("where", "name:Banana")
                .when().get("/fruits")
                .then().statusCode(200)
                .extract()
                .response();
        System.out.println("Where: " + response.asString());
    }

    @Test
    public void testCreateAndDelete() {
        Fruit kiwi = new Fruit("kiwi");

        Integer kiwiId = RestAssured
                .given().contentType(ContentType.JSON).body(kiwi)
                .when().post("/fruits")
                .then().statusCode(201)
                .extract()
                .path("id");
        System.out.println("ID = " + kiwiId);

        Response response = RestAssured
                .given()
                .pathParam("id", kiwiId)
                .when().delete("/fruits/{id}")
                .then().statusCode(204)
                .extract()
                .response();
        System.out.println(response.asString());
    }

    @Test
    public void testUpdate() {
        Fruit mango = new Fruit("Mango");
        RestAssured
                .given()
                .pathParam("id", 2)
                .contentType(ContentType.JSON)
                .body(mango)
                .when().put("/fruits/{id}")
                .then()
                .statusCode(200)
                .body("id", is(2),
                        "name", is("Mango"));

        RestAssured
                .given()
                .pathParam("id", 2)
                .when().get("/fruits/{id}")
                .then()
                .statusCode(200)
                .body("id", is(2),
                        "name", is("Mango"));
    }

    @Test
    public void testNotFound() {
        RestAssured.when().get("/fruits/999").then().statusCode(404);
        RestAssured.when().delete("/fruits/999").then().statusCode(404);
    }
}
