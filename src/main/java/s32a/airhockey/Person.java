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
    @Getter @Setter private double rating;
    @Getter @Setter private boolean isBot = false;
    
    /**
     * 
     * @param name
     * @param rating 
     */
    public Person(String name, Double rating)
    {
        if (name.equals("") || name == null || rating < 0 || rating == null) 
        {
            throw new IllegalArgumentException();
        }
        this.name = name;
        this.rating = rating;
    }
}
