package com.alexzhelyapov1.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class AddProjectDialogController {
    @FXML private TextField nameField;
    @FXML private TextField tagsField;
    @FXML private DatePicker deadlinePicker;

    public String getName() {
        return nameField.getText().trim();
    }

    public String getTags() {
        return tagsField.getText().trim();
    }

    public LocalDate getDeadline() {
        return deadlinePicker.getValue();
    }
}