/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Kargathia
 */
public interface ILobby extends Remote {
    
    /**
     * Gets the Settings of the Application
     * @return Returns a HashMap containing the Settings
     * @throws RemoteException 
     */
    public Map getRMIAirhockeySettings()
            throws RemoteException;

    /**
     * @param name
     * @return the up-to-date version of the person with the given name.
     * @throws RemoteException
     */
    public IPerson getMyPerson(String name)
            throws RemoteException;

    /**
     * Gets all activePersons in the Lobby
     * @return Returns a HashMap containing the Persons
     * @throws RemoteException 
     */
    public Map<String, IPerson> getRMIActivePersons()
            throws RemoteException;

    /**
     * Gets the active Games in the Lobby
     * @return Returns a list of active Games
     * @throws RemoteException 
     */
    public Map<String, IGame> getRMIActiveGames()
            throws RemoteException;

    /**
     * Adds a Person to the Database using a new unique playerName and a password
     * @param playerName The playerName of the Person
     * @param passWord  The password of the Person
     * @return Returns a boolean indicating success
     * @throws IllegalArgumentException
     * @throws SQLException
     * @throws RemoteException 
     */
    public boolean addPerson(String playerName, String passWord)
            throws IllegalArgumentException, SQLException, RemoteException;

    /**
     * Verifies the given playerName and password with an existing Account
     * @param playerName The playerName to be verified
     * @param password The password to be verified
     * @param client The LobbyClient the input is from
     * @return Returns a boolean indicating whether the information given
     * matches with an existing Account
     * @throws IllegalArgumentException
     * @throws SQLException
     * @throws RemoteException 
     */
    public boolean checkLogin(String playerName, String password, ILobbyClient client)
            throws IllegalArgumentException, SQLException, RemoteException;

    /**
     * Logs out a Person from the Lobby
     * @param playerName The Person to be logged out
     * @return
     * @throws RemoteException 
     */
    public boolean logOut(String playerName)
            throws RemoteException;

    /**
     * Starts the Game for a client
     * @param person The Person who is in the Game of this client
     * @param client The GameClient of which the Game should start
     * @return Returns the started Server Game containing all information
     * the client needs to play the Game
     * @throws RemoteException
     * @throws IllegalArgumentException 
     */
    public IGame startGame(String person, IGameClient client)
            throws RemoteException, IllegalArgumentException;

    /**
     * Lets a Person with corresponding GameClient join a Game
     * @param game The Game to be joined (gameID)
     * @param person The Person joining (name)
     * @param client The corresponding GameClient of the Person
     * @return Return the Server Game
     * @throws RemoteException
     * @throws IllegalArgumentException 
     */
    public IGame joinGame(String game, String person, IGameClient client)
            throws RemoteException, IllegalArgumentException;

    /**
     * Let's a Person with corresponding GameClient spectate a Game
     * @param gameInput The Game to be spectated (gameID)
     * @param personInput The Person spectating (name)
     * @param client The corresponding GameClient of the Person
     * @return
     * @throws RemoteException
     * @throws IllegalArgumentException 
     */
    public IGame spectateGame(String gameInput, String personInput, IGameClient client)
            throws RemoteException, IllegalArgumentException;

    /**
     * Adds a chat message to the Lobby's ChatBox
     * @param message The message to be added
     * @param from The name of the Person who sent the message
     * @return Return a boolean indicating success of the addition
     * @throws IllegalArgumentException
     * @throws RemoteException 
     */
    public boolean addChatMessage(String message, String from)
            throws IllegalArgumentException, RemoteException;

    /**
     * Ends the Game as a result of a Player leaving the Game
     * @param game The Game to be ended (gameID)
     * @param hasLeft The Player who left the Game (name)
     * @return
     * @throws RemoteException 
     */
    public boolean endGame(String game, String hasLeft)
            throws RemoteException;

    /**
     * Stops a Person from spectating a Game
     * @param game The spectated Game (gameID)
     * @param spectator The Person spectating the Game (name)
     * @throws RemoteException 
     */
    public void stopSpectating(String game, String spectator)
            throws RemoteException;

    /**
     * Gets a Game in the Lobby based on its gameID
     * @param gameID The gameID of the Game
     * @return Returns the Game
     * @throws RemoteException 
     */
    public IGame getMyGame(String gameID) throws RemoteException;

    /**
     * Gets the rankings of all existing Persons
     * @return Returns a list of all existing Persons, containing their rankings
     * @throws SQLException
     * @throws RemoteException 
     */
    public List<IPerson> getRankings()
            throws SQLException, RemoteException;

    /**
     * Populates the Lobby
     * @throws RemoteException 
     */
    public void populate()
            throws RemoteException;
}
