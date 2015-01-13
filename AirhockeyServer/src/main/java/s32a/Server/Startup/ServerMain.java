/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Server.Startup;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPSClient;
import s32a.Server.AirhockeyServer;

/**
 *
 * @author Kargathia
 */
public class ServerMain extends Application{

    
    private Stage stage;

    @Override
    public void start(Stage primaryStage) {

        this.stage = primaryStage;
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                try {
                    Stage stage = new Stage();
                    GridPane gp = new GridPane();
                    gp.setAlignment(Pos.CENTER);
                    gp.setHgap(10);
                    gp.setVgap(10);
                    gp.setPadding(new Insets(25, 25, 25, 25));
                    Label ip = new Label(":");
                    gp.add(ip, 0, 1);
                    TextField tfIp = new TextField();
                    tfIp.setText("athena.fhict.nl");
                    gp.add(tfIp, 1, 1);
                    Label port = new Label("Port:");
                    gp.add(port, 0, 2);
                    TextField tfPort = new TextField();
                    tfPort.setText("1099");
                    gp.add(tfPort, 1, 2);
                    Button confirm = new Button("Confirm");
                    gp.add(confirm, 1, 3);
                    confirm.setOnAction(new EventHandler<ActionEvent>() {

                        @Override
                        public void handle(ActionEvent e) {
                            FTPClient client = new FTPSClient(false);
                            try {
                                client.connect(tfIp.getText());
                                boolean login = client.login("i293443", "ifvr2edfh101");
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                            
                        }
                    });

                    Group root = new Group();
                    Scene scene = new Scene(root, 300, 150);
                    root.getChildren().add(gp);
                    stage.setScene(scene);
                    stage.setTitle("FTP Server");

                    stage.getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {

                        @Override
                        public void handle(KeyEvent ke) {
                            if (ke.getCode() == KeyCode.ENTER) {
                                confirm.fire();
                            }
                        }
                    });

                    stage.show();
                }
                catch (Exception ex) {
                    //showDialog("Error", "Could not open game: " + ex.getMessage());
                }
            }
       });
       while (stage.isShowing()) {

       }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
    }

    

}
