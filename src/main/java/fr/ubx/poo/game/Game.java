/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.game;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import fr.ubx.poo.model.go.character.Monster;
import fr.ubx.poo.model.go.character.Player;

public class Game {

    private final World world;
    private final Player player;
    private ArrayList<Monster> monsters = new ArrayList<>();
    private final String worldPath;
    public int initPlayerLives;
    public int numberlevel;
    private int actualLevel=1;
    private boolean changeWorld = false;

    public Game(String worldPath) {
        this.worldPath = worldPath;
        loadConfig(worldPath);
        world = new World(actualLevel);
        Position positionPlayer = null;
        try {
            if(actualLevel==1) positionPlayer = world.findPlayer();
            else positionPlayer = world.startPlayer();
            player = new Player(this, positionPlayer);
            findMonsters();
        } catch (PositionNotFoundException e) {
            System.err.println("Position not found : " + e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    public void findMonsters(){
        for (int x = 0; x < getWorld().dimension.width; x++) {
            for (int y = 0; y < getWorld().dimension.height; y++) {
                if (getWorld().getRaw()[y][x].equals(WorldEntity.Monster)){
                    monsters.add(new Monster(this, new Position(x, y)));
                }
            }
        }
    }

    public boolean isChangeWorld() {
        return changeWorld;
    }

    public void setChangeWorld(boolean changeWorld) {
        this.changeWorld = changeWorld;
    }

    public int getInitPlayerLives() {
        return initPlayerLives;
    }

    public ArrayList<Monster> getMonsters() {
        return monsters;
    }

    private void loadConfig(String path) {
        try (InputStream input = new FileInputStream(new File(path, "config.properties"))) {
            Properties prop = new Properties();
            // load the configuration file
            prop.load(input);
            initPlayerLives = Integer.parseInt(prop.getProperty("lives", "3"));
            numberlevel = Integer.parseInt(prop.getProperty("levels", "3"));
        } catch (IOException ex) {
            System.err.println("Error loading configuration");
        }
    }

    public World getWorld() {
        return world;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void update (long now){
        int oldLevel = actualLevel;
        if(player.isNextOpenDoor()) actualLevel ++;
        if(player.isPrevOpenDoor()) actualLevel = actualLevel-1;
        //world.update(actualLevel, oldLevel);
        //findMonsters();
    }

}
