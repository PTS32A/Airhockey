/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Client.GUI;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import s32a.Server.Lobby;

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
     * @throws java.sql.SQLException
     */
    public void registerOk(Event evt) throws IllegalArgumentException, SQLException {
        if (tfUserName.getText().equals("") || pwfPassword.getText().equals("")
                || pwfPasswordConfirm.getText().equals("")) {
            super.showDialog("Error", "One or more fields are empty");
        } else {
            if (!pwfPassword.getText().equals(pwfPasswordConfirm.getText())) {
                super.showDialog("Error", "Passwords do not match");
            } else {
                if (Lobby.getSingle().addPerson(tfUserName.getText(), pwfPassword.getText())) {
                    try {
                        super.showDialog("Success", "Welcome to Airhockey, " + tfUserName.getText() + "!");
                        super.goToLogin(getThisStage());
                    } catch (IOException ex) {
                        super.showDialog("Error", "Unable to open Lobby: " + ex.getMessage());
                    }
                } else {
                    super.showDialog("Error", "Username already exists.");
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
            super.showDialog("Error", "Unable to go to Login: " + ex.getMessage());
        }
    }

    private Stage getThisStage() {
        return (Stage) tfUserName.getScene().getWindow();
    }
}
