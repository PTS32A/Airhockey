/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.airhockey.gui;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import s32a.airhockey.Lobby;

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
    
    /**
     * Tries to login, changes window to lobby
     * @param evt 
     */
    public void login(Event evt) throws IllegalArgumentException, SQLException
    {
        if(tfUserName.getText().equals("") || pwfPassword.getText().equals(""))
        {
            super.showDialog("Error", "One or more fields are empty.");
        }
        else
        {
            if(Lobby.getSingle().checkLogin(tfUserName.getText(), pwfPassword.getText()))
            {
                try
                {
                    super.goToLobby(getThisStage());
                } catch (IOException ex)
                {
                    super.showDialog("Error", "Unable to open Lobby: " + ex.getMessage());
                }
            }
            else
            {
                super.showDialog("Error", "Username or password is incorrect.");
            }
        }
    }
    
    /**
     * moves the user to register window
     * @param evt 
     */
    public void register(Event evt)
    {
        try 
        {
            super.goToRegister(getThisStage());
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(LoginFX.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    private Stage getThisStage() 
    {
        return (Stage) tfUserName.getScene().getWindow();
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
