package stellarburgers.client;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class BaseClient {

    private static final String BASE_URL = "https://qa-stellarburgers.education-services.ru";

    protected RequestSpecification spec() {
        return given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON);
    }
}
