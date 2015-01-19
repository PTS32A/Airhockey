/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Client.ClientData;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import s32a.Client.GUI.AirhockeyGUI;
import static s32a.Client.GUI.AirhockeyGUI.me;
import s32a.Client.Startup.ClientMain;
import s32a.Shared.IGame;
import s32a.Shared.IGameClient;
import s32a.Shared.ILobby;
import s32a.Shared.ILobbyClient;
import s32a.Shared.IPerson;

/**
 *
 * @author Kargathia
 */
public class LobbyClient extends UnicastRemoteObject implements ILobbyClient, ILobby {

    @Getter
    private ObservableList<String> oChatList;
    @Getter
    private ObservableList<IPerson> oRankingsList;
    @Getter
    private ObservableList<IGame> oActiveGamesList;
    @Getter
    private ObservableList<String> playerInfo;

    private ILobby myLobby;
    private AirhockeyGUI gui;

    /**
     * Constructor
     * @param gui
     * @param myLobby
     * @throws RemoteException
     */
    public LobbyClient(AirhockeyGUI gui, ILobby myLobby) throws RemoteException {
        if (myLobby == null) {
            throw new RemoteException();
        }
        this.myLobby = myLobby;
        this.gui = gui;
        this.oRankingsList = FXCollections.observableArrayList(new ArrayList<IPerson>());
        this.oChatList = FXCollections.observableArrayList(new ArrayList<String>());
        this.playerInfo = FXCollections.observableArrayList(new ArrayList<String>());
        this.oActiveGamesList = FXCollections.observableArrayList(new ArrayList<>());

        this.playerInfo.add("Name: " + me);
        this.playerInfo.add("Rating: -5");
    }

    /**
     * Returns an existing player in the lobby based on given player name
     * @param playerName
     * @return Returns a player with the given name
     * returns null if no player with the given name is found
     * @throws RemoteException 
     */
    @Override
    public IPerson getMyPerson(String playerName) throws RemoteException {
        return myLobby.getMyPerson(playerName);
    }

    /**
     * Returns up-to-date data - actual RMI query, and not from local stub.
     *
     * @return Returns a Map containing games and their names
     * @throws RemoteException
     */
    @Override
    public Map<String, IGame> getRMIActiveGames() throws RemoteException {
        return myLobby.getRMIActiveGames();
    }

    /**
     * Returns up-to-date data - actual RMI query, and not from local stub.
     *
     * @return Returns a Map containing the game's setting as objects and 
     * strings for naming
     * @throws RemoteException
     */
    @Override
    public Map<String, Object> getRMIAirhockeySettings() throws RemoteException {
        return myLobby.getRMIAirhockeySettings();
    }

    /**
     * Returns up-to-date data - actual RMI query, and not from local stub.
     *
     * @return Returns a Map containing all active persons and their names
     * @throws RemoteException
     */
    @Override
    public Map<String, IPerson> getRMIActivePersons() throws RemoteException {
        return myLobby.getRMIActivePersons();
    }

    /**
     * Adds a person to the Lobby based on their player name and password
     * @param playerName The name of the player to be added
     * @param passWord The matching password of the player to be added
     * @return Returns a boolean indicating whether the addition was successful
     * @throws IllegalArgumentException
     * @throws SQLException
     * @throws RemoteException 
     */
    @Override
    public boolean addPerson(String playerName, String passWord)
            throws IllegalArgumentException, SQLException, RemoteException {
        return myLobby.addPerson(playerName, passWord);
    }

    /**
     * Verifies a given player name and password combination with existing
     * accounts
     * @param playerName The player name to be verified
     * @param password The password to be verified
     * @param client The lobbyclient coming from the client trying to log in
     * @return Returns a boolean indicating whether the given combination is
     * matches with an existing account
     * @throws IllegalArgumentException
     * @throws SQLException
     * @throws RemoteException 
     */
    @Override
    public boolean checkLogin(String playerName, String password, ILobbyClient client)
            throws IllegalArgumentException, SQLException, RemoteException {
        return myLobby.checkLogin(playerName, password, client);
    }

    /**
     * Logs out a client from the server
     * @param input The name of the player to be logged out
     * @return Returns the success of logging out as a boolean
     * @throws RemoteException 
     */
    @Override
    public boolean logOut(String input) throws RemoteException {
        return myLobby.logOut(input);
    }

    /**
     * Starts a game in the lobby
     * @param person The name of the person that started the game
     * @param client The gameclient that requested to start the game
     * @return Returns the game that has started
     * @throws RemoteException 
     */
    @Override
    public IGame startGame(String person, IGameClient client) throws RemoteException {
        return myLobby.startGame(person, client);
    }

    /**
     * Lets a person join a game
     * @param game The identifying name of the game be joined
     * @param person The name of the person to be joining
     * @param client the gameclient of the client joining
     * @return Return the game to be joined
     * @throws RemoteException 
     */
    @Override
    public IGame joinGame(String game, String person, IGameClient client) throws RemoteException {
        return myLobby.joinGame(game, person, client);
    }

    /**
     * Lets a join a game as a spectator
     * @param gameInput The identifying name of the game to be joined
     * @param personInput The name of the person joining as a specatator
     * @param client The gameclient of the client joining
     * @return Returns the game to be joined as a spectator
     * @throws RemoteException 
     */
    @Override
    public IGame spectateGame(String gameInput, String personInput, IGameClient client)
            throws RemoteException {
        return myLobby.spectateGame(gameInput, personInput, client);
    }

    /**
     * Adds a chat message to the chatbox
     * @param message The message to be added
     * @param from The name of the person who sent the chat message
     * @return Returns a boolean indicating the success of the addition
     * @throws IllegalArgumentException
     * @throws RemoteException 
     */
    @Override
    public boolean addChatMessage(String message, String from) throws IllegalArgumentException, RemoteException {
        return myLobby.addChatMessage(message, from);
    }

    /**
     * Ends a game
     * @param game The name of the game to be ended
     * @param hasLeft The name of the player that left the game in order for
     * the game to end. Could be empty if there was no single player causing
     * the end of the game.
     * @return Returns a boolean indicating the success of ending the game
     * @throws RemoteException 
     */
    @Override
    public boolean endGame(String game, String hasLeft) throws RemoteException {
        return myLobby.endGame(game, hasLeft);
    }

    /**
     * Stops a specator from spectating a game
     * @param game The name of the game
     * @param spectator The name of the spectator
     * @throws RemoteException 
     */
    @Override
    public void stopSpectating(String game, String spectator) throws RemoteException {
        myLobby.stopSpectating(game, spectator);
    }

    /**
     * Gets a game based on a game id
     * @param gameID The game id of the wanted game
     * @return Returns a game in the lobby with the given game id
     * Returns null if no game with given game id was found
     * @throws RemoteException 
     */
    @Override
    public IGame getMyGame(String gameID) throws RemoteException {
        return myLobby.getMyGame(gameID);
    }

    /**
     * Gets the rankings of the lobby
     * @return Returns a list of persons, containing their rankings (scores)
     * @throws SQLException
     * @throws RemoteException 
     */
    @Override
    public List<IPerson> getRankings() throws SQLException, RemoteException {
        return this.oRankingsList;
    }

    //----------------------------------- INCOMING FROM SERVER -----------------------------------------------
    /**
     * Sets the given games active in the lobby
     * @param activeGames A Hashmap of games to be set active
     * @throws RemoteException 
     */
    @Override
    public synchronized void setActiveGames(HashMap<String, IGame> activeGames) throws RemoteException {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                oActiveGamesList.clear();
                oActiveGamesList.addAll(activeGames.values());
            }
        });

    }

    /**
     * Sets the lobby in the lobbyclient
     * @param myLobbyInput The lobby to be set to
     * @throws RemoteException 
     */
    @Override
    public synchronized void setMyLobby(ILobby myLobbyInput) throws RemoteException {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                myLobby = myLobbyInput;
            }
        });
    }

    /**
     * Sets the chatbox with already sent messages
     * @param chat A list of strings containing the chat messages that have
     * been sent
     * @throws RemoteException 
     */
    @Override
    public synchronized void setChat(List<String> chat) throws RemoteException {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                oChatList.setAll(chat);
            }
        });
    }

    /**
     * Sets the rankings in the lobby
     * @param persons A list of persons to set the rankings (score) of
     * @throws RemoteException 
     */
    @Override
    public synchronized void setRankings(List<IPerson> persons) throws RemoteException {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                oRankingsList.setAll(persons);
            }
        });
    }

    /**
     * Sets the ranking (score0 of a given person
     * @param person The person whose rankings are to be set
     * @throws RemoteException 
     */
    @Override
    public void setPersonRanking(IPerson person) throws RemoteException {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                try {
                    if (person.getName().equals(me)) {
                        playerInfo.set(0, "Name: " + me);
                        playerInfo.set(1, "Rating: " + String.valueOf(person.getRating()));
                    }
                }
                catch (RemoteException ex) {
                    System.out.println("remoteException on setPersonRanking: " + ex.getMessage());
                }
            }
        });

    }

    /**
     * Forcibly logs out client - in case of errors, duplicate logins -
     * generally to make sure he's really logged out.
     *
     * @throws RemoteException
     */
    @Override
    public void enforceLogout() throws RemoteException {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                ClientMain.launchClient();
            }
        });
    }

}
