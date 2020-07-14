package org.restop.crud.deployment;

import io.quarkus.test.QuarkusUnitTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.restop.crud.deployment.pojo.Fruit;
import org.restop.crud.deployment.rest.FruitResource;

import static org.hamcrest.Matchers.*;

public class NoDtoTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .setArchiveProducer(() -> 
                    ShrinkWrap.create(JavaArchive.class)
                    .addClasses(Fruit.class, FruitResource.class)
                    .addAsResource("import.sql")
                    .addAsResource("application.properties")
            );

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
                        "meta.sort", is("id:Ascending"),
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
                        "meta.sort", is("id:Ascending"),
                        "links.size()", is(2));
    }

    @Test
    public void testReadAllPaginatedSort() {
        Response response = RestAssured
                .given()
                .queryParam("sort", "name:Ascending")
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
                not(containsString("Cherry")),
                containsString("Apple"),
                containsString("Banana")
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
        Fruit kiwi = new Fruit("kiwi", "It's an edible berry");
        Response response = RestAssured
                .given().contentType(ContentType.JSON).body(kiwi)
                .when().post("/fruits")
                .then().statusCode(201)
                .extract()
                .response();
        System.out.println(response.asString());

        response = RestAssured
                .when().delete("/fruits/4")
                .then().statusCode(204)
                .extract()
                .response();
        System.out.println(response.asString());
    }

    @Test
    public void testUpdate() {
        Fruit mango = new Fruit("Mango", "Tropical Fruit.");
        RestAssured
                .given()
                .pathParam("id", 2)
                .contentType(ContentType.JSON)
                .body(mango)
                .when().put("/fruits/{id}")
                .then()
                .statusCode(200)
                .body("id", is(2),
                        "name", is("Mango"),
                        "description", is("Tropical Fruit."));

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
