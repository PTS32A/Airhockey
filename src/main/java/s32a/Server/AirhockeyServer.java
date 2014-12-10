/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Server;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import s32a.Shared.ILobby;

/**
 *
 * @author Kargathia
 */
public class AirhockeyServer{

    private Lobby lobby;
    private Stage stage;
    private static final int portNumber = 1099;
    private static final String bindingName = "AirhockeyServer";
    private String ipAddress;

    public AirhockeyServer() {

        try {
            lobby = Lobby.getSingle();
            lobby.startPublisher();
//            lobby.populate();
            System.out.println("Server: Lobby created");
        }
        catch (RemoteException ex) {
            System.out.println("Server: RemoteException: " + ex.getMessage());
            lobby = null;
        }

        // Bind using Naming
        if (lobby != null) {
            try {
                LocateRegistry.createRegistry(portNumber);
                Naming.rebind(bindingName, lobby);
            }
            catch (MalformedURLException ex) {
                System.out.println("Server: MalformedURLException: " + ex.getMessage());
            }
            catch (RemoteException ex) {
                System.out.println("Server: RemoteException: " + ex.getMessage());
            }
            System.out.println("Server: Lobby bound to " + bindingName);
        } else {
            System.out.println("Server: Lobby not bound");
        }

        ipAddress = "";

        try {
            ipAddress = InetAddress.getLocalHost().toString();
        }
        catch (UnknownHostException ex) {
            Logger.getLogger(AirhockeyServer.class.getName()).log(Level.SEVERE, null, ex);
        }

        new JFXPanel();
        Platform.runLater(() ->
        {
            this.stage = new Stage();
            GridPane gp = new GridPane();
            gp.setAlignment(Pos.CENTER);
            gp.setHgap(10);
            gp.setVgap(10);
            gp.setPadding(new Insets(25, 25, 25, 25));
            Label ip = new Label("IP Address:");
            gp.add(ip, 0, 0);
            Label ipIn = new Label(this.ipAddress);
            gp.add(ipIn, 1, 0);
            Label port = new Label("Port:");
            gp.add(port, 0, 2);
            Label portIn = new Label(String.valueOf(portNumber));
            gp.add(portIn, 1, 2);
            Label games = new Label("Active Games:");
            gp.add(games, 0, 3);
            Label gamesIn = new Label("0");
            gp.add(gamesIn, 1, 3);
            Label person = new Label("Active Users:");
            gp.add(person, 0, 4);
            Label personIn = new Label("0");
            gp.add(personIn, 1, 4);

            // Binding 
            //gamesIn.textProperty().bind(lobby.getActiveGames().size());
            //personIn.textProperty().bind(lobby.getActivePersons().size());
    //        btn.setOnAction(new EventHandler<ActionEvent>() {
    //
    //            @Override
    //            public void handle(ActionEvent e) {
    //                 
    //            }
    //        });
            Group root = new Group();
            Scene scene = new Scene(root, 300, 300);
            root.getChildren().add(gp);
            this.stage.setScene(scene);
            this.stage.setTitle("Server Information");
            this.stage.show();
            
            this.stage.setOnCloseRequest((WindowEvent event) -> {
                Platform.exit();
                System.exit(0);
            });
        
        });
    }

    /**
     * Runs the program as server - for debugging only. In release running as
     * server is handled by running AirhockeyGUI with "server" as argument
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AirhockeyServer server = new AirhockeyServer();
//        launch(args);
    }
}
