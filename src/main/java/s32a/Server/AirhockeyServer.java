/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Server;

import s32a.Shared.ILobby;

/**
 *
 * @author Kargathia
 */
public class AirhockeyServer {

    private static Lobby _instance;
    
    public static ILobby getInstance(){
        if(_instance == null){
            _instance = new Lobby();
        }
        return _instance;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
    }

}
