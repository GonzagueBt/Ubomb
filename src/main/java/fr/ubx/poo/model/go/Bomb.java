package fr.ubx.poo.model.go;

import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.game.World;
import fr.ubx.poo.model.decor.Decor;

import java.util.ArrayList;

public class Bomb extends GameObject{
    private long time;
    private int number=0;
    private final int range;
    private int level;

    public Bomb(Game game, Position position, int level) {
        super(game, position);
        time= System.currentTimeMillis();
        range=game.getPlayer().getBombRange();
        this.level = level;
    }

    public int getNumber() {
        return number;
    }

    public void update(long now){
        if(System.currentTimeMillis()-time >1000){
            time=System.currentTimeMillis();
            number++;
        }
    }

    public void explosion(){
        // On the case of the bomb //
        for(int j=0 ; j< game.getWorld().get(level).getMonsters().size() ; j++){
            if(getPosition().equals(game.getWorld().get(level).getMonsters().get(j).getPosition())) game.getWorld().get(level).getMonsters().get(j).setAlive(false);
        }
        if(getPosition().equals(game.getPlayer().getPosition())) game.getPlayer().setLives(game.getPlayer().getLives()-1);
        Decor decor = game.getWorld().get(level).get(getPosition());
        if(!game.getWorld().get(level).isEmpty(getPosition()) && game.getWorld().get(level).isInside(getPosition())){
            if(decor.bombCanDestroy()) {
                game.getWorld().get(level).clear(getPosition());
            }
        }
        //   //
        int x = getPosition().x;
        int y = getPosition().y;
        ArrayList<Integer> memory = new ArrayList<>(); // to know if the explosion keep going is each direction
        for(int i=0 ; i<4 ; i++) memory.add(0); // 0 if they keep going, 1 otherwise
        for(int i=1; i<=range ; i++){
            // Est
            memory.set(0, destroyPosition(memory.get(0), new Position(x+i,y)));
            // Ouest
            memory.set(1, destroyPosition(memory.get(1), new Position(x-i,y)));
            // Sud
            memory.set(2, destroyPosition(memory.get(2), new Position(x,y+i)));
            // Nord
            memory.set(3, destroyPosition(memory.get(3), new Position(x,y-i)));
        }
        game.getWorld().get(level).setChanged(true);
    }

    // destroy or not the element on the position pos

    /**
     *
     * @param memory
     * @param pos
     * @return
     */
    public int destroyPosition(int memory, Position pos){
        World world = game.getWorld().get(level);
        Decor decor = world.get(pos);
        if(memory==0){ // if element on position pos has to be destroy
            if(!world.isEmpty(pos) && world.isInside(pos)){
                if(decor.bombCanDestroy()) { // we check if there is a decor on pos, then he can be destroy
                    game.getWorld().get(level).clear(pos);
                }
                if (decor.stopExplosion()) memory=1; // check if it's a decor who top the explosion
            }
            // check if there is a monster on the case who has to be destroy
            for(int j=0 ; j< world.getMonsters().size() ; j++){
                if(pos.equals(world.getMonsters().get(j).getPosition())) game.getWorld().get(level).getMonsters().get(j).setAlive(false);
            }
            // check if there is a bomb on the case who has to be destroy
            for(int j=0 ; j< world.getBombs().size() ; j++){
                if(pos.equals(world.getBombs().get(j).getPosition())) game.getWorld().get(level).getBombs().get(j).number=4;
            }
            // we check if the player is on the case
            if(pos.equals(game.getPlayer().getPosition())) game.getPlayer().setLives(game.getPlayer().getLives()-1);
        }
        return memory;
    }
}
