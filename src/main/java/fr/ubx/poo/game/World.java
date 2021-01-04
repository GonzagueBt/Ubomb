/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.game;

import fr.ubx.poo.model.decor.Decor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;

public class World {
    private Map<Position, Decor> grid;
    private WorldEntity[][] raw;
    private ArrayList<Map<Position, Decor>> gridLevels;
    private ArrayList<WorldEntity[][]> rawLevels;
    public Dimension dimension;
    public final String name = "src/main/resources/sample/level";
    private boolean changed = true;

    public World(int level) {
        String num = ""+level;
        this.raw = Parser.start(name+num+".txt");
        this.gridLevels = new ArrayList<>();
        this.rawLevels = new ArrayList<>();
        gridLevels.add(null); // pour que l'indice dans la liste soit égale au niveau
        rawLevels.add(null);
        dimension = new Dimension(raw.length, raw[0].length);
        grid = WorldBuilder.build(raw, dimension);
        gridLevels.add(grid);
        rawLevels.add(raw);
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

    public Position startPlayer() throws PositionNotFoundException {
        for (int x = 0; x < dimension.width; x++) {
            for (int y = 0; y < dimension.height; y++) {
                if (raw[y][x].equals(WorldEntity.DoorPrevOpened)){
                    return new Position(x, y);
                }
            }
        }
        throw new PositionNotFoundException("Player");
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

    public Collection<Decor> values() {
        return grid.values();
    }

    public boolean isInside(Position position) {
        if(position.x<0 || position.x >= dimension.width || position.y<0 || position.y>= dimension.height){
            return false;
        }
        return true; // to update
    }

    public boolean isEmpty(Position position) {
        return grid.get(position) == null;
    }

    public boolean isDecor(Position position){
        if(isEmpty(position)) return false;
        Decor decor = grid.get(position);
        return decor.cantBeOn(decor);
    }

    public boolean isMonster(Position position){
        if(isEmpty(position)) return false;
        Decor decor = grid.get(position);
        return decor.isMonster(decor);
    }

    public void update(int level, int old){
        gridLevels.set(old, this.grid);
        rawLevels.set(old, this.raw);
        if(level< old){
            this.grid= gridLevels.get(level);
            this.raw= rawLevels.get(level);
            this.dimension =  new Dimension(raw.length, raw[0].length);
        }
        else if(level > old) {
            String num = ""+level;
            this.raw = Parser.start(name+num+".txt");
            this.dimension =  new Dimension(raw.length, raw[0].length);
            this.grid = WorldBuilder.build(raw, dimension);
            gridLevels.add(grid);
            rawLevels.add(raw);
        }
    }
}
