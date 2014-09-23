/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s32a.airhockey;

import com.badlogic.gdx.math.Vector2;
import java.util.List;
import lombok.Getter;

/**
 *
 * @author Kargathia
 */
public class Puck
{
    @Getter private Vector2 position;
    @Getter private float speed;
    @Getter private List<Player> hitBy;
    @Getter private float direction;
    
    
    /**
     * 
     * @param speed 
     */
    public Puck(float speed)
    {
        
    }
    
    /**
     * 
     * @return 
     */
    public boolean start()
    {
        
    }
    
    /**
     * 
     * @return 
     */
    public boolean stop()
    {
        
    }
    
    /**
     * 
     */
    private void run()
    {
        
    }
    
}
