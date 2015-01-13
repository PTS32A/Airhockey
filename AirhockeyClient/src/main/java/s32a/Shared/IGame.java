/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Shared;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

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

    // -------------------- Getters of simple stats --------------------------

    /**
     * @return gameID expressed as string
     * @throws RemoteException
     */
    public String getID()
            throws RemoteException;

    /**
     * @return difficulty (puckspeed as string)
     * @throws RemoteException
     */
    public String getDifficulty()
            throws RemoteException;

    /**
     * @return player name if present, "-" otherwise
     * @throws RemoteException
     */
    public String getPlayer1Name()
            throws RemoteException;

    /**
     * @return player name if present, "-" otherwise
     * @throws RemoteException
     */
    public String getPlayer2Name()
            throws RemoteException;

    /**
     * @return player name if present, "-" otherwise
     * @throws RemoteException
     */
    public String getPlayer3Name()
            throws RemoteException;

    /**
     * @return game status expressed as string
     * @throws RemoteException
     */
    public String getStatus()
            throws RemoteException;

    /**
     * @return seconds left on countdown between rounds in this game.
     * range is 4 -> 0
     * -1 if no countdown currently in progress.
     * @throws RemoteException
     */
    public int getCountDownTime()
            throws RemoteException;

    /**
     * Returns game start time in millis.
     * @return
     * @throws RemoteException
     */
    public long getGameStartTime()
            throws RemoteException;
    
}
