package com.todoqa.todoqaproject.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class TaskFormPage {

    private final WebDriver driver;

    private final By campoTitulo = By.id("titulo");
    private final By campoDescripcion = By.id("descripcion");
    private final By selectEstado = By.id("estado");
    private final By selectPrioridad = By.id("prioridad");
    private final By campoFechaLimite = By.id("fechaLimite");
    private final By botonGuardar = By.id("task-submit-btn");

    public TaskFormPage(WebDriver driver) {
        this.driver = driver;
    }

    public void escribirTitulo(String titulo) {
        driver.findElement(campoTitulo).sendKeys(titulo);
    }

    public void escribirDescripcion(String descripcion) {
        driver.findElement(campoDescripcion).sendKeys(descripcion);
    }

    public void seleccionarEstado(String estado) {
        new Select(driver.findElement(selectEstado)).selectByValue(estado);
    }

    public void seleccionarPrioridad(String prioridad) {
        new Select(driver.findElement(selectPrioridad)).selectByValue(prioridad);
    }

    public void clickearGuardar() {
        clickearConScrollYJavaScript(driver.findElement(botonGuardar));
    }

    public String leerTitulo() {
        return driver.findElement(campoTitulo).getAttribute("value");
    }

    public void limpiarYEscribirTitulo(String nuevoTitulo) {
        WebElement campo = driver.findElement(campoTitulo);
        campo.clear();
        campo.sendKeys(nuevoTitulo);
    }

    public void esperarQueElFormularioCargue() {
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(campoTitulo));
    }
    private void clickearConScrollYJavaScript(WebElement elemento) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});", elemento);

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.elementToBeClickable(elemento));

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", elemento);
    }
}