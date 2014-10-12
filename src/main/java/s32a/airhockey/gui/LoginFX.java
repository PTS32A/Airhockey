/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.airhockey.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 *
 * @author Kargathia
 */
public class LoginFX extends AirhockeyGUI implements Initializable
{
    @FXML TextField tfUserName;
    @FXML PasswordField pwfPassword;
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        
    }
    
    // template code for opening an additional window, in this case showing Lobby
    // for merely switching windows, base.goTo<Something>() should be called
    public void openNew(Event evt)
    {
        final AirhockeyGUI base = this;        
        javafx.application.Platform.runLater(new Runnable() 
        {
            @Override
            public void run() 
            {           
                try
                {
                    Stage stage = new Stage();
                    base.goToLobby(stage);
                    stage.show();
                } 
                catch (IOException ex)
                {
                    Logger.getLogger(LoginFX.class.getName()).log(Level.SEVERE, null, ex);
                }          
            }
        });
    }
}
