/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Client.Startup;

import javafx.application.Application;
import javafx.stage.Stage;
import s32a.Client.GUI.AirhockeyGUI;
import s32a.Client.GUI.Dialog;

/**
 *
 * @author Kargathia
 */
public class ClientMain extends Application {

    private static Stage stage;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        launch(args);
    }

    /**
     * Repeatable method for (re)starting the client application.
     */
    public static void launchClient() {
        AirhockeyGUI gui = new AirhockeyGUI();
        gui.setStage(stage);
        ServerSelectGUI serverSelect = new ServerSelectGUI(stage, gui);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // sets security manager
        System.setProperty("java.security.policy", "rmi.policy");
        System.setSecurityManager(new SecurityManager());

        ClientMain.stage = stage;
        launchClient();
    }

}
