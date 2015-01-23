/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javafx.application.Platform;
import javafx.collections.MapChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 *
 * @author Kargathia
 */
public class AirhockeyServer {

    private Lobby lobby;
//    private int portNumber;
//    private String bindingName;
//    private String ipAddress;

    /**
     * constructor
     * @param stage the server stage
     * @param IPAddress the ip-address of the server
     * @param bindingName the binding name of the server 
     * @param portNumber the port number of the server
     */
    public AirhockeyServer(Stage stage, String IPAddress, String bindingName, int portNumber) {

        try {
            lobby = Lobby.getSingle();
            lobby.startPublisher();
            System.out.println("Server: Lobby created");
        } catch (RemoteException ex) {
            System.out.println("Server: RemoteException: " + ex.getMessage());
            lobby = null;
        } catch (NullPointerException ex) {
            System.out.println("Null pointer in Lobby - database error");
            lobby = null;
        }

        // Bind using Naming
        if (lobby != null) {
            try {
                Registry registry = null;
                
                // Checks whether usable registry pre-exists
                try{
                    registry = LocateRegistry.getRegistry(portNumber);
                    registry.list();
                } catch (RemoteException ex){
                    System.out.println("Unable to find existing registry");
                    registry = null;
                }

                // if not: creates new
                if(registry == null){
                    registry = LocateRegistry.createRegistry(portNumber);
                }
                registry.rebind(bindingName, lobby);
            } catch (RemoteException ex) {
                System.out.println("Server: RemoteException: " + ex.getMessage());
            }
            System.out.println("Server: Lobby bound to " + bindingName);
        } else {
            System.out.println("Server: Lobby not bound");
        }

//        ipAddress = "";
//        try {
//            ipAddress = InetAddress.getLocalHost().toString();
//        }
//        catch (UnknownHostException ex) {
//            Logger.getLogger(AirhockeyServer.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        printIPAddresses();
        GridPane gp = new GridPane();
        gp.setAlignment(Pos.CENTER);
        gp.setHgap(10);
        gp.setVgap(10);
        gp.setPadding(new Insets(25, 25, 25, 25));
        Label ip = new Label("IP Address:");
        gp.add(ip, 0, 0);
        Label ipIn = new Label(IPAddress);
        gp.add(ipIn, 1, 0);
        Label lblPort = new Label("Port:");
        gp.add(lblPort, 0, 2);
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

        personIn.setText(String.valueOf(lobby.getActivePersons().keySet().size()));
        lobby.getActivePersons().addListener(new MapChangeListener() {

            @Override
            public void onChanged(MapChangeListener.Change change) {
                final String size = String.valueOf(lobby.getActivePersons().keySet().size());
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        personIn.setText(size);
                    }
                });
            }
        });

        gamesIn.setText(String.valueOf(lobby.getActiveGames().keySet().size()));
        lobby.getActiveGames().addListener(new MapChangeListener() {

            @Override
            public void onChanged(MapChangeListener.Change change) {
                final String size = String.valueOf(lobby.getActiveGames().keySet().size());
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        gamesIn.setText(size);
                    }
                });
            }
        });

        Group root = new Group();
        Scene scene = new Scene(root, 300, 300);
        root.getChildren().add(gp);
        stage.setScene(scene);
        stage.setTitle("Server Information");
        stage.show();
    }

    /**
     * Performs a quick check whether it was possible to init Lobby
     * @return Return a boolean indicating success
     */
    public static boolean checkLobby() {
        try {
            Lobby.getSingle();
            return true;
        } catch (Exception ex) {
            return false;
        }
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
        } catch (UnknownHostException ex) {
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
}
