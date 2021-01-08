package fr.ubx.poo.model.go.character;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.Movable;
import fr.ubx.poo.model.decor.Decor;
import fr.ubx.poo.model.go.GameObject;

import java.util.ArrayList;

public class Monster extends GameObject implements Movable {
    Direction direction;
    private long time;
    private boolean alive=true;
    private final int level;

    public Monster(Game game, Position position, int level) {
        super(game, position);
        this.direction = Direction.S;
        this.time= System.currentTimeMillis();
        this.level = level;
    }
    // getters and setters //
    public boolean isAlive() {
        return alive;
    }
    public void setAlive(boolean alive) {
        this.alive = alive;
    }
    public Direction getDirection() {
        return direction;
    }
    // getters and setters //

    public boolean canMove(Direction direction) {
        // check if the next position is inside the map and if there is no decor that the player can't run on it
        if(!game.getWorld().get(level).isInside(direction.nextPosition(getPosition())) ||
                game.getWorld().get(level).isDecor(direction.nextPosition(getPosition()))){
            return false;
        }
        Position nextPos = direction.nextPosition(getPosition());
        Decor decor = game.getWorld().get(level).get(nextPos);
        // check if there is not an open door in the next position (in which case, the move is possible)
        return game.getWorld().get(level).isEmpty(nextPos) || (!decor.isBox() && !decor.isOpenPrevDoor() &&
                !decor.isOpenNextDoor());
    }

    public void doMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        setPosition(nextPos);
    }

    /**
     * update the monster : the monster makes a move, all 2 seconds divided by the actual level (so 2 second for the
     * first level, 1 for the second one, etc...)
     * if it's the last level, we call the method intellingentMonster, otherwise, we choose a random move
     */
    public void update(long now, int index) {
        if(System.currentTimeMillis()-time>2000/level) { // check if the time between the last move is long enough
            ArrayList<Direction> directions = new ArrayList<>();
            if(index!= -2 && game.getNumberlevel() == game.getActualLevel()) { // index=2 indicates that we have already
                // try a random move (and so it's useless to try intelligentMonster)
                if (index==-1) { directions = intelligentMonster(); index++; }
                if(index < directions.size()) direction= directions.get(index);
                else { direction = Direction.random(); index=-2; } //if any move that bring the monster closer to the
                // player is possible, we make obligatory a random move
            }
            else { direction = Direction.random(); index=-2; }// we choose a direction randomly
            if (canMove(direction)) { // if the monster can move in this direction, he moves
                doMove(direction);
                time = System.currentTimeMillis();
            }else if(!isBlocked()) this.update(now, index++); // else, if he's not blocked, we call the method again
        }
    }

    /**
     * intelligentMonster is call in method update just for the last level, the method allow to return a direction who
     * that will bring the monster closer to the player
     * @return an arraylist with the possible directions who bring the monster closer to the player
     */
    public ArrayList<Direction> intelligentMonster(){
        ArrayList<Direction> directions = new ArrayList<>();
        int xPlayer = game.getPlayer().getPosition().x;
        int yPlayer = game.getPlayer().getPosition().y;
        int x = getPosition().x;
        int y = getPosition().y;
        if(yPlayer - y < 0) directions.add(Direction.N);
        if(xPlayer - x < 0) directions.add(Direction.W);
        if(xPlayer - x > 0) directions.add(Direction.E);
        if(yPlayer - y > 0) directions.add(Direction.S);
        return directions;
    }

    /**
     * isBlocked check is a monster can do a move or if the 4 directions are blocked by a decoror the limits
     * of the world
     * @return a boolean, true if there is at leat one direction where the monster can go
     */
    public boolean isBlocked(){
        return !canMove(Direction.S) && !canMove(Direction.N) && !canMove(Direction.E) && !canMove(Direction.W);
    }

}
