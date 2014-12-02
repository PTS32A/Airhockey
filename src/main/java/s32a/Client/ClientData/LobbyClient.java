/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Client.ClientData;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import javafx.collections.ObservableList;
import lombok.Getter;
import s32a.Shared.IGame;
import s32a.Shared.ILobbyClient;

/**
 *
 * @author Kargathia
 */
public class LobbyClient extends UnicastRemoteObject implements ILobbyClient {

    List<IGame> activeGames;
    @Getter
    ObservableList<IGame> oActiveGames;

    public LobbyClient() throws RemoteException {

    }


}
