package com.alexzhelyapov1.model;

// Project.java
import java.time.LocalDate;

public class Project {
    private int id;
    private String name;
    private String tags;
    private LocalDate deadline;

    // Конструкторы, геттеры и сеттеры
    public Project() {}

    public Project(String name, String tags, LocalDate deadline) {
        this.name = name;
        this.tags = tags;
        this.deadline = deadline;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }
}
