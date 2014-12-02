/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Shared;

import java.util.HashMap;
import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableNumberValue;
import javafx.collections.ObservableList;
import s32a.Shared.enums.GameStatus;

/**
 *
 * @author Kargathia
 */
public interface IGame {

    public void setContinueRun(boolean input);

    public boolean addChatMessage(String message, String from);

    public boolean beginGame();

    public boolean adjustDifficulty(float puckSpeed);

    public boolean adjustDifficulty();

    public boolean pauseGame(boolean isPaused);

    public void startRound();

    public List<IPlayer> getMyPlayers();

    public IntegerProperty getRoundNo();
    
    public IntegerProperty setRoundNo();

    public ObservableList<String> getChatProperty();
    
    public void setChatProperty(ObservableList<String> messages);
    
    public StringProperty getGameTime();

    public FloatProperty getPuckSpeed();

    public DoubleProperty getPuckXPos();

    public DoubleProperty getPuckYPos();

    public GameStatus getStatusProp();
    
}
