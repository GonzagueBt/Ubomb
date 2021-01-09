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
    private boolean changed = true; // to indicates if a decor have been change and if its necessary to modified the sprites
    private final ArrayList<Monster> monsters = new ArrayList<>(); // list of all monsters in this world at this moment
    private final ArrayList<Bomb> bombs = new ArrayList<>(); // list of all bombs in this world at this moment

    public World(int level) {
        String num = ""+level;
        this.raw = Parser.start(name+num+".txt");
        dimension = new Dimension(raw.length, raw[0].length);
        grid = WorldBuilder.build(raw, dimension);
    }

    // getters and setters //
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
    // getters and setters //

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

    /**
     * startPlayer allow to find the position of the player when he arrives in a new level, so the method search
     * the position of an open door (previous or following)
     * @param i is equal to 0 if the new level succeeds the actual one or 1 if the new level preceeds the actual one
     * @return a position in the new actual level
     */
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

    /**
     * isInside check if a position is on the world or outside the limits of the world
     * @param position is the position that we want to check
     * @return a boolean
     */
    public boolean isInside(Position position) {
        return position.x >= 0 && position.x < dimension.width && position.y >= 0 && position.y < dimension.height;
    }

    /**
     * isempty check if a position doesn't contain any decor
     * @param position is the position that we want to check
     * @return a boolean
     */
    public boolean isEmpty(Position position) {
        return grid.get(position) == null;
    }

    /**
     * isDecor check if the decor of a position can be browsed by a character
     * @param position is the position that we want to check
     * @return a boolean with the methods cantBeOn of the class Decor
     * @see Decor
     */
    public boolean isDecor(Position position){
        if(isEmpty(position)) return false;
        Decor decor = grid.get(position);
        return decor.cantBeOn();
    }

    /**
     * isMonster check if there is a Monster in the actual world (level) at the position's player
     * Used in Player, in method processMove to see if the Player loose a life
     * @see fr.ubx.poo.model.go.character.Player
     * @param position is the position that we want to check
     * @return a boolean
     */
    public boolean isMonster(Position position, int actualLevel){
        for(int monster=0 ; monster<monsters.size() ; monster++) {
            if(monsters.get(monster).getLevel()== actualLevel &&monsters.get(monster).getPosition().equals(position)) return true;
        }
        return false;
    }

    /**
     * isNextOpenDoor look if the decor of a position is a next open doOr
     * @param position is the position that we want check
     * @return a boolean
     */
    public boolean isNextOpenDoor(Position position){
        Decor decor = get(position);
        if(!isEmpty(position)) return decor.isOpenNextDoor();
        return false;
    }

    /**
     * IsPrevOpenDoor look if the decor of a position is a previous open door
     * @param position is the position that we want to check
     * @return a boolean
     */
    public boolean isPrevOpenDoor(Position position){
        Decor decor = get(position);
        if(!isEmpty(position)) return decor.isOpenPrevDoor();
        return false;
    }
}
