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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskListSeleniumTest extends org.springframework.test.context.testng.AbstractTestNGSpringContextTests {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private TaskListPage taskListPage;
    private String tituloCreadoEnEsteTest;

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
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
        String tituloUnico = "Tarea creada con Selenium " + System.currentTimeMillis();
        tituloCreadoEnEsteTest = tituloUnico;

        taskListPage.abrir(baseUrl);
        taskListPage.clickearNuevaTarea();

        TaskFormPage taskFormPage = new TaskFormPage(driver);
        taskFormPage.escribirTitulo(tituloUnico);
        taskFormPage.escribirDescripcion("Test end-to-end desde la UI");
        taskFormPage.seleccionarEstado("TODO");
        taskFormPage.seleccionarPrioridad("ALTA");
        taskFormPage.clickearGuardar();
        taskListPage.esperarQueLaListaCargue();

        Assert.assertTrue(
                taskListPage.existeTareaConTitulo(tituloUnico),
                "La tarea creada deberia aparecer en la lista"
        );
    }

    @Test
    void editarTareaDesdeLaUiDeberiaActualizarElTitulo() {
        String baseUrl = "http://localhost:" + port;
        String tituloOriginal = "Tarea para editar " + System.currentTimeMillis();
        String tituloNuevo = "Tarea editada con Selenium " + System.currentTimeMillis();
        tituloCreadoEnEsteTest = tituloNuevo;

        taskListPage.abrir(baseUrl);
        taskListPage.clickearNuevaTarea();

        TaskFormPage taskFormPage = new TaskFormPage(driver);
        taskFormPage.escribirTitulo(tituloOriginal);
        taskFormPage.escribirDescripcion("Descripcion original");
        taskFormPage.seleccionarEstado("TODO");
        taskFormPage.seleccionarPrioridad("MEDIA");
        taskFormPage.clickearGuardar();
        taskListPage.esperarQueLaListaCargue();

        taskListPage.clickearEditarPorTitulo(tituloOriginal);
        taskFormPage.esperarQueElFormularioCargue();

        Assert.assertEquals(taskFormPage.leerTitulo(), tituloOriginal,
                "El formulario deberia precargar el titulo existente");

        taskFormPage.limpiarYEscribirTitulo(tituloNuevo);
        taskFormPage.clickearGuardar();
        taskListPage.esperarQueLaListaCargue();

        Assert.assertTrue(taskListPage.existeTareaConTitulo(tituloNuevo),
                "Deberia verse el titulo actualizado en la lista");
        Assert.assertFalse(taskListPage.existeTareaConTitulo(tituloOriginal),
                "El titulo viejo ya no deberia existir en la lista");
    }

    @Test
    void borrarTareaDesdeLaUiDeberiaSacarlaDeLaLista() {
        String baseUrl = "http://localhost:" + port;
        String tituloUnico = "Tarea para borrar con Selenium " + System.currentTimeMillis();

        taskListPage.abrir(baseUrl);
        taskListPage.clickearNuevaTarea();

        TaskFormPage taskFormPage = new TaskFormPage(driver);
        taskFormPage.escribirTitulo(tituloUnico);
        taskFormPage.escribirDescripcion("Se va a eliminar desde la UI");
        taskFormPage.seleccionarEstado("TODO");
        taskFormPage.seleccionarPrioridad("BAJA");
        taskFormPage.clickearGuardar();
        taskListPage.esperarQueLaListaCargue();

        Assert.assertTrue(taskListPage.existeTareaConTitulo(tituloUnico),
                "La tarea deberia existir antes de borrarla");

        taskListPage.clickearBorrarPorTitulo(tituloUnico);
        taskListPage.esperarQueLaListaCargue();

        Assert.assertFalse(taskListPage.existeTareaConTitulo(tituloUnico),
                "La tarea no deberia existir despues de borrarla");
    }

    @AfterMethod
    public void limpiarDatosDeEsteTest() {
        if (tituloCreadoEnEsteTest != null) {
            taskListPage.abrir("http://localhost:" + port);
            taskListPage.borrarTareaSiExiste(tituloCreadoEnEsteTest);
            tituloCreadoEnEsteTest = null;
        }
    }
}