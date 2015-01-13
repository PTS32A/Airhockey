/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Client.GUI;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 *
 * @author Kargathia
 */
public class LoginFX extends AirhockeyGUI implements Initializable {

    @FXML
    TextField tfUserName;
    @FXML
    PasswordField pwfPassword;
    @FXML
    Label lblConnStatus;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    /**
     * Tries to login, changes window to lobby
     *
     * @param evt
     */
    public void login(Event evt) {
        if (tfUserName.getText().equals("") || pwfPassword.getText().equals("")) {
            super.showDialog("Error", "One or more fields are empty.");
        } else {
            try {
                AirhockeyGUI.me = tfUserName.getText();
                if (lobby.checkLogin(tfUserName.getText(), pwfPassword.getText(), lobby)) {
                    super.goToLobby(getThisStage());
                } else {
                    super.showDialog("Error", "Username or password is incorrect.");
                }
            }
            catch (IllegalArgumentException ex) {
                super.showDialog("Error", "Unable to login: " + ex.getMessage());
            }
            catch (SQLException ex) {
                super.showDialog("Error", "Unable to open Lobby: " + ex.getMessage());
            }
            catch (IOException ex) {
                try {
                    lobby.logOut(super.getMe().getName());
                }
                catch (RemoteException ex1) {
                    System.out.println("RemoteException on trying to logout after IOException: " + ex1.getMessage());
                    Logger.getLogger(LoginFX.class.getName()).log(Level.SEVERE, null, ex1);
                }
                super.showDialog("Error", "Unable to open Lobby" + ex.getMessage());
            }
        }
    }

    /**
     * Displays connection status, preventing having to use a popup.
     * @param status
     */
    public void displayConnectionStatus(String status){
        this.lblConnStatus.setText("Connection status: " + status);
    }

    /**
     * moves the user to register window
     *
     * @param evt
     */
    public void register(Event evt) {
        try {
            super.goToRegister(getThisStage());
        }
        catch (IOException ex) {
            super.showDialog("Error", "Unable to go to Register: " + ex.getMessage());
        }
    }

    /**
     * Returns currently active stage
     *
     * @return
     */
    private Stage getThisStage() {
        return (Stage) tfUserName.getScene().getWindow();
    }
}