/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Server.Publishers;

import java.util.List;
import s32a.Shared.IPlayer;
import s32a.Shared.ISpectator;

/**
 *
 * @author Kargathia
 */
public class GamePublisher {

    public void setPlayer(List<IPlayer> players){};

    public void setSpectators(List<ISpectator> spectators){};

    public void setRoundNo(int roundNo){}

    public void setChat(List<String> chat){}

    public void setPuckX(double x){}
    
    public void setPuckY(double y){}

    public void setPlayer1X(double x){}

    public void setPlayer1Y(double y){}

    public void setPlayer2X(double x){}

    public void setPlayer2Y(double y){}

    public void setPlayer3X(double x){}

    public void setPlayer3Y(double y){}

    public void setPlayer1Score(int score){}

    public void setPlayer2Score(int score){}

    public void setPlayer3Score(int score){}

}
