package com.alexzhelyapov1.controllers;

// MainController.java
import com.alexzhelyapov1.dao.ProjectDAO;
import com.alexzhelyapov1.dao.TrackingDAO;
import com.alexzhelyapov1.dao.DatabaseConnection;
import com.alexzhelyapov1.model.Project;
import com.alexzhelyapov1.model.TrackingDay;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;

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
            DatabaseConnection.initializeDatabase();
            loadData();
            setupTableColumns();
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
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

        // Сортируем по дате
        trackingDays.sort(Comparator.comparing(TrackingDay::getDate).reversed());
    }

    private void setupTableColumns() {
        tableView.getColumns().clear();

        // Колонка с датами
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        tableView.getColumns().add(dateColumn);

        for (Project project : projects) {
            TableColumn<TrackingDay, Boolean> col = new TableColumn<>(project.getName());

            // Обработчик клика на заголовок
            col.getStyleClass().add("editable-header");

            // Обработка клика на заголовок
//            col.getStyleableNode().setOnMouseClicked(e -> {
//                if (e.getClickCount() == 1) {
//                    handleEditProject(project);
//                }
//            });

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

    private void handleEditProject(Project project) {
        showAlert("None", "handleEditProject");
//        try {
////            FXMLLoader loader = new FXMLLoader(
////                    getClass().getResource("/fxml/edit-project-dialog.fxml")
////            );
//            FXMLLoader loader = new FXMLLoader();
//            loader.setLocation(getClass().getResource("/fxml/add-project-dialog.fxml"));
//
//            Dialog<ButtonType> dialog = new Dialog<>();
//            dialog.setDialogPane(loader.load());
//            dialog.setTitle("Редактирование проекта");
//
//            EditProjectDialogController controller = loader.getController();
//            controller.setProject(project);
//
//            Optional<ButtonType> result = dialog.showAndWait();
//            if (result.isPresent() && result.get() == ButtonType.OK) {
//                if (controller.isInputValid()) {
//                    controller.updateProject();
//                    projectDAO.updateProject(project);
//                    refreshProjects();
//                } else {
//                    showAlert("Ошибка", "Название проекта не может быть пустым");
//                }
//            }
//        } catch (IOException | SQLException e) {
//            showError("Ошибка редактирования: " + e.getMessage());
//        }
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

    @FXML
    private void handleAddDay() {
        try {
            LocalDate today = LocalDate.now();

            if (trackingDays.stream().anyMatch(day -> day.getDate().equals(today))) {
                showAlert("День уже существует", "Сегодняшняя дата уже есть в трекере");
                return;
            }

            TrackingDay newDay = new TrackingDay(today);
            trackingDAO.addTrackingDay(newDay, projects);
            trackingDays.add(newDay);
            trackingDays.sort(Comparator.comparing(TrackingDay::getDate).reversed());

            // Обновляем таблицу и скроллим к новой записи
            tableView.refresh();

            Platform.runLater(() -> {
                tableView.scrollTo(newDay);
                tableView.getSelectionModel().select(newDay);

                // Дополнительная стилизация при необходимости
                tableView.lookupAll(".table-row-cell")
                        .forEach(node -> node.setStyle(""));
                tableView.lookup(".table-row-cell:selected")
                        .setStyle("-fx-background-color: lightblue;");
            });

        } catch (SQLException e) {
            showError("Ошибка при добавлении дня: " + e.getMessage());
        }
    }



    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}