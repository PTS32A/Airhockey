/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Server;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import s32a.Server.Person;
import s32a.Shared.IGame;
import s32a.Shared.ISpectator;

/**
 *
 * @author Kargathia
 */
public class Spectator extends Person implements ISpectator {

    @Getter
    private List<IGame> myGames;

    Spectator(String name, double rating) throws RemoteException {
        super(name, rating);
        myGames = new ArrayList<>();
    }

    /**
     * if spectator is not already watching input, it will add it to his list.
     *
     * @param inputGame
     * @return
     */
    public boolean addGame(IGame inputGame) {
        if (inputGame == null) {
            return false;
        }
        Game input = (Game)inputGame;
        for (IGame game : myGames) {
            if (((Game)game).getGameInfo().get("gameID").equals(input.getGameInfo().get("gameID"))) {
                return false;
            }
        }
        return myGames.add(input);
    }

    /**
     * Denotes spectator is no longer watching given IGame.
     *
     * @param input
     * @return
     */
    public boolean removeGame(IGame input) {
        if (input == null) {
            return false;
        }
        return myGames.remove(input);
    }

    /**
     * @return game most recently added to the list - used to set up GameFX
     */
    public IGame getNewestGame() {
        if (myGames == null || myGames.size() < 1) {
            return null;
        }
        return myGames.get(myGames.size() - 1);
    }
}
