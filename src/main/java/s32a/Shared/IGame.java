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

    public void setContinueRun(boolean input)
            throws RemoteException;

    public boolean addChatMessage(String message, String from)
            throws RemoteException;

    public boolean beginGame()
            throws RemoteException;

    public boolean adjustDifficulty(float puckSpeed)
            throws RemoteException;

    public boolean adjustDifficulty()
            throws RemoteException;

    public boolean pauseGame(boolean isPaused)
            throws RemoteException;

    public void startRound()
            throws RemoteException;
    
}
