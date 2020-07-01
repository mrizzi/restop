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

public class OpenApiTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .setArchiveProducer(() -> 
                    ShrinkWrap.create(JavaArchive.class)
                    .addClasses(Fruit.class, FruitResource.class)
                    .addAsResource("application_openapi.properties", "application.properties")
                    .addAsManifestResource("microprofile-config.properties")
            );

    @Test
    public void test() {
        Response response = RestAssured
                .given()
                .accept(ContentType.JSON)
                .when().get("/openapi")
                .then()
                .statusCode(200).extract().response();
//                .body("openapi", is("3.0.1"));
        System.out.println("OpenAPI: " + response.asString());
    }
}
