package fr.ubx.poo.model.decor;

public class Door extends Decor{
    private boolean open;
    private boolean next;

    public Door(boolean open, boolean next) {
        this.open = open;
        this.next = next;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isNext() {
        return next;
    }
}
