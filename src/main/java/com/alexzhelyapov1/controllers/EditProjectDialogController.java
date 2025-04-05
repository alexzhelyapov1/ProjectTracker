package com.alexzhelyapov1.controllers;

import com.alexzhelyapov1.model.Project;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditProjectDialogController {
    @FXML private TextField nameField;
    @FXML private TextField tagsField;
    @FXML private DatePicker deadlinePicker;
    private boolean deleteRequested = false;

    @FXML
    public void handleDelete() {
        deleteRequested = true;
        ((Stage) nameField.getScene().getWindow()).close();
    }

    public boolean isDeleteRequested() {
        return deleteRequested;
    }

    private Project project;

    public void setProject(Project project) {
        this.project = project;
        nameField.setText(project.getName());
        tagsField.setText(project.getTags());
        deadlinePicker.setValue(project.getDeadline());
    }

    public void updateProject() {
        project.setName(nameField.getText().trim());
        project.setTags(tagsField.getText().trim());
        project.setDeadline(deadlinePicker.getValue());
    }

    public boolean isInputValid() {
        return !nameField.getText().trim().isEmpty();
    }
}
