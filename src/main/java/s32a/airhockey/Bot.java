/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s32a.airhockey;

import com.badlogic.gdx.math.Vector2;
import java.awt.Rectangle;
import java.util.Calendar;
import lombok.Getter;
import lombok.Setter;
/**
 * The AI class for Iteration 1 - methods for automating bat movement should go here
 * - WIP
 * @author Kargathia
 */
public class Bot extends Player
{
    @Getter private Vector2 batPos;
    @Getter private Colors color;
    @Getter private int score;
    @Getter private int rotation;
    @Getter private Vector2 goalPos;
    @Getter @Setter private Game myGame;
    @Getter private Rectangle rec;
    /**
     * 
     * @param name
     * @param rating
     * @param color
     */
    public Bot(String name, int rating, Colors color)
    {
        super(name,rating,color);
        this.goalPos = (Vector2)Lobby.getSingle().getAirhockeySettings().get("Goal Default");
        float sideLength = (float)Lobby.getSingle().getAirhockeySettings().get("Side Length");
        int batWidth = (int)(sideLength/100*8);
        this.batPos = new Vector2(goalPos.x, goalPos.y + 5);
        rec = new Rectangle((int)batPos.x, (int)batPos.y, batWidth, batWidth);
        this.score = 0;
    }
    
    
}
