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
    
    public void setPuckX(double x)
            throws RemoteException;
    
    public void setPuckY(double y)
            throws RemoteException;
    
    public void setPlayer1X(double x)
            throws RemoteException;
    
    public void setPlayer1Y(double y)
            throws RemoteException;
    
    public void setPlayer2X(double x)
            throws RemoteException;
    
    public void setPlayer2Y(double y)
            throws RemoteException;
    
    public void setPlayer3X(double x)
            throws RemoteException;
    
    public void setPlayer3Y(double y)
            throws RemoteException;
    
    public void setPlayer1Score(int score)
            throws RemoteException;
    
    public void setPlayer2Score(int score)
            throws RemoteException;
    
    public void setPlayer3Score(int score)
            throws RemoteException;
}
