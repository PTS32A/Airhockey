/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demoball;

import genericTests.TestClass;
import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Kargathia
 */
public class DemoBallMain extends Application
{
    Stage stage;
    
    @Override
    public void start(Stage stage) throws IOException
    {
        TestClass testey = new TestClass();

        // loads FXML file, and shows it on screen
        this.stage = stage;
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("demoball.fxml"));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        
        // closes the entire application when the exit button is pressed - if not the timers will stay active
        stage.setOnCloseRequest((WindowEvent event) ->
        {
            Platform.exit();
            System.exit(0);
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        launch(args);
    }
    
}
