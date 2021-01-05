package fr.ubx.poo.model.go;

import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.decor.Decor;

import java.util.ArrayList;

public class Bomb extends GameObject{
    private long time;
    private int number=0;
    private final int range;

    public Bomb(Game game, Position position) {
        super(game, position);
        time= System.currentTimeMillis();
        range=game.getPlayer().getBombRange();
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
        // On the case
        for(int j=0 ; j< game.getMonsters().size() ; j++){
            if(getPosition().equals(game.getMonsters().get(j).getPosition())) game.getMonsters().get(j).setAlive(false);
        }
        if(getPosition().equals(game.getPlayer().getPosition())) game.getPlayer().setLives(game.getPlayer().getLives()-1);

        int x = getPosition().x;
        int y = getPosition().y;
        ArrayList<Integer> memory = new ArrayList<>();
        for(int i=0 ; i<4 ; i++) memory.add(0);
        for(int i=1; i<=range ; i++){
            Position pos_x1 = new Position(x+i,y); // Est
            Position pos_x0 = new Position(x-i,y); // Ouest
            Position pos_y1 = new Position(x,y+i); // Sud
            Position pos_y0 = new Position(x,y-i); // Nord

            // Est
            Decor decor = game.getWorld().get(pos_x1);
            if(memory.get(0)==0){
                if(!game.getWorld().isEmpty(pos_x1) && game.getWorld().isInside(pos_x1) && decor.bombCanDestroy()) {
                    game.getWorld().clear(pos_x1);
                    if (decor.stopExplosion()) memory.set(0, 1);
                }
                for(int j=0 ; j< game.getMonsters().size() ; j++){
                    if(pos_x1.equals(game.getMonsters().get(j).getPosition())) game.getMonsters().get(j).setAlive(false);
                }
                if(pos_x1.equals(game.getPlayer().getPosition())) game.getPlayer().setLives(game.getPlayer().getLives()-1);
            }
            // Ouest
            decor = game.getWorld().get(pos_x0);
            if(memory.get(1)==0){
                if(!game.getWorld().isEmpty(pos_x0) && game.getWorld().isInside(pos_x0) && decor.bombCanDestroy()) {
                    game.getWorld().clear(pos_x0);
                    if (decor.stopExplosion()) memory.set(1, 1);
                }
                for(int j=0 ; j< game.getMonsters().size() ; j++){
                    if(pos_x0.equals(game.getMonsters().get(j).getPosition())) game.getMonsters().get(j).setAlive(false);
                }
                if(pos_x0.equals(game.getPlayer().getPosition())) game.getPlayer().setLives(game.getPlayer().getLives()-1);
            }
            // Sud
            decor = game.getWorld().get(pos_y1);
            if(memory.get(2)==0){
                if(!game.getWorld().isEmpty(pos_y1) && game.getWorld().isInside(pos_y1) && decor.bombCanDestroy()) {
                    game.getWorld().clear(pos_y1);
                    if (decor.stopExplosion()) memory.set(2, 1);
                }
                for(int j=0 ; j< game.getMonsters().size() ; j++){
                    if(pos_y1.equals(game.getMonsters().get(j).getPosition())) game.getMonsters().get(j).setAlive(false);
                }
                if(pos_y1.equals(game.getPlayer().getPosition())) game.getPlayer().setLives(game.getPlayer().getLives()-1);
            }
            // Nord
            decor = game.getWorld().get(pos_y0);
            if(memory.get(3)==0){
                if(!game.getWorld().isEmpty(pos_y0) && game.getWorld().isInside(pos_y0) && decor.bombCanDestroy()) {
                    game.getWorld().clear(pos_y0);
                    if (decor.stopExplosion()) memory.set(3, 1);
                }
                for(int j=0 ; j< game.getMonsters().size() ; j++){
                    if(pos_y0.equals(game.getMonsters().get(j).getPosition())) game.getMonsters().get(j).setAlive(false);
                }
                if(pos_y0.equals(game.getPlayer().getPosition())) game.getPlayer().setLives(game.getPlayer().getLives()-1);
            }
        }
        game.getWorld().setChanged(true);
    }
}
