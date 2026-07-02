package com.todoqa.todoqaproject;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerRestAssuredTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    // ---------- GET /tasks ----------

    @Test
    void getAllTasks_deberiaDevolver200() {
        given()
                .when()
                .get("/tasks")
                .then()
                .statusCode(200);
    }

    // ---------- GET /tasks/{id} ----------

    @Test
    void getTaskById_existente_deberiaDevolver200YBodyCorrecto() {
        String body = """
            {
                "titulo": "Comprar pan",
                "descripcion": "Ir a la panaderia",
                "estado": "TODO"
            }
            """;

        int id = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/tasks")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .when()
                .get("/tasks/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("titulo", equalTo("Comprar pan"))
                .body("estado", equalTo("TODO"));
    }

    @Test
    void getTaskById_inexistente_deberiaDevolver404() {
        given()
                .when()
                .get("/tasks/999999")
                .then()
                .statusCode(404);
    }

    // ---------- POST /tasks ----------

    @Test
    void createTask_valido_deberiaDevolver201() {
        String body = """
            {
                "titulo": "Estudiar RestAssured",
                "descripcion": "Practicar tests de API",
                "estado": "TODO"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/tasks")
                .then()
                .statusCode(201)
                .body("titulo", equalTo("Estudiar RestAssured"))
                .body("id", notNullValue());
    }

    @Test
    void createTask_sinTitulo_deberiaDevolver400() {
        String body = """
            {
                "titulo": "",
                "descripcion": "Sin titulo",
                "estado": "TODO"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/tasks")
                .then()
                .statusCode(400);
    }

    @Test
    void createTask_estadoInvalido_deberiaDevolver400() {
        String body = """
            {
                "titulo": "Tarea con estado invalido",
                "descripcion": "Test de validacion",
                "estado": "TERMINADO"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/tasks")
                .then()
                .statusCode(400);
    }

    @Test
    void createTask_descripcionMuyLarga_deberiaDevolver400() {
        String descripcionLarga = "a".repeat(701);

        String body = """
            {
                "titulo": "Tarea con descripcion larga",
                "descripcion": "%s",
                "estado": "TODO"
            }
            """.formatted(descripcionLarga);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/tasks")
                .then()
                .statusCode(400);
    }

    // ---------- PUT /tasks/{id} ----------

    @Test
    void updateTask_existente_deberiaDevolver200() {
        String createBody = """
            {
                "titulo": "Tarea original",
                "descripcion": "Antes de editar",
                "estado": "TODO"
            }
            """;

        int id = given()
                .contentType(ContentType.JSON)
                .body(createBody)
                .when()
                .post("/tasks")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        String updateBody = """
            {
                "titulo": "Tarea editada",
                "descripcion": "Despues de editar",
                "estado": "IN_PROGRESS"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(updateBody)
                .when()
                .put("/tasks/" + id)
                .then()
                .statusCode(200)
                .body("titulo", equalTo("Tarea editada"))
                .body("estado", equalTo("IN_PROGRESS"));
    }

    @Test
    void updateTask_inexistente_deberiaDevolver404() {
        String body = """
            {
                "titulo": "No existe",
                "descripcion": "No deberia actualizarse",
                "estado": "TODO"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .put("/tasks/999999")
                .then()
                .statusCode(404);
    }

    // ---------- DELETE /tasks/{id} ----------

    @Test
    void deleteTask_existente_deberiaDevolver204YLuego404() {
        String createBody = """
            {
                "titulo": "Tarea para borrar",
                "descripcion": "Se va a eliminar",
                "estado": "TODO"
            }
            """;

        int id = given()
                .contentType(ContentType.JSON)
                .body(createBody)
                .when()
                .post("/tasks")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .when()
                .delete("/tasks/" + id)
                .then()
                .statusCode(204);

        given()
                .when()
                .get("/tasks/" + id)
                .then()
                .statusCode(404);
    }

    @Test
    void deleteTask_inexistente_deberiaDevolver404() {
        given()
                .when()
                .delete("/tasks/999999")
                .then()
                .statusCode(404);
    }
}