/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import s32a.Client.GUI.GameFX;
import s32a.Shared.enums.GameStatus;

/**
 *
 * @author Kargathia
 */
public interface IGameClient extends Remote {

    /**
     * Sets the Game
     *
     * @param game The Game to be set to
     * @throws RemoteException
     */
    public void setGame(IGame game)
            throws RemoteException;

    /**
     * Ends the game - called when another participant has left the game
     * prematurely.
     *
     * @throws RemoteException
     */
    public void endGame()
            throws RemoteException;

    /**
     * Sets Players in the Game
     *
     * @param players A list of Players to be added
     * @throws RemoteException
     */
    public void setPlayers(List<IPlayer> players)
            throws RemoteException;

    /**
     * Sets Spectators in the Game
     *
     * @param spectators A list of Spectators to be added
     * @throws RemoteException
     */
    public void setSpectators(List<ISpectator> spectators)
            throws RemoteException;

    /**
     * Sets roundNo
     *
     * @param roundNo The number of the round
     * @throws RemoteException
     */
    public void setRoundNo(int roundNo)
            throws RemoteException;

    /**
     * Sets the ChatBox containing all sent messages
     *
     * @param chat A list of messages to be added to the ChatBox
     * @throws RemoteException
     */
    public void setChat(List<String> chat)
            throws RemoteException;

    /**
     * Sets the Puck's position using its X and Y coordinates
     *
     * @param x The X coordinate
     * @param y The Y coordinate
     * @throws RemoteException
     */
    public void setPuck(double x, double y)
            throws RemoteException;

    /**
     * Sets Player1's bat position using X and Y coordinates
     *
     * @param x The X coordinate
     * @param y The Y coordinate
     * @throws RemoteException
     */
    public void setPlayer1Bat(double x, double y)
            throws RemoteException;

    /**
     * Sets Player2's bat position using X and Y coordinates
     *
     * @param x The X coordinate
     * @param y The Y coordinate
     * @throws RemoteException
     */
    public void setPlayer2Bat(double x, double y)
            throws RemoteException;

    /**
     * Sets Player3's bat position using X and Y coordinates
     *
     * @param x The X coordinate
     * @param y The Y coordinate
     * @throws RemoteException
     */
    public void setPlayer3Bat(double x, double y)
            throws RemoteException;

    /**
     * Sets Player1's score
     *
     * @param score The score to be set to
     * @throws RemoteException
     */
    public void setPlayer1Score(int score)
            throws RemoteException;

    /**
     * Sets Player2's score
     *
     * @param score The score to be set to
     * @throws RemoteException
     */
    public void setPlayer2Score(int score)
            throws RemoteException;

    /**
     * Sets Player3's score
     *
     * @param score The score to be set to
     * @throws RemoteException
     */
    public void setPlayer3Score(int score)
            throws RemoteException;

    /**
     * Sets the difficulty of the Game
     *
     * @param difficulty The difficulty to be set to
     * @throws RemoteException
     */
    public void setDifficulty(String difficulty)
            throws RemoteException;

    /**
     * Sets the status of Game (i.e.: "Preparing", "Ready", "Paused", "Playing")
     *
     * @param status The status to be set to
     * @throws RemoteException
     */
    public void setStatus(GameStatus status)
            throws RemoteException;

    /**
     * Sets Game Time
     *
     * @param gameTime
     * @throws RemoteException
     */
    public void setGameTime(String gameTime)
            throws RemoteException;
    
    /**
     * Registers the GameFX of the client
     * 
     * @param fx
     * @throws RemoteException 
     */
    public void registerGameFX(GameFX fx)
            throws RemoteException;
}
