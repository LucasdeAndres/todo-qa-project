package com.todoqa.todoqaproject.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;

public class TaskListPage {

    private final WebDriver driver;

    private final By tabla = By.id("task-table");
    private final By filas = By.cssSelector("#task-table tbody tr");
    private final By botonNuevaTarea = By.id("new-task-btn");
    private final By titulosDeTareas = By.className("task-titulo");

    public TaskListPage(WebDriver driver) {
        this.driver = driver;
    }

    public void abrir(String baseUrl) {
        driver.get(baseUrl + "/ui/tasks");
    }

    public boolean estaLaTablaVisible() {
        return driver.findElement(tabla).isDisplayed();
    }

    public int cantidadDeTareas() {
        return driver.findElements(filas).size();
    }

    public boolean existeTareaConTitulo(String titulo) {
        List<WebElement> titulos = driver.findElements(titulosDeTareas);
        return titulos.stream().anyMatch(el -> el.getText().equals(titulo));
    }

    public void clickearNuevaTarea() {
        clickearConScrollYJavaScript(driver.findElement(botonNuevaTarea));
    }

    public void clickearEditarPorTitulo(String titulo) {
        WebElement fila = buscarFilaPorTitulo(titulo);
        clickearConScrollYJavaScript(fila.findElement(By.className("task-edit-btn")));
    }

    public void clickearBorrarPorTitulo(String titulo) {
        WebElement fila = buscarFilaPorTitulo(titulo);
        clickearConScrollYJavaScript(fila.findElement(By.cssSelector(".task-delete-btn")));
    }

    private WebElement buscarFilaPorTitulo(String titulo) {
        List<WebElement> todasLasFilas = driver.findElements(filas);
        return todasLasFilas.stream()
                .filter(fila -> fila.findElement(By.className("task-titulo")).getText().equals(titulo))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No se encontro una fila con titulo: " + titulo));
    }

    public void esperarQueLaListaCargue() {
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(tabla));
    }

    public void borrarTareaSiExiste(String titulo) {
        if (existeTareaConTitulo(titulo)) {
            clickearBorrarPorTitulo(titulo);
            esperarQueLaListaCargue();
        }
    }

    private void clickearConScrollYJavaScript(WebElement elemento) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});", elemento);

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.elementToBeClickable(elemento));

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", elemento);
    }
}