/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.game;

import fr.ubx.poo.model.decor.Decor;
import fr.ubx.poo.model.go.Bomb;
import fr.ubx.poo.model.go.character.Monster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;

public class World {
    private final Map<Position, Decor> grid;
    private final WorldEntity[][] raw;
    public Dimension dimension;
    public final String name = "src/main/resources/sample/level";
    private boolean changed = true;
    private final ArrayList<Monster> monsters = new ArrayList<>();
    private final ArrayList<Bomb> bombs = new ArrayList<>();

    public World(int level) {
        String num = ""+level;
        this.raw = Parser.start(name+num+".txt");
        dimension = new Dimension(raw.length, raw[0].length);
        grid = WorldBuilder.build(raw, dimension);
    }

    public Decor get(Position position) {
        return grid.get(position);
    }
    public WorldEntity[][] getRaw() {
        return raw;
    }
    public void set(Position position, Decor decor) {
        grid.put(position, decor);
        this.changed = true;
    }
    public boolean isChanged() {
        return changed;
    }
    public void setChanged(boolean changed) {
        this.changed = changed;
    }
    public void clear(Position position) {
        grid.remove(position);
    }
    public void forEach(BiConsumer<Position, Decor> fn) {
        grid.forEach(fn);
    }
    public ArrayList<Monster> getMonsters() { return monsters; }
    public ArrayList<Bomb> getBombs() { return bombs; }
    public Collection<Decor> values() {
        return grid.values();
    }

    public Position findPlayer() throws PositionNotFoundException {
        for (int x = 0; x < dimension.width; x++) {
            for (int y = 0; y < dimension.height; y++) {
                if (raw[y][x].equals(WorldEntity.Player)){
                    return new Position(x, y);
                }
            }
        }
        throw new PositionNotFoundException("Player");
    }

    public Position startPlayer(int i){
        WorldEntity door;
        if(i==0) door =WorldEntity.DoorPrevOpened;
        else door = WorldEntity.DoorCloseNext;
        for (int x = 0; x < dimension.width; x++) {
            for (int y = 0; y < dimension.height; y++) {
                if (raw[y][x].equals(door)){
                    return new Position(x, y);
                }
            }
        }
        return null;
    }

    public boolean isInside(Position position) {
        return position.x >= 0 && position.x < dimension.width && position.y >= 0 && position.y < dimension.height;
    }

    public boolean isEmpty(Position position) {
        return grid.get(position) == null;
    }

    public boolean isDecor(Position position){
        if(isEmpty(position)) return false;
        Decor decor = grid.get(position);
        return decor.cantBeOn(decor);
    }

    public boolean isNextOpenDoor(Position position){
        Decor decor = get(position);
        if(!isEmpty(position)) return decor.isOpenNextDoor(decor);
        return false;
    }

    public boolean isPrevOpenDoor(Position position){
        Decor decor = get(position);
        if(!isEmpty(position)) return decor.isOpenPrevDoor(decor);
        return false;
    }
}
