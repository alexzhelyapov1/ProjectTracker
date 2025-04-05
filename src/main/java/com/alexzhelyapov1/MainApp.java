package com.alexzhelyapov1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
//        Parent root = FXMLLoader.load(MainApp.class.getResource("/fxml/main.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/fxml/main.fxml"));
        primaryStage.setTitle("Project Tracker");
        primaryStage.setScene(new Scene(fxmlLoader.load(), 800, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
