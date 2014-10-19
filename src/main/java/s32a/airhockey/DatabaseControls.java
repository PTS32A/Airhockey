/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s32a.airhockey;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kargathia
 */
public class DatabaseControls
{
    private Connection conn;
    private Properties props;
    
    /**
     * creates new instance of DatabaseControls
     */
    public DatabaseControls()
    {
        this.conn = null;
        this.props = null;
        try
        {
            this.configure();
        } catch (IOException | NullPointerException ex)
        {
            System.out.println("unable to configure database: check database.properties");
        }
        
    }
    
    /**
     * loads the configuration settings from file, and tests whether everything works
     * @throws IOException if something went wrong
     */
    public void configure() throws IOException
    {
        this.props = new Properties();
        try (FileInputStream in = new FileInputStream("database.properties")) 
        {
            props.load(in);
        } catch (FileNotFoundException ex)
        {
            throw new IOException("File not found: " + ex.getMessage());
        } catch (IOException ex)
        {
            throw new IOException("IOException on props load: " + ex.getMessage());
        }
            
        try
        {
            this.initConnection();
        } catch (SQLException ex)
        {
            throw new IOException("SQL Exception on initConnection: " + ex.getMessage());
        }
        finally
        {
            this.closeConnection();
        }
    }
    
    /**
     * Makes a connection to the database 
     * @throws SQLException 
     */
    private void initConnection() throws SQLException 
    {
        if(props.get("url") == null || props.get("username") == null 
                || props.get("password") == null)
        {
            throw new SQLException("props values not correctly configured");
        }
        
        String url = (String)props.get("url");
        String username = (String)props.get("username");
        String password = (String)props.get("password");

        this.conn = DriverManager.getConnection(url, username, password);
    }
    
    /**
     * closes currently active connection
     */
    private void closeConnection() 
    {
        try {
            conn.close();
            conn = null;
        } catch (SQLException ex) {
            System.err.println("closeConnection: " + ex.getMessage());
        }
    }
    
    /**
     * Checks whether provided playername and password correspond to an existing database item
     * @param playerName
     * @param password
     * @return 
     * @throws java.sql.SQLException 
     */
    public Person checkLogin(String playerName, String password) throws SQLException
    {
        this.initConnection();
        Person output = null;
        
        PreparedStatement prepStat = null;
        String query = "SELECT playername, rating FROM player WHERE playername = ? AND playerpassword = ?";
        
        
        // checks with the database whether that username / password combination exists
        try
        {
            prepStat = conn.prepareStatement(query);
            prepStat.setString(1, playerName);
            prepStat.setString(2, password);
        
            ResultSet rs = prepStat.executeQuery();
            while (rs.next())
            {
                if(output != null)
                {
                    throw new SQLException("multiple players found");
                }
                int rating = rs.getInt("rating");
                String name = rs.getString("playername");
                output = new Person(name, rating);
            }
            return output;
        }
        finally
        {
            prepStat.close();
            this.closeConnection();
        }       
    }
    
    /**
     * Adds a new Person to the database
     * @param playerName
     * @param password
     * @return the newly added person, if applicable
     * @throws java.sql.SQLException 
     */
    public Person addPerson(String playerName, String password) throws SQLException
    {
        this.initConnection();
        
        PreparedStatement prepStat = null;
        String query = "SELECT playername FROM player WHERE playername = ?";
        try
        {
            prepStat = this.conn.prepareStatement(query);
            prepStat.setString(1, playerName);
            ResultSet rs = prepStat.executeQuery();
            while (rs.next())
            {
                return null;
            }
     
        query = "INSERT INTO player (playername, playerpassword, rating) VALUES (?, ?, ?)";

            prepStat = this.conn.prepareStatement(query);
            prepStat.setString(1, playerName);
            prepStat.setString(2, password);
            prepStat.setInt(3, 15);
            prepStat.executeUpdate();
        }
        finally
        {
            prepStat.close();
            this.closeConnection();
        }
        return this.checkLogin(playerName, password);
    }
    
    /**
     * Retrieves a list of highest ranked players from the database
     * sorted descending by rating
     * @return the X highest rated players, sorted by rating
     * @throws java.sql.SQLException
     */
    public List<Person> getRankings() throws SQLException
    {        
        List<Person> output = new ArrayList<>();
        String query = "SELECT playername, ranking FROM player SORT BY ranking DESC";
        Statement stat = null;
        
        try
        {
            this.initConnection();
            stat = conn.createStatement();

            ResultSet rs = stat.executeQuery(query);
            while (rs.next())
            {
                String name = rs.getString("playername");
                int rating = rs.getInt("ranking");
                output.add(new Person(name, rating));
            } 
        }
        finally
        {
            if (stat != null) 
            {
                stat.close();
            }
            this.closeConnection();
        }
        return output;
    } 
    
    /**
     * Clears the entire database
     * Mostly useful for testing purposes
     * @throws java.sql.SQLException
     */
    public void clearDatabase() throws SQLException
    {
        String query = "DELETE FROM game";
        Statement stat = null;
        
        try
        {
            this.initConnection();
            stat = conn.createStatement();
            stat.executeUpdate(query);
            
            query = "DELETE FROM player";
            stat.executeUpdate(query);
        }
        finally
        {
            stat.close();
            this.closeConnection();
        }
    }
    
    /**
     * Saves a game to the database - currently gamedate is set as current date,
     * as game does not save gamedate yet.
     * Does not adjust scores - that's lobby's job
     * @param game
     * @throws SQLException
     * @throws IllegalArgumentException when game doesn't have three players
     */
    public void saveGame(Game game) throws SQLException, IllegalArgumentException
    {
        if(game.getMyPlayers().size() < 3)
        {
            throw new IllegalArgumentException("Game contained less than three players");
        }
        
        this.initConnection();
        PreparedStatement prepStat = null;
        String query = "INSERT INTO game (gameid, gamedate, "
                + "player1score, player2score, player3score, "
                + "player1, player2, player3, puckspeed) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"; 
        
        try
        {
            prepStat = this.conn.prepareStatement(query);
            
            prepStat.setString(1, (String)game.getGameInfo().get("gameID"));        
            java.util.Date utilDate = new java.util.Date();
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            prepStat.setDate(2, sqlDate);
            
            prepStat.setInt(3, game.getMyPlayers().get(0).getScore());
            prepStat.setInt(4, game.getMyPlayers().get(1).getScore());
            prepStat.setInt(5, game.getMyPlayers().get(2).getScore());
            
            prepStat.setString(6, game.getMyPlayers().get(0).getName());
            prepStat.setString(7, game.getMyPlayers().get(1).getName());
            prepStat.setString(8, game.getMyPlayers().get(2).getName());
            
            prepStat.setFloat(9, game.getMyPuck().getSpeed());
            
            prepStat.executeUpdate();
        }
        finally
        {
            prepStat.close();
            this.closeConnection();
        }
    }
    
    /**
     * updates player's game history, and calculates his new rating
     * player scores should have been adjusted before this
     * @param player
     * @param lastGame currently not used TODO: remove this
     * @param hasLeft can be null
     * @return his new rating
     * @throws java.sql.SQLException
     */
    public int getNewRating(Person player, Game lastGame, Player hasLeft) throws SQLException
    {
        this.initConnection();
        int output = -1;       
        CallableStatement callStat = null;        
        try
        {
            callStat = conn.prepareCall("{? = call getNewRating(?, ?)}");
            callStat.registerOutParameter(1,java.sql.Types.INTEGER);
            callStat.setString(2, player.getName());
            if(hasLeft != null)
            {
                callStat.setString(3, hasLeft.getName());
            }
            else
            {
                callStat.setNull(3, Types.VARCHAR);
            }
            callStat.execute();
            output = callStat.getInt(1);
        }
        finally
        {
            callStat.close();
            this.closeConnection();
        }
        return output;       
    }
}
