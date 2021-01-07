/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.model.decor;

import fr.ubx.poo.model.Entity;

/***
 * A decor is an element that does not know its own position in the grid.
 */
public class Decor extends Entity {

    /**
     * cantBeOn looks this decor is a decor where a character can't be (in the same case)
     * so if this decor is a close doorr, a stone or a tree
     * @return a boolean
     */
    public boolean cantBeOn (){
        if(this instanceof Stone || this instanceof Tree || (this instanceof DoorCloseNext)) return true;
        return false;
    }

    /**
     * bombCanDestroy looks if this decor is a decor that can be destroy by a bomb
     * so if this decor is a door, a key, the princess, a tree or a stone a bomb can't destroy it
     * @return a boolean
     */
    public boolean bombCanDestroy (){
        if(isOpenNextDoor() || cantBeOn() || isOpenPrevDoor() || isPrincess() || isKey()) return false;
        return true;
    }

    /**
     * stopExplosion looks if this decor is a decor that stop an explosion
     * so if the decor is a door, a box, a tree or a stone
     * @return a boolean
     */
    public boolean stopExplosion(){
        if(cantBeOn() || isBox() || isOpenPrevDoor() || isOpenNextDoor()) return true;
        return false;
    }

    /**
     * methods to know what kind of decor this decor his
     */
    public boolean isHeart (){
        return this instanceof Heart;
    }
    public boolean isPrincess (){
        return this instanceof Princess;
    }
    public boolean isKey (){
        return this instanceof Key;
    }
    public boolean isBox () { return this instanceof Box; }
    public boolean isOpenPrevDoor(){
        return this instanceof DoorPrevOpened;
    }
    public boolean isCloseDoor (){
        return this instanceof DoorCloseNext;
    }
    public boolean isBNDec (){
        return this instanceof BombNumberDec;
    }
    public boolean isBNInc (){
        return this instanceof BombNumberInc;
    }
    public boolean isBRDec (){
        return this instanceof BombRangeDec;
    }
    public boolean isBRInc (){
        return this instanceof BombRangeInc;
    }
    public boolean isOpenNextDoor (){
        return this instanceof DoorNextOpened;
    }

}
