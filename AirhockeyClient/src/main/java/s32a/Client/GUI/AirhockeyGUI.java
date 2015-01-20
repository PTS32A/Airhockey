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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Getter;
import lombok.Setter;
import s32a.Client.ClientData.GameClient;
import s32a.Client.ClientData.LobbyClient;
import s32a.Shared.ILobby;
import s32a.Shared.IPerson;
import s32a.Shared.IPlayer;

/**
 * NOTES: find out what game is currently active for the closeGame click event
 *
 * @author Kargathia
 */
public class AirhockeyGUI {

    @Setter 
    @Getter
    private Stage stage;
    /**
     * Lobby instance is kept centrally, to be queried throughout the client
     */
    protected static LobbyClient lobby = null;
    /**
     * The name of the current player. Kept short as it is referenced often.
     */
    public static String me = "--";

    /**
     * Starts client GUI after IP address and port number were provided
     * @param ipAddress
     * @param bindingName
     * @param portNumber
     */
    public void startClient(String ipAddress, int portNumber, String bindingName) {

        try {
            lobby = new LobbyClient(this, this.requestRemoteLobby(ipAddress, bindingName, portNumber));
            if (lobby == null) {
                Dialog.showDialog("Error", "lobby is null");
                return;
            }
        } catch (RemoteException ex) {
            String error = "RemoteException in trying to open new LobbyClient";
            System.out.println(error);
            Dialog.showDialog("Error", error);
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
        } catch (IOException ex) {
            System.out.println("failed to load Login.fxml");
            Logger.getLogger(AirhockeyGUI.class.getName()).log(Level.SEVERE, null, ex);
        }

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                try {
                    lobby.logOut(me);
                    Platform.exit();
                    System.exit(0);
                } catch (RemoteException ex) {
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
    GameFX goToGame(Stage stage, GameClient client) throws IOException {

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
        stage.setTitle("Airhockey");
        controller.addCloseEvent(stage);
        controller.setMyGame(client);
        controller.bindMyGameProperties();
        controller.setMyStage(stage);
        if (lobby.getMyPerson(me) instanceof IPlayer) {
            controller.addEvents((IPlayer) lobby.getMyPerson(me));
        }

        // Terminates game
        stage.show();
        return controller;
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
    private ILobby requestRemoteLobby(String ipAddress, String bindingName, int portNumber) {
        if (ipAddress == null || bindingName == null || portNumber == -1) {
            Dialog.showDialog("Error", "no binding name, ipAddress or portNumber provided");
            return null;
        }

//        // Solves socket connection refused bug
//        System.setProperty("java.rmi.server.hostname", ipAddress);
        ILobby output = null;

        // get beurs associated with registry entry
        try {
            Registry registry = LocateRegistry.getRegistry(ipAddress, portNumber);
            output = (ILobby) registry.lookup(bindingName);

//            output = (ILobby) Naming.lookup("rmi://"
//                    + ipAddress + ":"
//                    + portNumber + "/"
//                    + bindingName);   
        } //        catch (MalformedURLException ex) {
        //            System.out.println("Client: MalformedURLException: " + ex.getMessage());
        //            output = null;
        //        }
        catch (RemoteException ex) {
            System.out.println("Client: RemoteException: " + ex.getMessage());
            output = null;
        } catch (NotBoundException ex) {
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
        } catch (RemoteException ex) {
            System.out.println("RemoteException on retrieving current person: " + ex.getMessage());
            Logger.getLogger(AirhockeyGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return output;
    }
}
