/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package s32a.airhockey;

import lombok.Getter;

/**
 *
 * @author Kargathia
 */
public class Spectator extends Person
{
    private Game game;
    
    Spectator(String name, double rating, Game game)
    {
        super(name, rating);
        this.game = game;
    }
}
