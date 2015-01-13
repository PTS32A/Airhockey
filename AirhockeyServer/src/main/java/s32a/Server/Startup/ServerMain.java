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
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
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
    private ServerInfo serverInfo;
    private FTPHandler handler;

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
                    Label ip = new Label("FTP website:");
                    gp.add(ip, 0, 1);
                    TextField tfIp = new TextField();
                    tfIp.setText("athena.fhict.nl");
                    gp.add(tfIp, 1, 1);
                    Label user = new Label("Username:");
                    gp.add(user, 0, 2);
                    TextField tfUser = new TextField();
                    tfUser.setText("i293443");
                    gp.add(tfUser, 1, 2);
                    Label pass = new Label("Password:");
                    gp.add(pass, 0, 3);
                    PasswordField tfPass = new PasswordField();
                    tfPass.setText("ifvr2edfh101");
                    gp.add(tfPass, 1, 3);
                    Label port = new Label("Status:");
                    gp.add(port, 0, 4);
                    TextField tfPort = new TextField();
                    tfPort.setText("Not Connected");
                    gp.add(tfPort, 1, 4);
                    Button confirm = new Button("Connect");
                    gp.add(confirm, 1, 5);
                    confirm.setOnAction(new EventHandler<ActionEvent>() {

                        @Override
                        public void handle(ActionEvent e) {
                            FTPClient client = new FTPSClient(false);
                            try {
                                String user = tfUser.getText();
                                String pass = tfPass.getText();
                                client.connect(tfIp.getText());
                                boolean login = client.login(user, pass);
                                if (login) {
                                    client.logout();
                                    stage.close();
                                    serverSetUp();
                                }
                                else{
                                    tfPort.setText("Could not connect, check spelling and internet connection.");
                                }
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                            
                        }
                    });

                    Group root = new Group();
                    Scene scene = new Scene(root, 300, 250);
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
                    System.out.println(ex.toString());
                }
            }
       });
    }
    
    private void serverSetUp() throws IOException
    {
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
                    Label serverName = new Label("Server name:");
                    gp.add(serverName, 0, 1);
                    TextField tfSN = new TextField();
                    tfSN.setText("Airhockey");
                    gp.add(tfSN, 1, 1);
                    Label bind = new Label("Binding Name:");
                    gp.add(bind, 0, 2);
                    TextField tfBind = new TextField();
                    tfBind.setText("AirhockeyServer");
                    gp.add(tfBind, 1, 2);
                    Label ip = new Label("IP Adress:");
                    gp.add(ip, 0, 3);
                    TextField tfIP = new TextField();
                    tfIP.setText("0.0.0.0");
                    gp.add(tfIP, 1, 3);
                    Label port = new Label("Port:");
                    gp.add(port, 0, 4);
                    TextField tfPort = new TextField();
                    tfPort.setText("1099");
                    gp.add(tfPort, 1, 4);
                    Label desc = new Label("Description");
                    gp.add(desc, 0, 5);
                    TextField tfDesc = new TextField();
                    tfDesc.setText("");
                    gp.add(tfDesc, 1, 5);
                    Button confirm = new Button("Start Server");
                    gp.add(confirm, 1, 6);
                    confirm.setOnAction(new EventHandler<ActionEvent>() {

                        @Override
                        public void handle(ActionEvent e) {
                            if(serverInfo(tfSN.getText(), tfBind.getText(),
                                    tfIP.getText(), tfPort.getText(), tfDesc.getText())){
                                //Todo
                                stage.close();
                            }
                            else{
                                //Error
                            }
                        }
                    });

                    Group root = new Group();
                    Scene scene = new Scene(root, 300, 300);
                    root.getChildren().add(gp);
                    stage.setScene(scene);
                    stage.setTitle("Start New Server");

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
                    System.out.println(ex.toString());
                }
            }
       });
    }
    
    public boolean serverInfo(String name, String bind, String ip, String port, String description)
    {
        try{
            this.serverInfo = new ServerInfo(name, description, bind, ip, Integer.valueOf(port));
            return true;
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    

}
