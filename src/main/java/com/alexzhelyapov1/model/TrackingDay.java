package com.alexzhelyapov1.model;

// TrackingDay.java
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class TrackingDay {
    private final ObjectProperty<LocalDate> date;
    private final Map<Project, BooleanProperty> statusMap = new HashMap<>();

    public TrackingDay(LocalDate date) {
        this.date = new SimpleObjectProperty<>(date);
    }

    // Реализация dateProperty
    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    // Реализация statusProperty
    public BooleanProperty statusProperty(Project project) {
        if (!statusMap.containsKey(project)) {
            statusMap.put(project, new SimpleBooleanProperty(false));
        }
        return statusMap.get(project);
    }

    // Обновленные методы для работы с новыми свойствами
    public Boolean getStatus(Project project) {
        return statusProperty(project).get();
    }

    public void setStatus(Project project, Boolean done) {
        statusProperty(project).set(done);
    }

    public LocalDate getDate() {
        return date.get();
    }

    public boolean hasDate(LocalDate date) {
        return this.date.equals(date);
    }
}
