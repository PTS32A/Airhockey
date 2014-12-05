/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 *
 * @author Kargathia
 */
public interface IGameClient extends Remote{

    public void setGame(IGame game)
            throws RemoteException;
    
    public void setPlayer(List<IPlayer> players)
            throws RemoteException;
    
    public void setSpectators(List<ISpectator> spectators)
            throws RemoteException;
    
    public void setRoundNo(int roundNo)
            throws RemoteException;
    
    public void setChat(List<String> chat)
            throws RemoteException;
    
    public void setPuck(double x, double y)
            throws RemoteException;
    
    public void setPlayer1Bat(double x, double y)
            throws RemoteException;
    
    public void setPlayer2Bat(double x, double y)
            throws RemoteException;
    
    public void setPlayer3Bat(double x, double y)
            throws RemoteException;
    
    public void setPlayer1Score(int score)
            throws RemoteException;
    
    public void setPlayer2Score(int score)
            throws RemoteException;
    
    public void setPlayer3Score(int score)
            throws RemoteException;

    public void setDifficulty(String difficulty)
            throws RemoteException;
}
