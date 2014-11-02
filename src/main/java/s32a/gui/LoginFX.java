/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.gui;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import s32a.airhockey.Lobby;

/**
 *
 * @author Kargathia
 */
public class LoginFX extends AirhockeyGUI implements Initializable
{

    @FXML
    TextField tfUserName;
    @FXML
    PasswordField pwfPassword;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        
    }

    /**
     * Tries to login, changes window to lobby
     *
     * @param evt
     */
    public void login(Event evt)
    {
        if (tfUserName.getText().equals("") || pwfPassword.getText().equals(""))
        {
            super.showDialog("Error", "One or more fields are empty.");
        } else
        {
            try
            {
                if (Lobby.getSingle().checkLogin(tfUserName.getText(), pwfPassword.getText()))
                {                   
                    super.goToLobby(getThisStage());
                } else
                {
                    super.showDialog("Error", "Username or password is incorrect.");
                }
            } catch (IllegalArgumentException ex)
            {
                super.showDialog("Error", "Unable to login: " + ex.getMessage());
            } catch (SQLException ex)
            {
                super.showDialog("Error", "Unable to open Lobby: " + ex.getMessage());
            } catch (IOException ex)
            {
                Lobby.getSingle().logOut(Lobby.getSingle().getCurrentPerson());
                super.showDialog("Error", "Unable to open Lobby" + ex.getMessage());
            }
        }
    }

    /**
     * moves the user to register window
     *
     * @param evt
     */
    public void register(Event evt)
    {
        try
        {
            super.goToRegister(getThisStage());
        } catch (IOException ex)
        {
            super.showDialog("Error", "Unable to go to Register: " + ex.getMessage());
        }
    }

    private Stage getThisStage()
    {
        return (Stage) tfUserName.getScene().getWindow();
    }
}