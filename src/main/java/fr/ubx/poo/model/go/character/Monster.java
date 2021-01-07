package fr.ubx.poo.model.go.character;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.Movable;
import fr.ubx.poo.model.decor.Decor;
import fr.ubx.poo.model.go.GameObject;

public class Monster extends GameObject implements Movable {
    Direction direction;
    private long time;
    private boolean alive=true;
    private int level;

    public Monster(Game game, Position position, int level) {
        super(game, position);
        this.direction = Direction.S;
        this.time= System.currentTimeMillis();
        this.level = level;
    }

    @Override
    public boolean canMove(Direction direction) {
        if(!game.getWorld().get(level).isInside(direction.nextPosition(getPosition())) || game.getWorld().get(level).isDecor(direction.nextPosition(getPosition()))){
            return false;
        }
        Position nextPos = direction.nextPosition(getPosition());
        Decor decor = game.getWorld().get(level).get(nextPos);
        return game.getWorld().get(level).isEmpty(nextPos) || (!decor.isBox(decor) && !decor.isOpenPrevDoor(decor) && !decor.isOpenNextDoor(decor));
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public void doMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        setPosition(nextPos);
    }

    public void update(long now) {
        if(System.currentTimeMillis()-time>2000/level) {
            direction = Direction.random();
            if (canMove(direction)) {
                doMove(direction);
                time = System.currentTimeMillis();
            }else if(!isBlocked()) this.update(now);
        }
    }

    public boolean isBlocked(){
        return !canMove(Direction.S) && !canMove(Direction.N) && !canMove(Direction.E) && !canMove(Direction.W);
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}
