/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s32a.airhockey;

import com.badlogic.gdx.math.Vector2;
/**
 *
 * @author Kargathia
 */
public class Bot extends Player
{
    /**
     * 
     * @param name
     * @param rating
     * @param batPos
     * @param color
     * @param score
     * @param starter
     * @param AI
     * @param rotation
     * @param goalPos 
     */
    public Bot(String name, int rating, Vector2 batPos, String color, int score,
            boolean starter, boolean AI, int rotation, Vector2 goalPos)
    {
        super(name,rating,batPos,color,score,starter,AI,rotation,goalPos);
    }
}
