package org.restop.sample;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class LibraryResourceTest {

    @Test
    public void testLibrary() throws Exception {
        // Full text search
        RestAssured.when().get("/library/author?firstName=John").then()
                .statusCode(200)
                .body("data.size()", is(1),
                        "meta.count", is(1),
                        "meta.offset", is(0),
                        "meta.limit", is(25),
                        "meta.sort", is("id:Ascending"),
                        "links.size()", is(2),
                        "data.firstName", contains("John"),
                        "data.lastName", contains("Irving"));

/*        RestAssured.when().get("/library/author/search?pattern=vertigo").then()
                .statusCode(200)
                .body("firstName", contains("Paul"),
                        "lastName", contains("Auster"));

        RestAssured.when().get("/library/author/search?pattern=mystery").then()
                .statusCode(200)
                .body("firstName", contains("John"),
                        "lastName", contains("Irving"));*/

        // Add an author
        Author davidWrong = new Author();
        davidWrong.firstName = "David";
        davidWrong.lastName = "Wrong";
        RestAssured.given()
//                .contentType(ContentType.URLENC.withCharset("UTF-8"))
//                .formParam("firstName", "David")
//                .formParam("lastName", "Wrong")
//                .put("/library/author/")
                .contentType(ContentType.JSON).body(davidWrong)
                .post("/library/author/")
                .then()
                .statusCode(201);

//        Integer davidLodgeId = RestAssured.when().get("/library/author/search?pattern=dav*").then()
        Integer davidLodgeId = RestAssured.when().get("/library/author?firstName=David").then()
                .statusCode(200)
                .body("data.firstName", contains("David"),
                        "data.lastName", contains("Wrong"))
                .extract().path("data[0].id");

        // Update an author
        Author davidLodge = new Author();
        davidLodge.firstName = "Davide";
        davidLodge.lastName = "Lodge";
        RestAssured.given()
//                .contentType(ContentType.URLENC.withCharset("UTF-8"))
//                .formParam("firstName", "David")
//                .formParam("lastName", "Lodge")
//                .post("/library/author/" + davidLodgeId)
                .pathParam("id", davidLodgeId)
                .contentType(ContentType.JSON).body(davidLodge)
                .put("/library/author/{id}")
                .then()
                .statusCode(200);

/*        RestAssured.when().get("/library/author/search?pattern=dav*").then()
                .statusCode(200)
                .body("firstName", contains("David"),
                        "lastName", contains("Lodge"));*/

        // Add a book
        Author davidLodgeRetrieved = RestAssured.given()
                .pathParam("id", davidLodgeId)
                .get("/library/author/{id}")
                .then()
                .statusCode(200)
                .extract()
                .response().as(Author.class);
        Book book = new Book();
        book.title = "Therapy";
        book.author = davidLodgeRetrieved;
        RestAssured.given()
//                .contentType(ContentType.URLENC.withCharset("UTF-8"))
//                .formParam("title", "Therapy")
//                .formParam("authorId", davidLodgeId)
//                .put("/library/book/")
                .contentType(ContentType.JSON).body(book)
                .post("/library/book/")
                .then()
                .statusCode(201);

        RestAssured
                .given()
                .queryParam("title", "Therapy")
                .when().get("/library/book")
                .then()
                .statusCode(200)
                .body("data.size()", is(1),
                        "meta.count", is(1),
                        "meta.offset", is(0),
                        "meta.limit", is(25),
                        "meta.sort", is("id:Ascending"),
                        "links.size()", is(2),
                        "data.title", contains("Therapy"));

/*
        RestAssured.when().get("/library/author/search?pattern=therapy").then()
                .statusCode(200)
                .body("firstName", contains("David"),
                        "lastName", contains("Lodge"));
*/
        RestAssured.given()
                .pathParam("id", davidLodgeId)
                .get("/library/author/{id}")
                .then()
                .statusCode(200)
                .body("id", is(davidLodgeId),
                        "lastName", is("Lodge"),
                        "books.size()", is(1),
                        "books.title", contains("Therapy"));

    }
}
