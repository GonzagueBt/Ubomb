package fr.ubx.poo.model.decor;

public class BombNumber extends Decor{
    private boolean increase;

    public BombNumber(boolean increase) {
        this.increase = increase;
    }

    public boolean isIncrease() {
        return increase;
    }
}
