/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Server;

import java.io.PrintStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
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
import javafx.scene.layout.AnchorPane;
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
        Label lblIP = new Label("IP Address:");
        gp.add(lblIP, 0, 0);
        Label lblIPDisplay = new Label(IPAddress);
        gp.add(lblIPDisplay, 1, 0);
        Label lblPort = new Label("Port:");
        gp.add(lblPort, 0, 2);
        Label lblPortDisplay = new Label(String.valueOf(portNumber));
        gp.add(lblPortDisplay, 1, 2);
        Label lblGamesCount = new Label("Active Games:");
        gp.add(lblGamesCount, 0, 3);
        Label lblGamesCountDisplay = new Label("0");
        gp.add(lblGamesCountDisplay, 1, 3);
        Label lblPersonCount = new Label("Active Users:");
        gp.add(lblPersonCount, 0, 4);
        Label lblPersonCountDisplay = new Label("0");
        gp.add(lblPersonCountDisplay, 1, 4);
        Label lblSystemLog = new Label("System Log:");
        lblSystemLog.setUnderline(true);
        gp.add(lblSystemLog, 1, 5);

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
//        gp.add(lvOutDisplay, 1, 5);

        lblPersonCountDisplay.setText(String.valueOf(lobby.getActivePersons().keySet().size()));
        lobby.getActivePersons().addListener(new MapChangeListener() {

            @Override
            public void onChanged(MapChangeListener.Change change) {
                final String size = String.valueOf(lobby.getActivePersons().keySet().size());
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        lblPersonCountDisplay.setText(size);
                    }
                });
            }
        });

        lblGamesCountDisplay.setText(String.valueOf(lobby.getActiveGames().keySet().size()));
        lobby.getActiveGames().addListener(new MapChangeListener() {

            @Override
            public void onChanged(MapChangeListener.Change change) {
                final String size = String.valueOf(lobby.getActiveGames().keySet().size());
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        lblGamesCountDisplay.setText(size);
                    }
                });
            }
        });

        Group root = new Group();
        Scene scene = new Scene(root, 300, 600);
        root.getChildren().add(gp);

        AnchorPane viewPane = new AnchorPane();
        viewPane.getChildren().add(lvOutDisplay);
        AnchorPane.setTopAnchor(lvOutDisplay, 170.0);
        AnchorPane.setLeftAnchor(lvOutDisplay, 25.0);
        root.getChildren().add(viewPane);

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
