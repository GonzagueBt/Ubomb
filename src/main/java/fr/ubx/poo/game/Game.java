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
    private boolean changeLevel = false; //if the player has change of level during his last move
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

    // getters and setters //
    public boolean isChangeLevel() { return changeLevel; }
    public void setChangeLevel(boolean changeLevel) { this.changeLevel = changeLevel; }
    public int getInitPlayerLives() { return initPlayerLives; }
    public int getActualLevel() { return actualLevel; }
    public ArrayList<World> getWorld() { return world; }
    public Player getPlayer() { return this.player; }
    public int getMaxlevel() { return maxlevel; }
    public int getNumberlevel() { return numberlevel; }
    // getters and setters //

    /**
     * newLevel create a new world
     * @param level is the number of the new level
     */
    public void newLevel(int level){
        World world = new World(level) ;
        this.world.add(world);
        findMonsters(level);
    }

    /**
     * findMonsters find and create monsters in the actual level ; the monsters is add to the Arraylist monsters in
     * the class world of the actual level.
     * @param level is the number of a level and the index of the level of a class World in the Arraylist world in this
     *              class ;
     * @see World
     */
    public void findMonsters(int level){
        for (int x = 0; x < getWorld().get(level).dimension.width; x++) {
            for (int y = 0; y < getWorld().get(level).dimension.height; y++) {
                if (getWorld().get(level).getRaw()[y][x].equals(WorldEntity.Monster)){
                    getWorld().get(level).getMonsters().add(new Monster(this, new Position(x, y), level));
                }
            }
        }
    }

    /**
     * createBomb create a new bomb, it's calls when [space] is in input
     * It's check if a bomb can be put on the case of the player, and put it
     * It's add a bomb to the Arraylist Bombs in the class World of the actual level
     */
    public void createBomb(){
        boolean canBomb = true;
        for(int i=0; i<getWorld().get(actualLevel).getBombs().size() ; i++){
            if (getWorld().get(actualLevel).getBombs().get(i).getPosition().equals(player.getPosition())) {
                canBomb = false;
                break;
            }
        }
        if(canBomb) { // if the player can put a bomb on his case
            Bomb bomb = new Bomb(this, player.getPosition(), actualLevel);
            player.setBomb(player.getBomb() - 1);
            getWorld().get(actualLevel).getBombs().add(bomb);
        }
    }

    /**
     * update the world (so the level). Change the world if the player is on a OpenDoor with the corresponding world
     * @changeLevel variable allow to indicates that it's needs to close stage and initialize again the game
     * (with all the sprites) in the class GameEngine
     * @see fr.ubx.poo.engine.GameEngine
     */
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

    /**
     * updateBombs updates all bombs of all levels (so of each world in the arraylist world)
     * It's begin with the update of the bomb in the class Bomb, and if it's time for the bomb to explode,
     * the method make all the process (call the function explosion of the class Bomb, remove the bomb and add a
     * bomb in the bag of the player only if the player doesn't have losse a bomb in his bag during the process bomb)
     * @see Bomb
     */
    public void updateBombs(long now){
        for(int level=1 ; level<=maxlevel ; level++) {
            int number= getWorld().get(level).getBombs().size();
            int cpt=0;
            while (cpt < number) {
                getWorld().get(level).getBombs().get(cpt).update(now);
                if (getWorld().get(level).getBombs().get(cpt).getNumber() == 5) {
                    getWorld().get(level).getBombs().get(cpt).explosion();
                    getWorld().get(level).getBombs().remove(cpt);
                    if(player.isLooseBomb()) player.setLooseBomb(false);
                    else player.setBomb(player.getBomb() + 1);
                    number--;
                } else cpt++;
            }
        }
    }

    /**
     * updateMonsters updates all monsters of all levels (so of each world in the arraylist world)
     * If the monster he's not destroy by a bomb, we call the methods update of the class Monster who allow
     * to update his move.
     * @see Monster
     */
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
                    getWorld().get(level).getMonsters().get(cpt).update(now, -1);
                    cpt++;
                }
            }
        }
    }

}
