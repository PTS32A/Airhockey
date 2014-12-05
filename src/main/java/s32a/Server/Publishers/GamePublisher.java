/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Server.Publishers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import s32a.Server.Game;
import s32a.Shared.IGameClient;
import s32a.Shared.IPlayer;
import s32a.Shared.ISpectator;

/**
 *
 * @author Kargathia
 */
public class GamePublisher {

    /**
     * Collection of observers of this game (Players + Spectators) Key = name
     */
    private HashMap<String, IGameClient> observers;

    /**
     * the Game associated with this publisher
     */
    private Game myGame;

    public GamePublisher(Game myGame) {
        this.observers = new HashMap<>();
        this.myGame = myGame;
    }

    /**
     * Tries to add an observer to the list. If the list already contains given
     * observer, return false;
     *
     * @param name
     * @param client
     * @return
     */
    public boolean addObserver(String name, IGameClient client) {
        if (this.observers.containsKey(name)) {
            return false;
        }
        return (this.observers.put(name, client) == null);
    }

    /**
     * Removes observer of given name from the list. Realistically will only be
     * called on a spectator leaving, as a player leaving triggers deletion of
     * the entire game.
     *
     * @param name
     */
    public void removeObserver(String name) {
        this.observers.remove(name);
    }

    public void setPlayers(List<IPlayer> players) {
    }

    ;

    public void setSpectators(List<ISpectator> spectators) {
    }

    ;

    public void setRoundNo(int roundNo) {
    }

    public void setChat(List<String> chat) {
    }

    public void setPuckX(double x) {
    }

    public void setPuckY(double y) {
    }

    public void setPlayer1X(double x) {
    }

    public void setPlayer1Y(double y) {
    }

    public void setPlayer2X(double x) {
    }

    public void setPlayer2Y(double y) {
    }

    public void setPlayer3X(double x) {
    }

    public void setPlayer3Y(double y) {
    }

    public void setPlayer1Score(int score) {
    }

    public void setPlayer2Score(int score) {
    }

    public void setPlayer3Score(int score) {
    }

}
