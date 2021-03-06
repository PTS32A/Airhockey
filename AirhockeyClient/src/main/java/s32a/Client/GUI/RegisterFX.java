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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import static s32a.Client.GUI.Dialog.showDialog;

/**
 *
 * @author Kargathia
 */
public class RegisterFX extends AirhockeyGUI implements Initializable {

    @FXML
    TextField tfUserName;
    @FXML
    PasswordField pwfPassword, pwfPasswordConfirm;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    /**
     * Tries to register credentials - transports to lobby on confirmation logs
     * in with new credentials Shows error dialog when unable, or wrong input
     *
     * @param evt
     */
    public void registerOk(Event evt) {
        if (tfUserName.getText().equals("") || pwfPassword.getText().equals("")
                || pwfPasswordConfirm.getText().equals("")) {
            showDialog("Error", "One or more fields are empty");
        } else {
            if (!pwfPassword.getText().equals(pwfPasswordConfirm.getText())) {
                showDialog("Error", "Passwords do not match");
            } else {
                try {
                    if (lobby.addPerson(tfUserName.getText(), pwfPassword.getText())) {
                        try {
                            super.goToLogin(getThisStage());
                            showDialog("Success", "Welcome to Airhockey, " + tfUserName.getText() + "!");
                        } catch (IOException ex) {
                            showDialog("Error", "Unable to open Lobby: " + ex.getMessage());
                        }
                    } else {
                        showDialog("Error", "Username already exists.");
                    }
                }
                catch (IllegalArgumentException ex) {
                    System.out.println("IllegalArgumentException on lobby.addPerson: " + ex.getMessage());
                    showDialog("Error", "Wrong input");
                }
                catch (SQLException ex) {
                    System.out.println("SQL Exception on lobby.addPerson: " + ex.getMessage());
                    showDialog("Database Error", "SQL Error on adding person: " + ex.getMessage());
                }
                catch (RemoteException ex) {
                    System.out.println("RemoteException on lobby.addPerson: " 
                            + ex.getMessage());
                    showDialog("Error", "An error occured adding a new person to lobby: "
                            + ex.getMessage());
//                    Logger.getLogger(RegisterFX.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * resets fields
     *
     * @param evt
     */
    public void registerCancel(Event evt) {
        try {
            super.goToLogin(getThisStage());
        } catch (IOException ex) {
            showDialog("Error", "Unable to go to Login: " + ex.getMessage());
        }
    }

    /**
     * Gets this stage
     * @return Returns the stage
     */
    private Stage getThisStage() {
        return (Stage) tfUserName.getScene().getWindow();
    }
}
