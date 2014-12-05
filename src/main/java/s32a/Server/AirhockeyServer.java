/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import s32a.Shared.ILobby;

/**
 *
 * @author Kargathia
 */
public class AirhockeyServer {

    private Lobby lobby;
    private static final int portNumber = 1099;
    private static final String bindingName = "AirhockeyServer";

    public AirhockeyServer(){

        try {
            lobby = Lobby.getSingle();
            System.out.println("Server: Lobby created");
        } catch (RemoteException ex) {
            System.out.println("Server: RemoteException: " + ex.getMessage());
            lobby = null;
        }

        // Bind using Naming
        if (lobby != null) {
            try {
                LocateRegistry.createRegistry(portNumber);
                Naming.rebind(bindingName, lobby);
            } catch (MalformedURLException ex) {
                System.out.println("Server: MalformedURLException: " + ex.getMessage());
            } catch (RemoteException ex) {
                System.out.println("Server: RemoteException: " + ex.getMessage());
            }
            System.out.println("Server: Lobby bound to " + bindingName);
        } else {
            System.out.println("Server: Lobby not bound");
        }
    }



}
