/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s32a.airhockey;

import java.util.ArrayList;

/**
 *
 * @author Kargathia
 */
public class DatabaseControls
{
    private String connection;
    
    /**
     * 
     */
    public DatabaseControls()
    {
        
    }
    
    /**
     * Checks whether provided playername and password correspond to an existing database item
     * @param playerName
     * @param password
     * @return 
     */
    public Person checkLogin(String playerName, String password)
    {
        return null;
    }
    
    /**
     * Adds a new Person to the database
     * @param playerName
     * @param password
     * @return the newly initialized Person if all went well
     * Null if person already exists or anything else went wrong
     */
    public Person addPerson(String playerName, String password)
    {
        return null;
    }
    
    /**
     * Retrieves a list of highest ranked players from the database
     * sorted descending by rating
     * @return the X highest rated players, sorted by rating
     */
    public ArrayList<Person> getRankings()
    {
        return null;
    } 
    
    /**
     * Removes a player with given player name from the database
     * Mostly useful for testing purposes
     * @param playerName
     * @return 
     */
    public boolean removePerson(String playerName)
    {
        return false;
    }
    
    /**
     * updates player's game history, and calculates his new rating
     * @param player
     * @param lastGame
     * @param hasLeft can be null
     * @return his new rating
     */
    public int getNewRating(Player player, Game lastGame, Player hasLeft)
    {
        return -1;
    }
}
