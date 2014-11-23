/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s32a.Server;

import s32a.Server.Person;
import s32a.Shared.ISpectator;

/**
 *
 * @author Kargathia
 */
public class Spectator extends Person implements ISpectator {

    Spectator(String name, double rating) {
        super(name, rating);
    }
}
