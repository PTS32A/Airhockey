/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Shared;

import java.util.HashMap;
import java.util.List;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import s32a.Shared.enums.GameStatus;

/**
 *
 * @author Kargathia
 */
public interface IGame {

    public ObjectProperty<GameStatus> getStatusProp();

    public IChatbox getMyChatbox();

    public IPuck getMyPuck();

    public void setGameTime(String input);

    public StringProperty difficultyProperty();

    public ObjectProperty<GameStatus> statusProperty();

    public StringProperty player1NameProperty();

    public StringProperty player2NameProperty();

    public StringProperty player3NameProperty();

    public List<ISpectator> getMySpectators();

    public List<IPlayer> getMyPlayers();

    public HashMap getGameInfo();

    public IntegerProperty getRoundNo();

    public boolean isContinueRun();

    public void setContinueRun(boolean input);

    public StringProperty getGameTime();

    public boolean addChatMessage(String message, String from);

    public boolean addPlayer(IPlayer player);

    public boolean addSpectator(ISpectator spectator)
            throws IllegalArgumentException;

    public boolean removeSpectator(ISpectator spectator);

    public boolean beginGame();

    public boolean adjustDifficulty(float puckSpeed);

    public boolean adjustDifficulty();

    public boolean pauseGame(boolean isPaused);

    public void startRound();


}
