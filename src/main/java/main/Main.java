package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {

    private BorderPane root = new BorderPane();
    private VBox menuBox; // main menu container

    @Override
    public void start(Stage stage) throws Exception {
        // Root gradient background
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #2b0000, #1a1a1a);");

        menuBox = new VBox(30);
        menuBox.setStyle("-fx-padding: 60; -fx-alignment: center;");

        Text title = new Text("Sorting Visualizer");
        title.setFill(Color.SILVER);
        title.setFont(Font.font("Segoe UI", 28));

        // ComboBox for selecting sort
        ComboBox<String> sortSelector = new ComboBox<>();
        sortSelector.getItems().addAll(
                "Bubble Sort",
                "Selection Sort",
                "Insertion Sort"

        );
        sortSelector.setValue("Bubble Sort");
        sortSelector.setStyle(
                "-fx-background-color: #1a1a1a; " +
                        "-fx-border-color: #555555; " +
                        "-fx-border-radius: 6; " +
                        "-fx-text-fill: silver; " +
                        "-fx-font-size: 16;"
        );

        // Start button
        Button startBtn = new Button("Start Visualization");
        startBtn.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #e0e0e0, #a0a0a0);" +
                        "-fx-text-fill: #000000;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 16px;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-border-color: #999999;" +
                        "-fx-border-width: 2px;" +
                        "-fx-padding: 10 20 10 20;" +
                        "-fx-effect: dropshadow( gaussian , rgba(0,0,0,0.4) , 4, 0, 0, 2 );"
        );

        menuBox.getChildren().addAll(title, sortSelector, startBtn);
        root.setCenter(menuBox);

        // --- Start Visualization Action ---
        startBtn.setOnAction(e -> {
            String selectedSort = sortSelector.getValue();
            try {
                FXMLLoader loader = null;
                switch (selectedSort) {
                    case "Bubble Sort":
                        loader = new FXMLLoader(getClass().getResource("/bubble.fxml"));
                        break;
                    case "Selection Sort":
                        loader = new FXMLLoader(getClass().getResource("/selection.fxml"));
                        break;
                    case "Insertion Sort":
                        loader = new FXMLLoader(getClass().getResource("/insert.fxml"));
                        break;
                    default:
                        System.out.println("Sort not implemented yet!");
                        return;
                }

                // Load the FXML for the selected sort
                root.setCenter(loader.load());

                // Pass reference of main.java.org.example.Main to the visualizer controller
                Object controller = loader.getController();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.setTitle("Sorting Visualizer");
        stage.show();
    }}
