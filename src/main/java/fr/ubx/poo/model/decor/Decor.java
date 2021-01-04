/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.model.decor;

import fr.ubx.poo.model.Entity;

/***
 * A decor is an element that does not know its own position in the grid.
 */
public class Decor extends Entity {
        public boolean cantBeOn (Decor decor){
            if(decor instanceof Stone || decor instanceof Tree || (decor instanceof DoorNext && !((DoorNext) decor).isOpen())) return true;
            return false;
        }

        public boolean isHeart (Decor decor){
            return decor instanceof Heart;
        }

        public boolean isPrincess (Decor decor){
            return decor instanceof Princess;
        }

    public boolean isKey (Decor decor){
        return decor instanceof Key;
    }
}
