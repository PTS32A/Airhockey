/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Client.ClientData;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import s32a.Shared.IGameClient;

/**
 *
 * @author Kargathia
 */
public class GameClient extends UnicastRemoteObject implements IGameClient{

    public GameClient() throws RemoteException{

    }

}
