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
import static s32a.Client.GUI.Dialog.showDialog;
import s32a.Client.Startup.ClientMain;

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
            showDialog("Error", "One or more fields are empty.");
        } else {
            try {
                AirhockeyGUI.me = tfUserName.getText();
                if (lobby.checkLogin(tfUserName.getText(), pwfPassword.getText(), lobby)) {
                    super.goToLobby(getThisStage());
                } else {
                    System.out.println("showdialog now");
                    showDialog("Error", "Username or password is incorrect.");
                }
            } catch (IllegalArgumentException ex) {
                showDialog("Error", "Unable to login: " + ex.getMessage());
                // executed if not logged in
                ClientMain.launchClient();
            } catch (SQLException ex) {
                showDialog("Error", "Unable to open Lobby: " + ex.getMessage());
                // executed if not logged in
                ClientMain.launchClient();
            } catch (IOException ex) {
                try {
                    lobby.logOut(super.getMe().getName());
                    // executed if not logged in
                    ClientMain.launchClient();
                } catch (RemoteException ex1) {
                    System.out.println("RemoteException on trying to logout after IOException: " + ex1.getMessage());
                    Logger.getLogger(LoginFX.class.getName()).log(Level.SEVERE, null, ex1);
                    // executed if not logged in
                    ClientMain.launchClient();
                }
                showDialog("Error", "Unable to open Lobby" + ex.getMessage());
            }

        }
    }

    /**
     * Displays connection status, preventing having to use a popup.
     *
     * @param status
     */
    public void displayConnectionStatus(String status) {
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
        } catch (IOException ex) {
            showDialog("Error", "Unable to go to Register: " + ex.getMessage());
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
