package com.todoqa.todoqaproject.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

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
        driver.findElement(botonNuevaTarea).click();
    }
}