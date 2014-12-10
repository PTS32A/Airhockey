/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Client.timers;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import s32a.Client.ClientData.GameClient;
import s32a.Server.Bot;
import s32a.Client.GUI.GameFX;
import s32a.Shared.IGame;
import s32a.Shared.IPlayer;
import s32a.Shared.enums.GameStatus;

/**
 *
 * @author Luke
 */
public class GameTimer extends AnimationTimer {

    private final GameFX gameFX;
    private long lastAction;
    private long prevUpd;
    private int timer;

    public GameTimer(GameFX gameFX) {
        this.gameFX = gameFX;
        this.lastAction = 0;
        this.prevUpd = 0;
        this.timer = 3;
    }

    @Override
    public void handle(long now) {
        if (gameFX.isActionTaken()) {
            lastAction = now;
        }
        if (now - lastAction > 60000000000L) {
            gameFX.quitClick(null);
        }
        if (gameFX.getStatus().equals(GameStatus.Waiting) && now - prevUpd > 1000000000) {
            if (timer != 0) {
                gameFX.setCountdown(String.valueOf(timer));
                timer--;
            }
            else{
                gameFX.setCountdown("");
                try {
                    gameFX.nextRound();
                }
                catch (RemoteException ex) {
                    Logger.getLogger(GameTimer.class.getName()).log(Level.SEVERE, null, ex);
                }
                timer = 3;
            }
            prevUpd = now;
        }
        

        // bot movement should be done server side
//        for (IPlayer p : myGame.getMyPlayers()) {
//            if (p instanceof Bot) {
//                Bot b = (Bot) p;
//                b.moveBot();
//            }
//        }
    }
}
