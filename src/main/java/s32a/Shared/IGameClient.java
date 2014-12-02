/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Shared;

import java.util.List;
import javafx.beans.property.IntegerProperty;
import javafx.collections.ObservableList;

/**
 *
 * @author Kargathia
 */
public interface IGameClient {
    
    public void setPlayer(List<IPlayer> players);
    
    public void setSpectators(List<ISpectator> spectators);
    
    public void setRoundNo(Integer roundNo);
    
    public void setChat(List<String> chat);
    
}
