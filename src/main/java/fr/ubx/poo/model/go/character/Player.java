/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.model.go.character;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.Movable;
import fr.ubx.poo.model.decor.Box;
import fr.ubx.poo.model.decor.Decor;
import fr.ubx.poo.model.decor.DoorOpenNext;
import fr.ubx.poo.model.go.GameObject;
import fr.ubx.poo.game.Game;

public class Player extends GameObject implements Movable {
    private boolean alive = true;
    Direction direction;
    private boolean moveRequested = false;
    private int lives = 1;
    private boolean winner;
    private long invulnerable = 0;

    /// ajout de ces variables : possiblement besoin de changer ces variables de classes
    private int Bomb=1;
    private int BombRange= 1;
    private int key=0;

    public Player(Game game, Position position) {
        super(game, position);
        this.direction = Direction.S;
        this.lives = game.getInitPlayerLives();
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        if(lives<this.lives){ // for invulnerability during one second
            if(invulnerable!=0) return;
            invulnerable=System.currentTimeMillis();
        }
        this.lives = lives;
    }

    public int getBomb() {
        return Bomb;
    }

    public void setBomb(int bomb) {
        Bomb = bomb;
    }

    public int getBombRange() {
        return BombRange;
    }

    public void setBombRange(int bombRange) {
        BombRange = bombRange;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public Direction getDirection() {
        return direction;
    }

    public void requestMove(Direction direction) {
        if (direction != this.direction) {
            this.direction = direction;
        }
        moveRequested = true;
    }

    @Override
    public boolean canMove(Direction direction) {
        if(!game.getWorld().isInside(direction.nextPosition(getPosition())) || game.getWorld().isDecor(direction.nextPosition(getPosition()))){
            return false;
        }
        Position nextPos = direction.nextPosition(getPosition());
        if(!game.getWorld().isEmpty(nextPos) && game.getWorld().get(nextPos) instanceof Box) {
            if (!game.getWorld().isInside(direction.nextPosition(getPosition(), 2)) || !game.getWorld().isEmpty(direction.nextPosition(getPosition(), 2))) {
                return false;
            }
        }
        return true;
    }

    public void doMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        if(!game.getWorld().isEmpty(nextPos)) processMove(nextPos);
        // déplacement des Box
        // a mettre dans Box() avec méthode abstraite de CanMove de interface Movable
        if(!game.getWorld().isEmpty(nextPos) && game.getWorld().get(nextPos) instanceof Box){
            Position nextPos2 = direction.nextPosition(getPosition(),2);
            game.getWorld().set(nextPos2, game.getWorld().get(nextPos));
            game.getWorld().clear(nextPos);
        }
        setPosition(nextPos);
    }

    private void processMove(Position position){
        Decor decor = game.getWorld().get(position);
        // WINNER
        if(decor.isPrincess(decor)){
            this.winner=true;
        }
        //Win a Heart
        if(decor.isHeart(decor)){
            game.getWorld().clear(position);
            this.lives++;
        }
        if(decor.isBNDec(decor)){
            game.getWorld().clear(position);
            if(Bomb>1) this.Bomb--;
        }
        if(decor.isBNInc(decor)){
            game.getWorld().clear(position);
            this.Bomb++;
        }
        if(decor.isBRDec(decor)){
            game.getWorld().clear(position);
            if(BombRange>1) this.BombRange--;
        }
        if(decor.isBRInc(decor)){
            game.getWorld().clear(position);
            this.BombRange++;
        }
        //Win a key
        if(decor.isKey(decor)){
            game.getWorld().clear(position);
            this.key++;
        }
        game.getWorld().setChanged(true);
    }

    public void processKey(){
        Position position = direction.nextPosition(getPosition());
        Decor decor = game.getWorld().get(position);
        if(!game.getWorld().isEmpty(position) && decor.isCloseDoor(decor)){
            if(key>0){
                game.getWorld().set(position, new DoorOpenNext());
                key--;
                game.setChangeWorld(true);
            }
        }
    }

    public void processBomb(){

    }

    public void update(long now) {
        if(System.currentTimeMillis()-invulnerable>1000) invulnerable=0;
        if (moveRequested) {
            if (canMove(direction)) {
                doMove(direction);
                for(int i=0 ; i<game.getMonsters().size() ; i++){
                    game.getMonsters().get(i).touchPlayer();
                }
            }
        }
        moveRequested = false;
    }

    public boolean isWinner() {
        return winner;
    }

    public boolean isAlive() {
        if(getLives()==0){
            alive=false;
        }
        return alive;
    }

    public boolean isNextOpenDoor(){
        Position nextPos = direction.nextPosition(getPosition());
        Decor decor = game.getWorld().get(nextPos);
        if(!game.getWorld().isEmpty(nextPos) && decor.isOpenNextDoor(decor)){
            return true;
        }
        return false;
    }

    public boolean isPrevOpenDoor(){
        Position nextPos = direction.nextPosition(getPosition());
        Decor decor = game.getWorld().get(nextPos);
        if(!game.getWorld().isEmpty(nextPos) && decor.isOpenDoor(decor)){
            return true;
        }
        return false;
    }


}
