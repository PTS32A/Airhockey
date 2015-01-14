/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Server.Startup;

import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import s32a.Server.AirhockeyServer;

/**
 *
 * @author Kargathia
 */
public class ServerMain extends Application {

    private Stage stage;
    private ServerInfo serverInfo;
    private FTPHandler handler = null;
    private AirhockeyServer server = null;

    @Override
    public void start(Stage primaryStage) {

        this.stage = primaryStage;
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                try {
//                    stage = new Stage();
                    GridPane gp = new GridPane();
                    gp.setAlignment(Pos.CENTER);
                    gp.setHgap(10);
                    gp.setVgap(10);
                    gp.setPadding(new Insets(25, 25, 25, 25));
                    Label ip = new Label("FTP website:");
                    gp.add(ip, 0, 1);
                    TextField tfFTPAddress = new TextField();
                    tfFTPAddress.setText("athena.fhict.nl");
                    gp.add(tfFTPAddress, 1, 1);
                    CheckBox cbxSSL = new CheckBox("SSL");
                    cbxSSL.setSelected(true);
                    gp.add(cbxSSL, 1, 2);
                    Label user = new Label("Username:");
                    gp.add(user, 0, 3);
                    TextField tfUser = new TextField();
                    tfUser.setText("i293443");
                    gp.add(tfUser, 1, 3);
                    Label pass = new Label("Password:");
                    gp.add(pass, 0, 4);
                    PasswordField tfPass = new PasswordField();
                    tfPass.setText("ifvr2edfh101");
                    gp.add(tfPass, 1, 4);
                    Label port = new Label("Status:");
                    gp.add(port, 0, 5);
                    TextField tfPort = new TextField();
                    tfPort.setText("Not Connected");
                    gp.add(tfPort, 1, 5);
                    Button btnConfirm = new Button("Connect");
                    gp.add(btnConfirm, 1, 6);
                    btnConfirm.setOnAction(new EventHandler<ActionEvent>() {

                        @Override
                        public void handle(ActionEvent e) {
                            String user = tfUser.getText();
                            String pass = tfPass.getText();
                            String server = tfFTPAddress.getText();
                            boolean SSL = cbxSSL.isSelected();
                            handler = new FTPHandler(server, user, pass, SSL);
                            try {
                                boolean login = handler.checkLogin();
                                if (login) {
                                    stage.close();
                                    serverSetUp();
                                } else {
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
                                btnConfirm.fire();
                            }
                        }
                    });

                    stage.show();
                } catch (Exception ex) {
                    //showDialog("Error", "Could not open game: " + ex.getMessage());
                    System.out.println(ex.toString());
                }
            }
        });
    }

    private void serverSetUp() throws IOException {
//        Platform.runLater(new Runnable() {
//
//            @Override
//            public void run() {
                try {
//                    stage = new Stage();
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
                    tfIP.setPromptText("0.0.0.0");
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
                    Button btnConfirm = new Button("Start Server");
                    gp.add(btnConfirm, 1, 6);
                    btnConfirm.setOnAction(new EventHandler<ActionEvent>() {

                        @Override
                        public void handle(ActionEvent e) {
                            System.out.println("Starting Server");
                            if (serverInfo(tfSN.getText(), tfBind.getText(),
                                    tfIP.getText(), tfPort.getText(), tfDesc.getText())) {
//                                stage.hide();
                                server = new AirhockeyServer(
                                                stage, 
                                                serverInfo.getIP(), 
                                                serverInfo.getBindingName(), 
                                                serverInfo.getPort());
                            } else {
                                System.out.println("Error occured, unable to open server");
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
                                btnConfirm.fire();
                            }
                        }
                    });

                    stage.show();
                } catch (Exception ex) {
                    //showDialog("Error", "Could not open game: " + ex.getMessage());
                    System.out.println(ex.toString());
                }
            }
//        });
//    }

    public boolean serverInfo(String address, String bind, String ip, String port, String description) {
        try {
            // sets security manager
            System.setProperty("java.security.policy", "rmi.policy");
            System.setSecurityManager(new SecurityManager());
            
            // sets up server info
            this.serverInfo = new ServerInfo(address, description, bind, ip, Integer.valueOf(port));
            if (this.handler == null) {
                return false;
            }
            String codebase = this.handler.registerServer(serverInfo);
            // sets codebase property
            System.setProperty("java.rmi.server.codebase", codebase);
            return true;
        } catch (Exception ex) {
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
