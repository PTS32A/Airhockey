/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Client.Startup;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import s32a.Client.GUI.AirhockeyGUI;

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

        try {
            stage.getIcons().add(new Image("file:GamePNG.png"));
        } catch (Exception ex) {
            System.out.println("Exception in setting the stage icon: " + ex.getMessage());
        }

        AirhockeyGUI gui = new AirhockeyGUI();
        gui.setStage(stage);
        ServerSelectGUI serverSelect = new ServerSelectGUI(stage, gui);
    }

    /**
     * Starts the client
     *
     * @param stage the client's stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        // sets security manager
        System.setProperty("java.security.policy", "rmi.policy");
        System.setSecurityManager(new SecurityManager());

        ClientMain.stage = stage;
        stage.setResizable(false);
        launchClient();
    }

}
