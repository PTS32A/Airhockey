/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.airhockey.gui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 *
 * @author Kargathia
 */
public class RegisterFX extends AirhockeyGUI implements Initializable
{
    @FXML TextField tfUserName;
    @FXML PasswordField pwfPassword, pwfPasswordConfirm;
    
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

    }
    
    /**
     * Tries to register credentials - transports to lobby on confirmation
     * logs in with new credentials
     * Shows error dialog when unable, or wrong input
     * @param evt 
     */
    public void registerOk(Event evt)
    {
        
    }
    
    /**
     * resets fields
     * @param evt 
     */
    public void registerCancel(Event evt)
    {
        
    }
}
