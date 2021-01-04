/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.game;

import fr.ubx.poo.model.decor.Decor;
import fr.ubx.poo.model.decor.DoorNext;
import fr.ubx.poo.model.decor.Stone;
import fr.ubx.poo.model.decor.Tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;

public class World {
    private final Map<Position, Decor> grid;
    private final WorldEntity[][] raw;
    private ArrayList<Map<Position, Decor>> levels;
    public final Dimension dimension;
    public final String name = "src/main/resources/sample/level";
    private boolean changed = true;

    public World(int level) {
        String num = ""+level;
        this.raw = Parser.start(name+num+".txt");
        this.levels = new ArrayList<>();
        levels.add(null); // pour que l'indice dans la liste soit égale au niveau
        dimension = new Dimension(raw.length, raw[0].length);
        grid = WorldBuilder.build(raw, dimension);
        levels.add(grid);
    }

    public Position findPlayer() throws PositionNotFoundException {
        for (int x = 0; x < dimension.width; x++) {
            for (int y = 0; y < dimension.height; y++) {
                if (raw[y][x] == WorldEntity.Player) {
                    return new Position(x, y);
                }
            }
        }
        throw new PositionNotFoundException("Player");
    }

    public WorldEntity[][] getRaw() {
        return raw;
    }

    public Decor get(Position position) {
        return grid.get(position);
    }

    public void set(Position position, Decor decor) {
        grid.put(position, decor);
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public void clear(Position position) {
        grid.remove(position);
        this.changed = true;
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
        WorldEntity Monster= WorldEntity.Monster;
        return raw[position.y][position.x].equals(Monster);
    }

    /*public void update(int level, int old){
        if(level< old){
            this.grid= levels.get(level);
        }
        else if(level > old) {
            String num = ""+level;
            this.raw = Parser.start(name+num+".txt");
            levels.add(raw);
        }
    }*/
}
