package fr.ubx.poo.model.decor;

public class DoorNext extends Decor {
    private boolean isOpen = false;
    @Override
    public String toString() {
        return "DoorNext";
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }
}
