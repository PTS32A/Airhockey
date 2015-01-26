/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Server;

import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import static s32a.Server.GUI.Dialog.showDialog;

/**
 *
 * @author Kargathia
 */
public class AirhockeyServer {

    private Lobby lobby;
    private ObservableList<String> outMessages;
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
        } catch (RemoteException ex) {
            showDialog("Error", "Server: RemoteException: " + ex.getMessage());
            lobby = null;
        } catch (NullPointerException ex) {
            showDialog("Error", "Null pointer in Lobby - database error: " + ex.getMessage());
            lobby = null;
        }

        // Bind using Naming
        if (lobby != null) {
            try {
                Registry registry = null;
                
                // Checks whether usable registry pre-exists
                try{
                    registry = LocateRegistry.getRegistry(portNumber);
                    registry.lookup("test");
                } catch (RemoteException | NotBoundException ex){
                    // do nothing - expected in majority of cases
                    System.out.println("No existing registry found - starting new one");
                    registry = null;
                }

                // if not: creates new
                if(registry == null){
                    registry = LocateRegistry.createRegistry(portNumber);
                }
                registry.rebind(bindingName, lobby);
            } catch (RemoteException ex) {
                showDialog("Error", "Server: RemoteException: " + ex.getMessage());
            }
            System.out.println("Server: Lobby bound to " + bindingName);
        } else {
            showDialog("Error", "Server: Lobby not bound");
        }

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

        // Adds a listview displaying all system.out.println messages
        ListView<String> lvOutDisplay = new ListView();
        lvOutDisplay.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> list) {
                final ListCell cell = new ListCell() {
                    private Text text;

                    @Override
                    public void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            text = new Text(item.toString());
                            text.setWrappingWidth(lvOutDisplay.getPrefWidth());
                            setGraphic(text);
                        }
                    }
                };

                return cell;
            }
        });

        this.outMessages = FXCollections.observableArrayList(new ArrayList<>());
        // overrides default implementation of out.println to also output to list
        System.setOut(new PrintStream(System.out){
            public void println(String s){
                outMessages.add(s);
                super.println(s);
            }
        });

        // autoscrolls to end of display
        this.outMessages.addListener(new ListChangeListener() {

            @Override
            public void onChanged(ListChangeListener.Change c) {
                lvOutDisplay.scrollTo(c.getList().size() - 1);
            }
        });

        lvOutDisplay.setItems(outMessages);
        gp.add(lvOutDisplay, 1, 5);

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
        Scene scene = new Scene(root, 400, 600);
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
}
