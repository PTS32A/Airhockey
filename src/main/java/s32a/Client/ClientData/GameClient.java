/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Client.ClientData;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import s32a.Shared.IGame;
import s32a.Shared.IGameClient;

/**
 *
 * @author Kargathia
 */
public class GameClient extends UnicastRemoteObject implements IGameClient, IGame{

    private IGame myGame;

    public GameClient() throws RemoteException{

    }

    @Override
    public void setContinueRun(boolean input) {
        myGame.setContinueRun(input);
    }

    @Override
    public boolean addChatMessage(String message, String from) {
        return myGame.addChatMessage(message, from);
    }

    @Override
    public boolean beginGame() {
        return myGame.beginGame();
    }

    @Override
    public boolean adjustDifficulty(float puckSpeed) {
        return myGame.adjustDifficulty(puckSpeed);
    }

    @Override
    public boolean adjustDifficulty() {
        return myGame.adjustDifficulty();
    }

    @Override
    public boolean pauseGame(boolean isPaused) {
        return myGame.pauseGame(isPaused);
    }

    @Override
    public void startRound() {
        myGame.startRound();
    }

}
