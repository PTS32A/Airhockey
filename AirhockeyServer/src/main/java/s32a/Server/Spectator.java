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

    /**
     * constructor
     * @param name The name of the spectator
     * @param rating The rating of the spectator
     * @throws RemoteException 
     */
    Spectator(String name, double rating) throws RemoteException {
        super(name, rating);
        myGames = new ArrayList<>();
    }

    /**
     * if spectator is not already watching input, it will add it to his list.
     *
     * @param inputGame The game to be added
     * @return Returns a boolean indicating the success of the addition
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
     * @param input The game to be removed
     * @return Returns a boolean indicating the success of the removal
     */
    public boolean removeGame(IGame input) {
        if (input == null) {
            return false;
        }
        return myGames.remove(input);
    }

    /**
     * equals method
     * @param other The other object to be compared with this
     * @return Returns a boolean indicating whether the compared objects are 
     * equal based on this' parent equals method
     */
    @Override
    public boolean equals(Object other) {
        return super.equals(other);
    }
}
