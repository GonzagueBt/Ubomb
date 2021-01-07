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

import fr.ubx.poo.model.go.Bomb;
import fr.ubx.poo.model.go.character.Monster;
import fr.ubx.poo.model.go.character.Player;

public class Game {
    private final ArrayList<World> world= new ArrayList<>(); // to register all levels
    private final Player player;
    private final String worldPath;
    private int initPlayerLives;
    private int numberlevel; // allow to create all levels at the beginning
    private int actualLevel=1; // indicates the level who is the player
    private boolean changeLevel = false;
    private int maxlevel = 1 ; // to indicates the bigger levels that the player has already visited

    public Game(String worldPath){
        this.worldPath = worldPath;
        loadConfig(worldPath);
        this.world.add(null);
        World first = new World(actualLevel);
        this.world.add(first);
        findMonsters(actualLevel);
        // creation of all levels
        for(int i=2 ; i<=numberlevel ; i++){
            newLevel(i);
        }
        try {
            Position positionPlayer = first.findPlayer();
            player = new Player(this, positionPlayer);
        } catch (PositionNotFoundException e) {
            System.err.println("Position not found : " + e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    // getters and setters //
    public boolean isChangeLevel() { return changeLevel; }
    public void setChangeLevel(boolean changeLevel) { this.changeLevel = changeLevel; }
    public int getInitPlayerLives() { return initPlayerLives; }
    public int getActualLevel() { return actualLevel; }
    public ArrayList<World> getWorld() { return world; }
    public Player getPlayer() { return this.player; }
    public int getMaxlevel() { return maxlevel; }
    // getters and setters //

    // create a new level //
    public void newLevel(int index){
        World world = new World(index) ;
        this.world.add(world);
        findMonsters(index);
    }

    // find and create monsters of a level //
    public void findMonsters(int level){
        for (int x = 0; x < getWorld().get(level).dimension.width; x++) {
            for (int y = 0; y < getWorld().get(level).dimension.height; y++) {
                if (getWorld().get(level).getRaw()[y][x].equals(WorldEntity.Monster)){
                    getWorld().get(level).getMonsters().add(new Monster(this, new Position(x, y), level));
                }
            }
        }
    }

    // create a bomb on the case of the player only if any bomb is already is the case //
    public void createBomb(){
        boolean canBomb = true;
        for(int i=0; i<getWorld().get(actualLevel).getBombs().size() ; i++){
            if (getWorld().get(actualLevel).getBombs().get(i).getPosition().equals(player.getPosition())) {
                canBomb = false;
                break;
            }
        }
        if(canBomb) {
            Bomb bomb = new Bomb(this, player.getPosition(), actualLevel);
            player.setBomb(player.getBomb() - 1);
            getWorld().get(actualLevel).getBombs().add(bomb);
        }
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

    // update the world : change the level if  the player is on an open door //
    public void update (){
        if(getWorld().get(actualLevel).isPrevOpenDoor(player.getPosition())){
            actualLevel--;
            player.setPosition(getWorld().get(actualLevel).startPlayer(1));
            changeLevel =true;
        }
        else if(getWorld().get(actualLevel).isNextOpenDoor(player.getPosition())) {
            actualLevel++;
            if(actualLevel>maxlevel) maxlevel=actualLevel;
            player.setPosition(getWorld().get(actualLevel).startPlayer(0));
            changeLevel =true;
        }
    }

    // update all bombs
    public void updateBombs(long now){
        for(int level=1 ; level<=maxlevel ; level++) {
            int number= getWorld().get(level).getBombs().size();
            int cpt=0;
            while (cpt < number) {
                getWorld().get(level).getBombs().get(cpt).update(now);
                if (getWorld().get(level).getBombs().get(cpt).getNumber() == 5) {
                    getWorld().get(level).getBombs().get(cpt).explosion();
                    getWorld().get(level).getBombs().remove(cpt);
                    player.setBomb(player.getBomb() + 1);
                    number--;
                } else cpt++;
            }
        }
    }

    // update all monsters of all word
    public void updateMonsters(long now){
        for(int level=1 ; level<=maxlevel ; level++) {
            int number = getWorld().get(level).getMonsters().size();
            int cpt = 0;
            while (cpt < number) {
                //if a monster is touch by a bomb, he is destroy
                if (!(getWorld().get(level)).getMonsters().get(cpt).isAlive()) {
                    getWorld().get(level).getMonsters().remove(cpt);
                    number--;
                    // else he is updates
                } else {
                    getWorld().get(level).getMonsters().get(cpt).update(now);
                    cpt++;
                }
            }
        }
    }

}
