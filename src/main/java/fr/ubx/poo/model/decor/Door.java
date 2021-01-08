package fr.ubx.poo.model.decor;

public class Door extends Decor{
    private boolean open; // open or close door
    private boolean next; // next or previous door

    public Door(boolean open, boolean next) {
        this.open = open;
        this.next = next;
    }

    public boolean isOpen() { return open; }
    public void setOpen(boolean open) { this.open = open; }
    public boolean isNext() { return next; }
}
