/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Client.GUI;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Getter;
import s32a.Client.ClientData.GameClient;
import s32a.Client.ClientData.LobbyClient;
import s32a.Server.AirhockeyServer;
import s32a.Shared.IGame;
import s32a.Shared.IGameClient;
import s32a.Shared.ILobby;
import s32a.Shared.IPerson;
import s32a.Shared.IPlayer;
import s32a.Shared.ISpectator;

/**
 * NOTES: find out what game is currently active for the closeGame click event
 *
 * @author Kargathia
 */
public class AirhockeyGUI extends Application {

    @FXML
    TextField tfIP, tfPort;
    
    @Getter
    private Stage stage;
    protected static LobbyClient lobby;
    protected static String me;
    protected static String ipAddress = null, bindingName = "AirhockeyServer", portNumber = null;

    @Override
    public void start(Stage stage) throws Exception {
        // sets the static strings with server info
        this.getServerInfo(stage);

        lobby = new LobbyClient(this.requestRemoteLobby(ipAddress, bindingName, portNumber));

        this.stage = stage;
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Login.fxml"));

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                try {
                    lobby.logOut(lobby.getMyPerson(me));
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

        // populates lobby
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    lobby.populate();
                }
                catch (RemoteException ex) {
                    System.out.println("RemoteException on populate: " + ex.getMessage());
                    Logger.getLogger(AirhockeyGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        t.start();
        //goToLobby(stage);
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
        // adds close event to controller through method
        controller.addCloseEvent(stage);
        if (lobby.getMyPerson(me) instanceof IPlayer) {
            controller.addEvents((IPlayer) lobby.getMyPerson(me));
        }
        controller.setMyGame(client);

        //Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Game.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
//        stage.setResizable(false);
        stage.setMinHeight(root.minHeight(600));
        stage.setMinWidth(root.minWidth(1100));

        // Terminates game
        stage.show();
    }

    void showDialog(String type, String message) {
        Stage myDialog = new Dialog(getStage(), type, message);
        myDialog.show();
    }

    /**
     * Opens new window requesting server info - ipAddress and port number
     * portNumber can be pre-filled with 1099, but the textbox should still be there
     * bindingName is hardcoded on both client and server side
     */
    private void getServerInfo(Stage stage){
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                try {
                    Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Server.fxml"));

                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();
                }
                catch (IOException ex) {
                    showDialog("Error", "Could not open game: " + ex.getMessage());
                }
            }
        });
    }

    public void confirmClick (Event evt) {
        ipAddress = tfIP.getText();
        portNumber = tfPort.getText();
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

        ILobby output = null;

        // get beurs associated with registry entry
        try {
            output = (ILobby) Naming.lookup("rmi://"
                    + ipAddress + ":"
                    + portNumber + "/"
                    + bindingName);
        }
        catch (MalformedURLException ex) {
            System.out.println("Client: MalformedURLException: " + ex.getMessage());
            output = null;
        }
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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args[1].equals("server")) {
            // launch application as server
            // TODO: make some snazzy GUI for displaying server stats
            AirhockeyServer server = new AirhockeyServer();
        } else {
            // launch as client
            launch(args);
        }
    }
}
