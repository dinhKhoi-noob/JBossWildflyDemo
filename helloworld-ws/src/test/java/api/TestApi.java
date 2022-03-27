package api;

import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;

import java.io.File;

import io.restassured.response.Response;


public class TestApi {
    @Test
    void testPing(){
        Response response = get("http://localhost:8080/helloworld-ws/api/file/ping");
        Assert.assertEquals(response.getStatusCode(), 200);
    }

    @Test
    void postInvalidFile(){
        given().multiPart("uploadedFile",new File("src/main/java/com/api/UploadService.java"))
        .when().post("http://localhost:8080/helloworld-ws/api/file/post")
        .then().assertThat().statusCode(400).and().extract().body().asString();
    }

    @Test
    void postValidFile(){
        given().multiPart("uploadedFile",new File("src/test/resources/index.png"))
        .when().post("http://localhost:8080/helloworld-ws/api/file/post")
        .then().assertThat().statusCode(202).and().extract().body().asString();
    }
}