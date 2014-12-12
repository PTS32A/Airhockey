/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Main;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import static javafx.scene.layout.AnchorPane.setBottomAnchor;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import s32a.Client.GUI.AirhockeyGUI;
import s32a.Server.AirhockeyServer;

/**
 *
 * @author Kargathia
 */
public class AirhockeyApplication extends Application {

    private static String type;
    private Stage stage;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        AnchorPane anchor = new AnchorPane();
        
        // Server button
        Button btnServer = new Button("Server");
        AnchorPane.setTopAnchor(btnServer, 50.0);
        AnchorPane.setLeftAnchor(btnServer, 100.0);
        anchor.getChildren().add(btnServer);
        btnServer.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                stage.hide();
                AirhockeyServer server = new AirhockeyServer(stage);
            }
        });

        // Client button
        Button btnClient = new Button("Client");
        AnchorPane.setTopAnchor(btnClient, 50.0);
        AnchorPane.setLeftAnchor(btnClient, 200.0);
        anchor.getChildren().add(btnClient);
        btnClient.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                stage.hide();
                AirhockeyGUI gui = new AirhockeyGUI();
                gui.startGUI(stage);
            }
        });

        // shows the lot
        Group root = new Group();
        Scene scene = new Scene(root, 300, 300);
        root.getChildren().add(anchor);
        stage.setScene(scene);
        stage.setTitle("Airhockey");
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
