/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Server;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import s32a.Server.Publishers.GamePublisher;
import s32a.Shared.IPerson;

/**
 *
 * @author Kargathia
 */
public class AirhockeyServer {

    private Lobby lobby;
    private Stage stage;
    private static final int portNumber = 1099;
    private static final String bindingName = "AirhockeyServer";
    private String ipAddress;
    private ObjectProperty<HashMap<String, IPerson>> activePersonsProp;

    public AirhockeyServer() {

        try {
            lobby = Lobby.getSingle();
            lobby.startPublisher();
            lobby.populate();
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

        printIPAddresses();

        new JFXPanel();
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                stage = new Stage();
                GridPane gp = new GridPane();
                gp.setAlignment(Pos.CENTER);
                gp.setHgap(10);
                gp.setVgap(10);
                gp.setPadding(new Insets(25, 25, 25, 25));
                Label ip = new Label("IP Address:");
                gp.add(ip, 0, 0);
                Label ipIn = new Label(ipAddress);
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
                gamesIn.textProperty().bind(Bindings.size((lobby.getActiveGames())).asString());

                activePersonsProp = new SimpleObjectProperty<>(null);              
                activePersonsProp.addListener(new ChangeListener() {

                    @Override
                    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                        personIn.setText(String.valueOf(activePersonsProp.get().size()));
                    }
                });
                activePersonsProp.bind(lobby.getActivePersonsProperty());

                Group root = new Group();
                Scene scene = new Scene(root, 300, 300);
                root.getChildren().add(gp);
                stage.setScene(scene);
                stage.setTitle("Server Information");
                stage.show();

                stage.setOnCloseRequest((WindowEvent event) -> {
                    Platform.exit();
                    System.exit(0);
                });
            }
        });
    }

    /**
     * Prints all known IP addresses
     */
    private static void printIPAddresses() {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            System.out.println("Server: IP Address: " + localhost.getHostAddress());
            // Just in case this host has multiple IP addresses....
            InetAddress[] allMyIps = InetAddress.getAllByName(localhost.getCanonicalHostName());
            if (allMyIps != null && allMyIps.length > 1) {
                System.out.println("Server: Full list of IP addresses:");
                for (InetAddress allMyIp : allMyIps) {
                    System.out.println("    " + allMyIp);
                }
            }
        }
        catch (UnknownHostException ex) {
            System.out.println("Server: Cannot get IP address of local host");
            System.out.println("Server: UnknownHostException: " + ex.getMessage());
        }

//        try {
//            System.out.println("Server: Full list of network interfaces:");
//            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
//                NetworkInterface intf = en.nextElement();
//                System.out.println("    " + intf.getName() + " " + intf.getDisplayName());
//                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
//                    System.out.println("        " + enumIpAddr.nextElement().toString());
//                }
//            }
//        }
//        catch (SocketException ex) {
//            System.out.println("Server: Cannot retrieve network interface list");
//            System.out.println("Server: UnknownHostException: " + ex.getMessage());
//        }
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
