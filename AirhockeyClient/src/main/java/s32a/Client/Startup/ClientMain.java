/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Client.Startup;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import s32a.Client.GUI.AirhockeyGUI;

/**
 *
 * @author Kargathia
 */
public class ClientMain extends Application {

    private Stage stage;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        this.stage = primaryStage;
//        ClientMainGUI clientMainGui = new ClientMainGUI(stage);
//        clientMainGui.startGUI();
        
        AirhockeyGUI gui = new AirhockeyGUI();
        gui.startGUI(stage);
    }

}
