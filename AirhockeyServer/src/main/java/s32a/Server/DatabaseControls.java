/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Server;

import s32a.Server.Player;
import s32a.Server.Person;
import s32a.Server.Game;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import s32a.Shared.IGame;
import s32a.Shared.IPerson;
import s32a.Shared.IPlayer;

/**
 *
 * @author Kargathia
 */
class DatabaseControls {

    private Connection conn;
    private Properties props;

    /**
     * creates new instance of DatabaseControls
     */
    public DatabaseControls() throws RemoteException {
        this.conn = null;
        this.props = null;
        try {
            this.configure();
        } catch (IOException | NullPointerException ex) {
            System.out.println("unable to configure database: check database.properties");
        }

    }

    /**
     * loads the configuration settings from file, and tests whether everything
     * works
     *
     * @throws IOException if something went wrong
     */
    public void configure() throws IOException, RemoteException {
        this.props = new Properties();
        try (FileInputStream in = new FileInputStream(
                "database.properties")) {
            props.load(in);
        } catch (FileNotFoundException ex) {
            throw new IOException("File not found: " + ex.getMessage());
        } catch (IOException ex) {
            throw new IOException("IOException on props load: " + ex.getMessage());
        }

        try {
            this.initConnection();
        } catch (SQLException ex) {
            throw new IOException("SQL Exception on initConnection: " + ex.getMessage());
        } finally {
            this.closeConnection();
        }
    }

    /**
     * Makes a connection to the database
     *
     * @throws SQLException
     */
    private void initConnection() throws SQLException, RemoteException {
        if (props.get("url") == null || props.get("username") == null
                || props.get("password") == null) {
            throw new SQLException("props values not correctly configured");
        }

        String url = (String) props.get("url");
        String username = (String) props.get("username");
        String password = (String) props.get("password");

        this.conn = DriverManager.getConnection(url, username, password);
    }

    /**
     * closes currently active connection
     */
    private void closeConnection() {
        try {
            conn.close();
            conn = null;
        } catch (SQLException ex) {
            System.err.println("closeConnection: " + ex.getMessage());
        }
    }

    /**
     * Checks whether provided playername and password correspond to an existing
     * database item
     *
     * @param playerName The name of the player
     * @param password The password of the player
     * @return Returns a person based on the player name and password if existing
     * @throws java.sql.SQLException
     */
    public IPerson checkLogin(String playerName, String password) throws SQLException, RemoteException {
        this.initConnection();
        Person output = null;

        PreparedStatement prepStat = null;
        String query = "SELECT playername, rating FROM player WHERE playername = ? AND playerpassword = ?";

        // checks with the database whether that username / password combination exists
        try {
            prepStat = conn.prepareStatement(query);
            prepStat.setString(1, playerName);
            prepStat.setString(2, password);

            ResultSet rs = prepStat.executeQuery();
            while (rs.next()) {
                if (output != null) {
                    throw new SQLException("multiple players found");
                }
                double rating = rs.getDouble("rating");
                String name = rs.getString("playername");
                output = new Person(name, rating);
            }
            return output;
        } finally {
            prepStat.close();
            this.closeConnection();
        }
    }

    /**
     * Adds a new Person to the database
     *
     * @param playerName The name of the player
     * @param password The password of the player
     * @return the newly added person, if applicable
     * @throws java.sql.SQLException
     */
    public IPerson addPerson(String playerName, String password) throws SQLException, RemoteException {
        this.initConnection();

        PreparedStatement prepStat = null;
        String query = "SELECT playername FROM player WHERE playername = ?";
        try {
            prepStat = this.conn.prepareStatement(query);
            prepStat.setString(1, playerName);
            ResultSet rs = prepStat.executeQuery();
            while (rs.next()) {
                return null;
            }

            query = "INSERT INTO dbi293443.player (playername, playerpassword, rating) VALUES (?, ?, ?)";

            prepStat = this.conn.prepareStatement(query);
            prepStat.setString(1, playerName);
            prepStat.setString(2, password);
            prepStat.setDouble(3, 15);
            prepStat.executeUpdate();
        } finally {
            prepStat.close();
            this.closeConnection();
        }
        return this.checkLogin(playerName, password);
    }

    /**
     * Retrieves a list of highest ranked players from the database sorted
     * descending by rating
     *
     * @return the X highest rated players, sorted by rating
     * @throws java.sql.SQLException
     */
    public List<IPerson> getRankings() throws SQLException, RemoteException {
        List<IPerson> output = new ArrayList<>();
        String query = "SELECT playername, rating FROM player ORDER BY rating DESC LIMIT 5";
        Statement stat = null;

        try {
            this.initConnection();
            stat = conn.createStatement();

            ResultSet rs = stat.executeQuery(query);
            while (rs.next()) {
                String name = rs.getString("playername");
                double rating = rs.getDouble("rating");
                output.add(new Person(name, rating));
            }
        } finally {
            if (stat != null) {
                stat.close();
            }
            this.closeConnection();
        }
        return output;
    }

    /**
     * Clears the entire database Mostly useful for testing purposes
     *
     * @throws java.sql.SQLException
     */
    public void clearDatabase() throws SQLException, RemoteException {
        String query = "DELETE FROM game";
        Statement stat = null;

        try {
            this.initConnection();
            stat = conn.createStatement();
            stat.executeUpdate(query);

            query = "DELETE FROM player";
            stat.executeUpdate(query);
        } finally {
            stat.close();
            this.closeConnection();
        }
    }

    /**
     * Saves a game to the database - currently gamedate is set as current date,
     * as game does not save gamedate yet. Does not adjust scores - that's
     * lobby's job
     *
     * @param game The game to be saved
     * @throws SQLException
     * @throws IllegalArgumentException when game doesn't have three players
     */
    public void saveGame(IGame gameInput) throws SQLException, IllegalArgumentException, RemoteException {
        if (gameInput == null) {
            throw new IllegalArgumentException("input was null");
        }
        Game game = (Game) Lobby.getSingle().getActiveGames().get(gameInput.getID());
        if (game.getMyPlayers().size() < 3) {
            throw new IllegalArgumentException("Game contained less than three players");
        }

        if (((Player) game.getMyPlayers().get(0)).getScore().get() < 0
                || ((Player) game.getMyPlayers().get(1)).getScore().get() < 0
                || ((Player) game.getMyPlayers().get(2)).getScore().get() < 0) {
            throw new IllegalArgumentException("One or more players had negative scores");
        }

        this.initConnection();
        PreparedStatement prepStat = null;
        String query = "INSERT INTO game (gameid, gamedate, "
                + "player1score, player2score, player3score, "
                + "player1, player2, player3, puckspeed) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            prepStat = this.conn.prepareStatement(query);

            prepStat.setString(1, game.getID());
            java.util.Date utilDate = new java.util.Date();
            java.sql.Timestamp sqlDate = new java.sql.Timestamp(utilDate.getTime());
            prepStat.setTimestamp(2, sqlDate);

            prepStat.setInt(3, ((Player) game.getMyPlayers().get(0)).getScore().get());
            prepStat.setInt(4, ((Player) game.getMyPlayers().get(1)).getScore().get());
            prepStat.setInt(5, ((Player) game.getMyPlayers().get(2)).getScore().get());

            prepStat.setString(6, game.getMyPlayers().get(0).getName());
            prepStat.setString(7, game.getMyPlayers().get(1).getName());
            prepStat.setString(8, game.getMyPlayers().get(2).getName());

            prepStat.setFloat(9, game.getMyPuck().getSpeed().get());

            prepStat.executeUpdate();
        } finally {
            prepStat.close();
            this.closeConnection();
        }
    }

    /**
     * updates player's game history, and calculates his new rating player
     * scores should have been adjusted before this
     *
     * @param player The player whose values should be updated
     * @param hasLeft can be null
     * @return his new rating
     * @throws java.sql.SQLException
     */
    public double getNewRating(IPerson player, IPerson hasLeft) throws SQLException, RemoteException {
        this.initConnection();
        double output = -1;
        try (CallableStatement callStat = conn.prepareCall("{? = call getNewRating(?, ?)}")) {
            callStat.registerOutParameter(1, java.sql.Types.DOUBLE);
            callStat.setString(2, player.getName());
            if (hasLeft != null) {
                callStat.setString(3, hasLeft.getName());
            } else {
                callStat.setNull(3, Types.VARCHAR);
            }
            callStat.execute();
            output = callStat.getInt(1);
        } finally {
            this.closeConnection();
        }
        return output;
    }
}
