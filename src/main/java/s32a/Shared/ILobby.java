/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Shared;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Kargathia
 */
public interface ILobby {

    public IPerson getMyPerson(String playerName);

    public HashMap getAirhockeySettings();

    public HashMap<String, IPerson> getActivePersons();

    public List<IGame> getActiveGames();

    public boolean addPerson(String playerName, String passWord)
            throws IllegalArgumentException, SQLException;

    public boolean checkLogin(String playerName, String password)
            throws IllegalArgumentException, SQLException;

    public boolean logOut(IPerson input);

    public IGame startGame(IPerson person);

    public IGame joinGame(IGame game, IPerson person);

    public IGame spectateGame(IGame gameInput, IPerson personInput);

    public boolean addChatMessage(String message, String from)
            throws IllegalArgumentException;

    public boolean endGame(IGame game, IPlayer hasLeft);

    public void stopSpectating(IGame game, IPerson spectator);

    public IGame getMyGame(String gameID);

    public List<IPerson> getRankings()
            throws SQLException;

    public void populate();
}
