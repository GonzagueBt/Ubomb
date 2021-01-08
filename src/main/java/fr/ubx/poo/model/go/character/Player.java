/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.model.go.character;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.Movable;
import fr.ubx.poo.model.decor.Box;
import fr.ubx.poo.model.decor.Decor;
import fr.ubx.poo.model.decor.DoorNextOpened;
import fr.ubx.poo.model.go.GameObject;
import fr.ubx.poo.game.Game;

public class Player extends GameObject implements Movable {
    private boolean alive = true;
    Direction direction;
    private boolean moveRequested = false;
    private int lives;
    private boolean winner;
    private long invulnerable = 0;

    // object owned by player
    private int Bomb=1;
    private int BombRange= 1;
    private int key=2;

    public Player(Game game, Position position) {
        super(game, position);
        this.direction = Direction.S;
        this.lives = game.getInitPlayerLives();
    }

    // getters and setters //
    public int getLives() { return lives; }
    public void setLives(int lives) {
        if(lives<this.lives){ // for invulnerability during one second
            if(invulnerable!=0) return;
            else invulnerable=System.currentTimeMillis(); }
        this.lives = lives;
    }
    public int getBomb() { return Bomb; }
    public void setBomb(int bomb) { Bomb = bomb; }
    public int getBombRange() { return BombRange; }
    public int getKey() { return key; }
    public void setKey(int key) { this.key = key; }
    public Direction getDirection() { return direction; }
    public boolean isWinner() { return winner; }
    // getters and setters //


    public void requestMove(Direction direction) {
        if (direction != this.direction) {
            this.direction = direction;
        }
        moveRequested = true;
    }

    public boolean canMove(Direction direction) {
        // isInside and isDecor
        if(!game.getWorld().get(game.getActualLevel()).isInside(direction.nextPosition(getPosition())) ||
                game.getWorld().get(game.getActualLevel()).isDecor(direction.nextPosition(getPosition()))){
            return false;
        }
        // deal with box
        Position nextPos = direction.nextPosition(getPosition());
        if(!game.getWorld().get(game.getActualLevel()).isEmpty(nextPos) && game.getWorld().get(game.getActualLevel()).
                get(nextPos).isBox()) {
            for(int i=0 ; i<game.getWorld().get(game.getActualLevel()).getMonsters().size() ; i++){
                if(game.getWorld().get(game.getActualLevel()).getMonsters().get(i).getPosition().equals
                        (direction.nextPosition(getPosition(), 2))){
                    return false;
                }
            }
            return game.getWorld().get(game.getActualLevel()).isInside(direction.nextPosition(getPosition(), 2)) &&
                    game.getWorld().get(game.getActualLevel()).isEmpty(direction.nextPosition(getPosition(), 2) );
        }
        return true;
    }

    public void doMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        if(!game.getWorld().get(game.getActualLevel()).isEmpty(nextPos)) processMove(nextPos);
        setPosition(nextPos);
    }

    /**
     * Treat all things that can happen when the player makes a move :
     * winner ; lives ; win or loose a bomb ; increase or decrease the range of bombs ; win a key ; move a box
     * lose a life because of a monster
     * @see Decor many methods used to compare 2 decors
     * @param position the position where the player makes his moves
     */
    private void processMove(Position position){
        Decor decor = game.getWorld().get(game.getActualLevel()).get(position);
        // WINNER
        if(decor.isPrincess()){ this.winner=true; }

        //Win a Heart
        if(decor.isHeart()){ game.getWorld().get(game.getActualLevel()).clear(position); this.lives++; }

        // decrease number of bomb
        if(decor.isBNDec()){ game.getWorld().get(game.getActualLevel()).clear(position); if(Bomb>1) this.Bomb--; }

        //increase number of bomb
        if(decor.isBNInc()){ game.getWorld().get(game.getActualLevel()).clear(position); this.Bomb++; }

        // decrease range of bomb
        if(decor.isBRDec()){ game.getWorld().get(game.getActualLevel()).clear(position); if(BombRange>1) this.BombRange--; }

        //increase range of bomb
        if(decor.isBRInc()){ game.getWorld().get(game.getActualLevel()).clear(position); this.BombRange++; }

        //Win a key
        if(decor.isKey()){ game.getWorld().get(game.getActualLevel()).clear(position); this.key++; }

        //move a Box
        if(!game.getWorld().get(game.getActualLevel()).isEmpty(position) && game.getWorld().get(game.getActualLevel()).
                get(position).isBox()){
            Position nextPos2 = direction.nextPosition(getPosition(),2);
            game.getWorld().get(game.getActualLevel()).set(nextPos2, game.getWorld().get(game.getActualLevel()).get(position));
            game.getWorld().get(game.getActualLevel()).clear(position);
        }
        // lose a life if player is on the case of a monster
        // FIXME: 07/01/2021 ne fonctionne plus depuis que le changment de monde fonctionne
        for (int i = 0; i < game.getWorld().get(game.getActualLevel()).getMonsters().size(); i++) {
            if(getPosition().equals(game.getWorld().get(game.getActualLevel()).getMonsters().get(i).getPosition())){
                lives--;
            }
        }
        // to indicate that it's needs to refresh sprites decor
        game.getWorld().get(game.getActualLevel()).setChanged(true);
    }

    /**
     * processKey treat the input Enter, look if the player can open a close door with a key
     */
    public void processKey(){
        Position position = direction.nextPosition(getPosition());
        Decor decor = game.getWorld().get(game.getActualLevel()).get(position);
        // only if player has a key and look a close door
        if(!game.getWorld().get(game.getActualLevel()).isEmpty(position) && decor.isCloseDoor()){
            if(key>0){
                game.getWorld().get(game.getActualLevel()).set(position, new DoorNextOpened());
                key--;
                game.getWorld().get(game.getActualLevel()).setChanged(true);
            }
        }
    }

    // update position of the player after his move
    public void update(long now) {
        if(System.currentTimeMillis()-invulnerable>1000) invulnerable=0;
        if (moveRequested) {
            if (canMove(direction)) {
                doMove(direction);
            }
        }
        moveRequested = false;
    }


    public boolean isAlive() {
        if(getLives()==0){
            alive=false;
        }
        return alive;
    }

}
