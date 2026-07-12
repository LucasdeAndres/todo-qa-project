package com.todoqa.todoqaproject.selenium;

import com.todoqa.todoqaproject.pages.TaskFormPage;
import com.todoqa.todoqaproject.pages.TaskListPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskListSeleniumTest extends org.springframework.test.context.testng.AbstractTestNGSpringContextTests {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private TaskListPage taskListPage;

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);
        taskListPage = new TaskListPage(driver);
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void laListaDeTareasDeberiaCargarCorrectamente() {
        String baseUrl = "http://localhost:" + port;

        taskListPage.abrir(baseUrl);

        Assert.assertTrue(taskListPage.estaLaTablaVisible(), "La tabla de tareas deberia estar visible");
    }
    @Test
    void clickearNuevaTareaDeberiaAbrirElFormulario() {
        String baseUrl = "http://localhost:" + port;

        taskListPage.abrir(baseUrl);
        taskListPage.clickearNuevaTarea();

        Assert.assertTrue(driver.getCurrentUrl().endsWith("/ui/tasks/new"),
                "Deberia haber navegado al formulario de nueva tarea");
    }

    @Test
    void crearTareaDesdeLaUiDeberiaAparecerEnLaLista() {
        String baseUrl = "http://localhost:" + port;

        taskListPage.abrir(baseUrl);
        taskListPage.clickearNuevaTarea();

        TaskFormPage taskFormPage = new TaskFormPage(driver);
        taskFormPage.escribirTitulo("Tarea creada con Selenium");
        taskFormPage.escribirDescripcion("Test end-to-end desde la UI");
        taskFormPage.seleccionarEstado("TODO");
        taskFormPage.seleccionarPrioridad("ALTA");
        taskFormPage.clickearGuardar();

        Assert.assertTrue(
                taskListPage.existeTareaConTitulo("Tarea creada con Selenium"),
                "La tarea creada deberia aparecer en la lista"
        );
    }
}