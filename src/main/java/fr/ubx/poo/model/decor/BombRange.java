package fr.ubx.poo.model.decor;

public class BombRange extends Decor{
    private boolean increase;

    public BombRange(boolean increase) {
        this.increase = increase;
    }

    public boolean isIncrease() {
        return increase;
    }
}
