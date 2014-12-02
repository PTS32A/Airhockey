/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Shared;

import java.util.List;
import javafx.collections.ObservableList;
import lombok.Getter;

/**
 *
 * @author Kargathia
 */
public interface ILobbyClient {
    
    public void setActiveGames(List<IGame> activeGames);
    
    public void setMyLobby(ILobby myLobby);
    
    public void setChat(List<String> chat);
    
    public void setRating(Double rating);
    
}
