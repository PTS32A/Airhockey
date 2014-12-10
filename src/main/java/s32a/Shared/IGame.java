/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Shared;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableNumberValue;
import javafx.collections.ObservableList;
import s32a.Shared.enums.GameStatus;

/**
 *
 * @author Kargathia
 */
public interface IGame extends Remote, Serializable {

    /**
     * Used in between rounds to allow or disallow the continuation of Puck movement.
     * @param input boolean value to set continueRun
     * @throws RemoteException 
     */
    public void setContinueRun(boolean input)
            throws RemoteException;

    /**
     * Adds a chat message to the ChatBox
     * @param message The message to be added
     * @param from A String containing the name of the Person who sent the message
     * @return Returns a boolean to indicate success
     * @throws RemoteException 
     */
    public boolean addChatMessage(String message, String from)
            throws RemoteException;

    /**
     * Used to start a Game for the first time.
     * Starts the first round.
     * @return Returns a boolean to indicate success
     * @throws RemoteException 
     */
    public boolean beginGame()
            throws RemoteException;

    /**
     * Adjusts the difficulty of the Game by changing the Puck's speed
     * @param puckSpeed The speed to be set to
     * @return Returns a boolean to indicate success
     * @throws RemoteException 
     */
    public boolean adjustDifficulty(float puckSpeed)
            throws RemoteException;

    /**
     * Adjusts the difficulty of the Game
     * @return Returns a boolean to indicate success
     * @throws RemoteException 
     */
    public boolean adjustDifficulty()
            throws RemoteException;

    /**
     * Pauses or unpaused the Game based on the parameter
     * @param isPaused Boolean whether the Game should be paused or unpaused
     * @return Returns a boolean to indicate success
     * @throws RemoteException 
     */
    public boolean pauseGame(boolean isPaused)
            throws RemoteException;

    /**
     * Starts a new round
     * @throws RemoteException 
     */
    public void startRound()
            throws RemoteException;
    
}
