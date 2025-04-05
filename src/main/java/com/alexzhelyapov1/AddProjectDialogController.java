package com.alexzhelyapov1;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class AddProjectDialogController {
    @FXML private TextField nameField;
    @FXML private TextField tagsField;
    @FXML private DatePicker deadlinePicker;

    public Project getProject() {
        if (isInputValid()) {
            return new Project(
                    nameField.getText().trim(),
                    tagsField.getText().trim(),
                    deadlinePicker.getValue()
            );
        }
        return null;
    }

    private boolean isInputValid() {
        if (nameField.getText().trim().isEmpty()) {
            nameField.requestFocus();
            return false;
        }
        return true;
    }
}