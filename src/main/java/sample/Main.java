package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root;
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/sample.fxml"));
        root = fxmlLoader.load();

        primaryStage.setTitle("SNMP Scanner");
        primaryStage.setScene(new Scene(root, 520, 430));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
