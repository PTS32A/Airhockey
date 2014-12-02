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

    public void setContinueRun(boolean input);

    public boolean addChatMessage(String message, String from);

    public boolean beginGame();

    public boolean adjustDifficulty(float puckSpeed);

    public boolean adjustDifficulty();

    public boolean pauseGame(boolean isPaused);

    public void startRound();
}
