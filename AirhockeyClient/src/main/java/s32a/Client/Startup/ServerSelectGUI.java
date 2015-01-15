/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Client.Startup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import s32a.Client.GUI.AirhockeyGUI;
import s32a.Shared.IServerInfo;
import s32a.Shared.ServerInfo;

/**
 *
 * @author Kargathia
 */
public class ServerSelectGUI {

    private final Stage stage;
    private final AirhockeyGUI gui;
    private FTPHandler handler;

    private ComboBox cmbxServers;
    private TextArea taServerDescription;

    public ServerSelectGUI(Stage stage, AirhockeyGUI gui) {
        this.stage = stage;
        this.gui = gui;

        this.displayFTPLogin();
    }

    /**
     * Shows a screen for logging in to the central FTP server
     */
    private void displayFTPLogin() {
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
        
        CheckBox cbxAnyConnect = new CheckBox("AnyConnect running");
        gp.add(cbxAnyConnect, 1, 5);

        Label port = new Label("Status:");
        gp.add(port, 0, 6);
        TextField tfPort = new TextField();
        tfPort.setText("Not Connected");
        gp.add(tfPort, 1, 6);

        Button btnConfirm = new Button("Connect");
        gp.add(btnConfirm, 1, 7);
        btnConfirm.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                String user = tfUser.getText();
                String pass = tfPass.getText();
                String server = tfFTPAddress.getText();
                boolean SSL = cbxSSL.isSelected();
                handler = new FTPHandler(server, user, pass, SSL);
                boolean login = handler.checkLogin();
                if (login) {
                    stage.hide();
                    displayServers(handler.getFTPData());

                    // sets codebase property
                    System.setProperty("java.rmi.server.codebase", handler.getCodebaseURL());
                    
                    // sets hostname
                    if(cbxAnyConnect.isSelected()){
                        System.setProperty("java.rmi.server.hostname", "127.0.0.1");
                    }
        
                } else {
                    tfPort.setText("Could not connect, check spelling and internet connection.");
                }
            }

        });

        Group root = new Group();
        Scene scene = new Scene(root, 300, 300);
        root.getChildren().add(gp);
        stage.setScene(scene);
        stage.setTitle("Central Server Login");

        stage.getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.ENTER) {
                    btnConfirm.fire();
                }
            }
        });

        stage.show();
    }

    /**
     * Displays given servers in GUI. Starts client on button click.
     * @param serverInput 
     */
    private void displayServers(List<ServerInfo> serverInput) {
        if (serverInput == null) {
            serverInput = new ArrayList<>();
        }
        ObservableList<ServerInfo> servers
                = FXCollections.observableArrayList(serverInput);

        try {
            GridPane gp = new GridPane();
            gp.setAlignment(Pos.CENTER);
            gp.setHgap(10);
            gp.setVgap(10);
            gp.setPadding(new Insets(25, 25, 25, 25));
            Label ip = new Label("Servers:");
            gp.add(ip, 0, 1);

            // Adds combobox for displaying servers
            // Includes cell factory to support a combobox of custom objects
            cmbxServers = new ComboBox(servers);
            cmbxServers.setCellFactory(new Callback<ListView<ServerInfo>, ListCell<ServerInfo>>() {

                @Override
                public ListCell<ServerInfo> call(ListView<ServerInfo> p) {
                    final ListCell<ServerInfo> cell = new ListCell<ServerInfo>() {

                        @Override
                        protected void updateItem(ServerInfo server, boolean bln) {
                            super.updateItem(server, bln);

                            if (server != null) {
                                setText(server.getName());
                            } else {
                                setText(null);
                            }
                        }
                    };
                    return cell;
                }
            });
            cmbxServers.setPromptText("Choose a server");
            cmbxServers.valueProperty().addListener(new ChangeListener<ServerInfo>() {
                @Override
                public void changed(ObservableValue ov, ServerInfo oldV, ServerInfo newV) {
                    if (newV != null) {
                        taServerDescription.setText(newV.getDescription());
                    } else {
                        taServerDescription.setText("");
                    }
                }
            });
            gp.add(cmbxServers, 0, 2);

            taServerDescription = new TextArea();
            taServerDescription.setText("Server description");
            taServerDescription.editableProperty().set(false);
            gp.add(taServerDescription, 0, 3);

            // Adds confirmation button 
            // Leads to startClient
            Button btnConfirm = new Button("Confirm");
            gp.add(btnConfirm, 0, 4);
            btnConfirm.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent e) {
                    ServerInfo server = (ServerInfo) cmbxServers.getSelectionModel().getSelectedItem();

                    if (server != null) {
                        gui.startClient(server.getIP(),
                                server.getPort(),
                                server.getBindingName());
                    }
                }
            });

            Group root = new Group();
            Scene scene = new Scene(root, 700, 380);
            root.getChildren().add(gp);
            stage.setScene(scene);
            stage.setTitle("Server Information");

            // confirms on hitting enter
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
            System.out.println("Error: Could not open game: " + ex.getMessage());
        }
    }

}
