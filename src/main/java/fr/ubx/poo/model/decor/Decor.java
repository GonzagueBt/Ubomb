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
        if(decor instanceof Stone || decor instanceof Tree || (decor instanceof DoorCloseNext)) return true;
        return false;
    }

    public boolean bombCanDestroy (){
        if(isOpenNextDoor(this) || cantBeOn(this) || isOpenDoor(this) || isPrincess(this) || isKey(this)) return false;
        return true;
    }

    public boolean stopExplosion(){
        if(cantBeOn(this) || isBox(this) || isOpenDoor(this) || isOpenNextDoor(this)) return true;
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
    public boolean isBox (Decor decor) { return decor instanceof Box; }
    public boolean isOpenDoor (Decor decor){
        return decor instanceof DoorOpenPrev;
    }
    public boolean isCloseDoor (Decor decor){
        return decor instanceof DoorCloseNext;
    }
    public boolean isBNDec (Decor decor){
        return decor instanceof BombNumberDec;
    }
    public boolean isBNInc (Decor decor){
        return decor instanceof BombNumberInc;
    }
    public boolean isBRDec (Decor decor){
        return decor instanceof BombRangeDec;
    }
    public boolean isBRInc (Decor decor){
        return decor instanceof BombRangeInc;
    }
    public boolean isOpenNextDoor (Decor decor){
        return decor instanceof DoorCloseNext;
    }

}
