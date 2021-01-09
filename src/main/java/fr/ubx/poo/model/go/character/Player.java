/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.model.go.character;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.Movable;
import fr.ubx.poo.model.decor.Decor;
import fr.ubx.poo.model.decor.Door;
import fr.ubx.poo.model.go.GameObject;
import fr.ubx.poo.game.Game;


public class Player extends GameObject implements Movable {
    private boolean alive = true;
    Direction direction;
    private boolean moveRequested = false;
    private int lives;
    private boolean winner;
    private long invulnerable = 0;
    private boolean looseBomb = false; // to know if player have to doesn't get back a bomb after it's explosion
        // (if the player has recovered a lost bomb malus)

    // object owned by player
    private int Bomb=1;
    private int BombRange= 1;
    private int key=0;

    public Player(Game game, Position position) {
        super(game, position);
        this.direction = Direction.S;
        this.lives = game.getInitPlayerLives();
    }

    // getters and setters //
    public int getLives() { return lives; }
    public int getBomb() { return Bomb; }
    public void setBomb(int bomb) { Bomb = bomb; }
    public int getBombRange() { return BombRange; }
    public int getKey() { return key; }
    public void setKey(int key) { this.key = key; }
    public Direction getDirection() { return direction; }
    public boolean isWinner() { return winner; }
    public boolean isLooseBomb() { return looseBomb; }
    public void setLooseBomb(boolean looseBomb) { this.looseBomb = looseBomb; }
    public long getInvulnerable() { return invulnerable; }
    // getters and setters //

    public void processLife(int lives){
        if(lives<this.lives){ // for invulnerability during one second
            if(invulnerable!=0) return;
            invulnerable=System.currentTimeMillis();
        }
        this.lives = lives;
    }

    public void requestMove(Direction direction) {
        if (direction != this.direction) {
            this.direction = direction;
        }
        moveRequested = true;
    }

    public boolean canMove(Direction direction) {
        // check if the next position is inside the map and if there is no decor that the player can't run on it
        if(!game.getWorld().get(game.getActualLevel()).isInside(direction.nextPosition(getPosition())) ||
                game.getWorld().get(game.getActualLevel()).isDecor(direction.nextPosition(getPosition()))){
            return false;
        }
        // if there is a box on the next position, check if the box can be moved (if nothing is behind)
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

        // check if there is a Monster on the next position : player lose a life if it's the case
        if (game.getWorld().get(game.getActualLevel()).isMonster(nextPos)) {
            if (invulnerable == 0) {invulnerable = System.currentTimeMillis(); lives--;}
        }
        //
        setPosition(nextPos);
    }

    /**
     * Treat all things that can happen when the player makes a move :
     * winner ; lives ; win or loose a bomb ; increase or decrease the range of bombs ; win a key ; move a box
     * @see Decor many methods used to compare 2 decors
     * @param position the position where the player makes his moves
     */
    private void processMove(Position position){
        Decor decor = game.getWorld().get(game.getActualLevel()).get(position);
        // WINNER
        if(decor.isPrincess()){ this.winner=true; }

        //Win a Heart
        if(decor.isHeart()){ game.getWorld().get(game.getActualLevel()).clear(position); this.lives++; }

        // decrease number of bomb only if player has most that one bomb in his bag + posed on the world
        if(decor.isBNDec()){ game.getWorld().get(game.getActualLevel()).clear(position);
        if(Bomb>1) { this.Bomb--; }
        else if(numberBombPosed()>1 || (numberBombPosed()==1 && Bomb==1)) looseBomb=true;
        }

        //increase number of bomb
        if(decor.isBNInc()){ game.getWorld().get(game.getActualLevel()).clear(position); this.Bomb++;}

        // decrease range of bomb
        if(decor.isBRDec()){ game.getWorld().get(game.getActualLevel()).clear(position); if(BombRange>1) this.BombRange--; }

        //increase range of bomb
        if(decor.isBRInc()){ game.getWorld().get(game.getActualLevel()).clear(position); this.BombRange++; }

        //Win a key
        if(decor.isKey()){ game.getWorld().get(game.getActualLevel()).clear(position); this.key++;}

        //move a Box
        if(!game.getWorld().get(game.getActualLevel()).isEmpty(position) && game.getWorld().get(game.getActualLevel()).
                get(position).isBox()){
            Position nextPos2 = direction.nextPosition(getPosition(),2);
            game.getWorld().get(game.getActualLevel()).set(nextPos2, game.getWorld().get(game.getActualLevel()).get(position));
            game.getWorld().get(game.getActualLevel()).clear(position);
        }
        // to indicate that it's needs to refresh sprites decor
        game.getWorld().get(game.getActualLevel()).setChanged(true);
    }

    /**
     * processKey treat the input [Enter], look if the player can open a close door with a key
     * If the player can, the close door is transformed into an open next door and the player lost a key
     */
    public void processKey(){
        Position position = direction.nextPosition(getPosition());
        Decor decor = game.getWorld().get(game.getActualLevel()).get(position);
        // if player looks a close door
        if(!game.getWorld().get(game.getActualLevel()).isEmpty(position) && decor.isCloseDoor()){
            if(key>0){ // if the player has at least one key
                game.getWorld().get(game.getActualLevel()).set(position, new Door(true, true));
                key--;
                game.getWorld().get(game.getActualLevel()).setChanged(true); // indicates that the sprites needs to
                    // be refresh
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
        if(getLives()==0){ alive=false; }
        return alive;
    }

    /**
     * numberBombPosed count all bombs posed by the player at this moment
     * Used in processMove to know if the player must loose a bomb even if his bag is empty at the moment
     * @return the number of bomb posed
     */
    public int numberBombPosed(){
        int cpt=0;
        for(int level=1 ; level<= game.getMaxlevel() ; level++){
            cpt=cpt +game.getWorld().get(level).getBombs().size();
        }
        return cpt;
    }

}
