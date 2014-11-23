/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Client.timers;

import javafx.animation.AnimationTimer;
import s32a.Server.Bot;
import s32a.Server.Game;
import s32a.Server.Player;
import s32a.Client.GUI.GameFX;
import s32a.Shared.IGame;

/**
 *
 * @author Luke
 */
public class GameTimer extends AnimationTimer {

    private final GameFX gameFX;
    private long refreshInMS;
    private long prevUpd;
    private long lastAction;
    private IGame myGame;

    public GameTimer(GameFX gameFX, Game game) {
        this.gameFX = gameFX;
        this.myGame = game;
        this.refreshInMS = 20;
        this.prevUpd = 0;
        this.lastAction = 0;
    }

    @Override
    public void handle(long now) {
        if (gameFX.isActionTaken()) {
            lastAction = now;
        }
        if (now - lastAction > 60000000000L) {
            gameFX.quitClick(null);
        }
//        if (now - prevUpd > refreshInMS * 1000000) {
//            gameFX.draw(myGame);
//            prevUpd = now;
//        }

        for (Player p : myGame.getMyPlayers()) {
            if (p instanceof Bot) {
                Bot b = (Bot) p;
                b.moveBot();
            }
        }
    }
}
