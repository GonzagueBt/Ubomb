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
        if(isStone() || isTree() || this.isCloseDoor()) return true;
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
    public boolean isStone () { return this instanceof Stone; }
    public boolean isTree () { return this instanceof Tree; }
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
    public boolean isBNDec (){
        return this instanceof BombNumber && !((BombNumber) this).isIncrease();
    }
    public boolean isBNInc (){ return this instanceof BombNumber && ((BombNumber) this).isIncrease(); }
    public boolean isBRDec (){ return this instanceof BombRange && !((BombRange) this).isIncrease(); }
    public boolean isBRInc (){ return this instanceof BombRange && ((BombRange) this).isIncrease(); }
    public boolean isOpenNextDoor (){ return this instanceof Door && ((Door) this).isOpen() && ((Door) this).isNext(); }
    public boolean isOpenPrevDoor(){ return this instanceof Door && ((Door) this).isOpen() && !((Door) this).isNext(); }
    public boolean isCloseDoor (){ return this instanceof Door && !((Door) this).isOpen(); }
}
