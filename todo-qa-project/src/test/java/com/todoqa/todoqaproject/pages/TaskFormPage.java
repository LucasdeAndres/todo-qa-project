package com.todoqa.todoqaproject.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

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
        driver.findElement(botonGuardar).click();
    }
}