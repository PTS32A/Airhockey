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
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import static javafx.scene.layout.AnchorPane.setBottomAnchor;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import s32a.Client.GUI.AirhockeyGUI;
import s32a.Server.AirhockeyServer;

/**
 *
 * @author Kargathia
 */
public class AirhockeyApplication extends Application {

    private Stage stage;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        AnchorPane anchor = new AnchorPane();

        // Label
        Label lblWelcome = new Label("Welcome to Airhockey");
        lblWelcome.setFont(new Font(24));
        AnchorPane.setTopAnchor(lblWelcome, 20.0);
        AnchorPane.setLeftAnchor(lblWelcome, 20.0);
        AnchorPane.setRightAnchor(lblWelcome, 20.0);
        anchor.getChildren().add(lblWelcome);
        
        // Server button
        Button btnServer = new Button("Server");
        btnServer.setMinWidth(80.0);
        btnServer.setMinHeight(80.0);
        AnchorPane.setTopAnchor(btnServer, 80.0);
        AnchorPane.setLeftAnchor(btnServer, 50.0);
        AnchorPane.setBottomAnchor(btnServer, 40.0);
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
        btnClient.setMinHeight(80.0);
        btnClient.setMinWidth(80.0);
        AnchorPane.setTopAnchor(btnClient, 80.0);
        AnchorPane.setLeftAnchor(btnClient, 150.0);
        AnchorPane.setBottomAnchor(btnClient, 40.0);
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
        Scene scene = new Scene(root);
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
