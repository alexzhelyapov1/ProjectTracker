package com.alexzhelyapov1;

// MainController.java
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import java.io.Console;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class MainController implements Initializable {
    @FXML private TableView<TrackingDay> tableView;
    @FXML private TableColumn<TrackingDay, LocalDate> dateColumn;

    private ObservableList<Project> projects = FXCollections.observableArrayList();
    private ObservableList<TrackingDay> trackingDays = FXCollections.observableArrayList();
    private ProjectDAO projectDAO = new ProjectDAO();
    private TrackingDAO trackingDAO = new TrackingDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            initializeDatabase();
            loadData();
            setupTableColumns();
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        }
    }

    private void initializeDatabase() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS projects (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "tags TEXT," +
                    "deadline DATE)");

            stmt.execute("CREATE TABLE IF NOT EXISTS tracking (" +
                    "project_id INTEGER," +
                    "date DATE," +
                    "done BOOLEAN," +
                    "PRIMARY KEY (project_id, date)," +
                    "FOREIGN KEY (project_id) REFERENCES projects(id))");
        }
    }

    private void loadData() throws SQLException {
        projects.setAll(projectDAO.getAllProjects());
        loadTrackingDays();
    }

    private void loadTrackingDays() throws SQLException {
        trackingDays.clear();
        Map<LocalDate, Map<Integer, Boolean>> allStatuses = trackingDAO.getAllStatuses();

        for (LocalDate date : allStatuses.keySet()) {
            TrackingDay day = new TrackingDay(date);
            Map<Integer, Boolean> projectStatuses = allStatuses.get(date);

            for (Project project : projects) {
                Boolean done = projectStatuses.getOrDefault(project.getId(), false);
                day.setStatus(project, done);
            }
            trackingDays.add(day);
        }
    }

    private void setupTableColumns() {
        tableView.getColumns().clear();
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        tableView.getColumns().add(dateColumn);

        for (Project project : projects) {
            TableColumn<TrackingDay, Boolean> col = new TableColumn<>(project.getName());

            col.setCellValueFactory(cellData -> {
                TrackingDay day = cellData.getValue();
                return day.statusProperty(project);
            });

            col.setCellFactory(CheckBoxTableCell.forTableColumn(col));

            col.setOnEditCommit(event -> {
                try {
                    TrackingDay day = event.getRowValue();
                    boolean newValue = event.getNewValue();
                    day.setStatus(project, newValue);
                    trackingDAO.updateStatus(project.getId(), day.getDate(), newValue);
                } catch (SQLException e) {
                    showError("Error saving status: " + e.getMessage());
                }
            });

            tableView.getColumns().add(col);
        }

        tableView.setItems(trackingDays);
    }

    @FXML
    private void handleAddProject() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/add-project-dialog.fxml"));

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(loader.load());
            dialog.setTitle("Новый проект");

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                AddProjectDialogController controller = loader.getController();
                Project project = new Project(
                        controller.getName(),
                        controller.getTags(),
                        controller.getDeadline()
                );
                projectDAO.addProject(project);
                refreshProjects();
            }
        } catch (IOException | SQLException e) {
            showError("Ошибка: " + e.getMessage());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void refreshProjects() throws SQLException {
        projects.setAll(projectDAO.getAllProjects());
        setupTableColumns();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}