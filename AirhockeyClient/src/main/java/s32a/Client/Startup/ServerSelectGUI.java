/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Client.Startup;

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
import javafx.scene.control.Control;
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
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import s32a.Client.GUI.AirhockeyGUI;
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

    private ObservableList<ServerInfo> servers;
    List<Control> serverDisplayControls;

    private String server = "athena.fhict.nl";
    private boolean SSL = true;
    private String user = "i293443";
    private String pass = "ifvr2edfh101";

    /**
     * Constructor
     * @param stage
     * @param gui
     */
    public ServerSelectGUI(Stage stage, AirhockeyGUI gui) {
        this.stage = stage;
        this.gui = gui;

        this.servers
                = FXCollections.observableArrayList(new ArrayList<ServerInfo>());

        this.displayServers(false);
    }

    /**
     * Shows a screen for logging in to the central FTP server with non-default
     * values.
     */
    private void displayFTPLogin() {
        Stage ftpLoginStage = new Stage();
        ftpLoginStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                stage.show();
            }
        });

        GridPane gp = new GridPane();
        gp.setAlignment(Pos.CENTER);
        gp.setHgap(10);
        gp.setVgap(10);
        gp.setPadding(new Insets(25, 25, 25, 25));

        Label lblServerIP = new Label("FTP website:");
        gp.add(lblServerIP, 0, 1);
        TextField tfFTPAddress = new TextField();
        tfFTPAddress.setText(this.server);
        gp.add(tfFTPAddress, 1, 1);

        CheckBox cbxSSL = new CheckBox("SSL");
        cbxSSL.setSelected(this.SSL);
        gp.add(cbxSSL, 1, 2);

        Label lblUser = new Label("Username:");
        gp.add(lblUser, 0, 3);
        TextField tfUser = new TextField();
        tfUser.setText(this.user);
        gp.add(tfUser, 1, 3);

        Label lblPass = new Label("Password:");
        gp.add(lblPass, 0, 4);
        PasswordField tfPass = new PasswordField();
        tfPass.setText(this.pass);
        gp.add(tfPass, 1, 4);

        CheckBox cbxAnyConnect = new CheckBox("AnyConnect running");
        gp.add(cbxAnyConnect, 1, 5);

        Label lblPort = new Label("Status:");
        gp.add(lblPort, 0, 6);
        TextField tfPort = new TextField();
        
        try //Try-catch because untested
        {
            tfPort.editableProperty().setValue(false);
        }
        catch (Exception ex)
        {
            System.out.println("Exception in making the status textfield non-editable. Data" + ex.getMessage());
        }
        
        tfPort.setText("Not Connected");
        gp.add(tfPort, 1, 6);

        Button btnConfirm = new Button("Connect");
        gp.add(btnConfirm, 1, 7);
        btnConfirm.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                user = tfUser.getText();
                pass = tfPass.getText();
                server = tfFTPAddress.getText();
                SSL = cbxSSL.isSelected();

                if (loginToFTP(server, user, pass, SSL)) {
                    setDataFromFTP(cbxAnyConnect.isSelected());
                    displayServers(true);
                    ftpLoginStage.close();
                } else {
                    tfPort.setText("Could not connect, check spelling and internet connection.");
                }
            }

        });

        Group root = new Group();
        Scene scene = new Scene(root, 300, 300);
        root.getChildren().add(gp);
        ftpLoginStage.setScene(scene);
        ftpLoginStage.setTitle("Central Server Login");

        ftpLoginStage.getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.ENTER) {
                    btnConfirm.fire();
                }
            }
        });

        stage.hide();
        ftpLoginStage.show();
    }

    /**
     * Displays given servers in GUI. Starts client on button click.
     *
     * @param serverInput
     */
    private void displayServers(boolean loggedIn) {
        this.serverDisplayControls = new ArrayList<>();

        try {
            // GridPane creation
            GridPane gp = new GridPane();
            gp.setAlignment(Pos.CENTER);
            gp.setHgap(10);
            gp.setVgap(10);
            gp.setPadding(new Insets(25, 25, 25, 25));

            // button for connecting with default settings
            Button btnFTPConnect = new Button("Refresh");
            btnFTPConnect.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    if (loginToFTP(server, user, pass, SSL)) {
                        setDataFromFTP(false);
                    }
                }
            });
            gp.add(btnFTPConnect, 0, 0);

            Button btnChangeFTPSettings = new Button("Settings");
            btnChangeFTPSettings.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    displayFTPLogin();
                }
            });
            gp.add(btnChangeFTPSettings, 0, 1);

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
            gp.add(cmbxServers, 1, 1);
            serverDisplayControls.add(cmbxServers);
            cmbxServers.setDisable(!loggedIn);

            // server display text area
            taServerDescription = new TextArea();
            taServerDescription.setText("Server description");
            taServerDescription.editableProperty().set(false);
            taServerDescription.setWrapText(true);
            taServerDescription.setMaxWidth(300);
            taServerDescription.setMaxHeight(100);
            gp.add(taServerDescription, 1, 2);
            serverDisplayControls.add(taServerDescription);
            taServerDescription.setDisable(!loggedIn);

            // Adds confirmation button 
            // Leads to startClient
            Button btnConfirm = new Button("Confirm");
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
            gp.add(btnConfirm, 1, 3);
            serverDisplayControls.add(btnConfirm);
            btnConfirm.setDisable(!loggedIn);

            Group root = new Group();
            Scene scene = new Scene(root, 500, 280);
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

    /**
     * Logs in to FTP server, and retrieves info. Can be called with default
     * info.
     *
     * @param server
     * @param user
     * @param pw
     * @param SSL
     * @return connection success
     */
    private boolean loginToFTP(String server, String user, String pw, boolean SSL) {
        handler = new FTPHandler(server, user, pw, SSL);
        return handler.checkLogin();
    }

    /**
     * After being logged in to FTP, handles the data retrieved from there.
     */
    private void setDataFromFTP(boolean anyConnect) {
        servers.addAll(handler.getFTPData());

        // sets codebase property
        System.setProperty("java.rmi.server.codebase", handler.getCodebaseURL());

        if (anyConnect) {
            System.setProperty("java.rmi.server.hostname", "127.0.0.1");
        }

        for (Control c : serverDisplayControls) {
            c.setDisable(false);
        }
    }

}
