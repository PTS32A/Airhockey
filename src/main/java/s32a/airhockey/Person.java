/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s32a.airhockey;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Kargathia
 */
public class Person
{
    @Getter private String name;
    @Getter @Setter private int rating;
    @Getter @Setter private boolean isBot = false;
    
    /**
     * 
     * @param name
     * @param rating 
     */
    Person(String name, int rating)
    {
        this.name = name;
        this.rating = rating;
    }
}
