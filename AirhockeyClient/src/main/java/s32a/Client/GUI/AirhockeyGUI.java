/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Client.GUI;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Getter;
import s32a.Client.ClientData.GameClient;
import s32a.Client.ClientData.LobbyClient;
import s32a.Client.Startup.ServerInfo;
import s32a.Shared.ILobby;
import s32a.Shared.IPerson;
import s32a.Shared.IPlayer;
import s32a.Shared.IServerInfo;

/**
 * NOTES: find out what game is currently active for the closeGame click event
 *
 * @author Kargathia
 */
public class AirhockeyGUI {

    @Getter
    private Stage stage;
    protected static LobbyClient lobby = null;
    public static String me = "--";
    protected static String ipAddress = null, 
            bindingName = "AirhockeyServer",
            portNumber = null;
    
    private ComboBox serverNameBox;
    private TextArea serverDescriptionArea;
    
    private List<IServerInfo> servers;
    private List<String> serverNames;
    private ObservableList<String> observableServerNames;

    public void startGUI(Stage stage) {
        this.stage = stage;
        this.servers = new ArrayList<>();
        this.serverNames = new ArrayList<>();
        
        //Hard coded fake servers for testing:
        servers.add(new ServerInfo("Server1", "This is description 1", "bindingName1", "101.100.100.100", 1099));
        servers.add(new ServerInfo("Server2", "This is description 2", "bindingName2", "102.100.100.100", 1099));
        servers.add(new ServerInfo("Server3", "This is description 3", "bindingName2", "103.100.100.100", 1099));
        
        for (IServerInfo serverInfo : servers)
        {
            serverNames.add(serverInfo.getName());
        }
        
        observableServerNames = FXCollections.observableArrayList(serverNames);
                
        // sets the static strings with server info
        this.getServerInfo(stage);

        //goToLobby(stage);
    }

    /**
     * Starts client GUI after IP address and port number were provided
     */
    public void startClient() {

        try {
            lobby = new LobbyClient(this, this.requestRemoteLobby(ipAddress, bindingName, portNumber));
            if (lobby == null) {
                showDialog("Error", "lobby is null");
                return;
            }
        }
        catch (RemoteException ex) {
            String error = "RemoteException in trying to open new LobbyClient";
            System.out.println(error);
            showDialog("Error", error);
            Logger.getLogger(AirhockeyGUI.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Login.fxml"));
            root = (Parent) loader.load();
            LoginFX controller = (LoginFX) loader.getController();

            // displays in LoginFX current connection status by calling displayConnectionStatus(String status)
            if (lobby != null) {
                controller.displayConnectionStatus("Connected");
            } else {
                controller.displayConnectionStatus("Connection problems");
            }
        }
        catch (IOException ex) {
            System.out.println("failed to load Login.fxml");
            Logger.getLogger(AirhockeyGUI.class.getName()).log(Level.SEVERE, null, ex);
        }

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                try {
                    lobby.logOut(lobby.getMyPerson(me).getName());
                    Platform.exit();
                    System.exit(0);
                }
                catch (RemoteException ex) {
                    System.out.println("RemoteException on logout: " + ex.getMessage());
                    Logger.getLogger(AirhockeyGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     *
     */
    void goToLogin(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Login.fxml"));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     *
     * @param stage
     * @throws java.io.IOException
     */
    public void goToRegister(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Register.fxml"));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     *
     */
    void goToLobby(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Lobby.fxml"));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     *
     */
    void goToGame(Stage stage, GameClient client) throws IOException {

        // gets the controller class while initializing
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Game.fxml"));
        Parent root = (Parent) loader.load();
        GameFX controller = (GameFX) loader.getController();

        //Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Game.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
//        stage.setResizable(false);
        stage.setMinHeight(root.minHeight(600));
        stage.setMinWidth(root.minWidth(1100));

        // adds close event to controller through method
        controller.addCloseEvent(stage);
        controller.setMyGame(client);
        controller.bindMyGameProperties();
        if (lobby.getMyPerson(me) instanceof IPlayer) {
            controller.addEvents((IPlayer) lobby.getMyPerson(me));
        }

        // Terminates game
        stage.show();
    }

    void showDialog(String type, String message) {
        Stage myDialog = new Dialog(getStage(), type, message);
        myDialog.show();
    }

    /**
     * Opens new window requesting server info - ipAddress and port number
     * portNumber can be pre-filled with 1099, but the textbox should still be
     * there bindingName is hardcoded on both client and server side
     */
    private void getServerInfo(Stage stage) {
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
                    Label ip = new Label("Servers:");
                    gp.add(ip, 0, 1);
                    
                    serverNameBox = new ComboBox(observableServerNames);
                    serverNameBox.setPromptText("Choose a server");
                    serverNameBox.valueProperty().addListener(new ChangeListener<String>() {
                        @Override 
                        public void changed(ObservableValue ov, String t, String t1) { 
                            int index = serverNameBox.getSelectionModel().getSelectedIndex();
                            if (index >= 0 && servers.size() > index)
                            {
                                serverDescriptionArea.setText(servers.get(index).getDescription());
                            }
                            else
                            {
                                serverDescriptionArea.setText("");
                            }
                        }    
                    });
                    try
                    {
                        ///serverNameBox.getSelectionModel().selectFirst();
                    }
                    catch (Exception ex)
                    {
                        System.out.println("Exception: " + ex.getMessage());
                    }
                    gp.add(serverNameBox, 0, 2);
                    
                    serverDescriptionArea = new TextArea();
                    serverDescriptionArea.setText("Server description");
                    serverDescriptionArea.editableProperty().set(false);
                    gp.add(serverDescriptionArea, 0, 3);
                    
                    Button confirm = new Button("Confirm");
                    gp.add(confirm, 0, 4);
                    confirm.setOnAction(new EventHandler<ActionEvent>() {

                        @Override
                        public void handle(ActionEvent e) {
                            int index = serverNameBox.getSelectionModel().getSelectedIndex();
                            
                            if (index >= 0 && servers.size() > index)
                            {
                                ipAddress = servers.get(index).getIP();
                                portNumber = servers.get(index).getPort() + "";
                                
                                stage.close();
                                startClient();
                            }
                        }
                    });

                    Group root = new Group();
                    Scene scene = new Scene(root, 700, 380);
                    root.getChildren().add(gp);
                    stage.setScene(scene);
                    stage.setTitle("Server Information");

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
                    showDialog("Error", "Could not open game: " + ex.getMessage());
                }
            }
        });
        while (stage.isShowing()) {

        }

    }

    /**
     * Makes the initial RMI connection by retrieving the ILobby bound in the
     * register at given IP-address
     *
     * @param ipAddress
     * @param bindingName
     * @param portNumber
     * @return
     */
    private ILobby requestRemoteLobby(String ipAddress, String bindingName, String portNumber) {
        if (ipAddress == null || bindingName == null || portNumber == null) {
            showDialog("Error", "no binding name, ipAddress or portNumber provided");
            return null;
        }

//        // Solves socket connection refused bug
//        System.setProperty("java.rmi.server.hostname", ipAddress);
        ILobby output = null;

        // get beurs associated with registry entry
        try {
            Registry registry = LocateRegistry.getRegistry(ipAddress, Integer.valueOf(portNumber));
            output = (ILobby) registry.lookup(bindingName);

//            output = (ILobby) Naming.lookup("rmi://"
//                    + ipAddress + ":"
//                    + portNumber + "/"
//                    + bindingName);   
        }
//        catch (MalformedURLException ex) {
//            System.out.println("Client: MalformedURLException: " + ex.getMessage());
//            output = null;
//        }
        catch (RemoteException ex) {
            System.out.println("Client: RemoteException: " + ex.getMessage());
            output = null;
        }
        catch (NotBoundException ex) {
            System.out.println("Client: NotBoundException: " + ex.getMessage());
            output = null;
        }
        return output;
    }

    /**
     * general method for returning current person. Provided here to prevent
     * having to catch RemoteExceptions everywhere on a commong call
     *
     * @return
     */
    protected IPerson getMe() {
        if (me == null || lobby == null) {
            System.out.println("me or lobby is null");
            return null;
        }
        IPerson output = null;
        try {
            output = lobby.getMyPerson(me);
        }
        catch (RemoteException ex) {
            System.out.println("RemoteException on retrieving current person: " + ex.getMessage());
            Logger.getLogger(AirhockeyGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return output;
    }
}
