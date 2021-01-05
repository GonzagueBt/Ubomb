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

    public Monster(Game game, Position position) {
        super(game, position);
        this.direction = Direction.S;
        this.time= System.currentTimeMillis();
    }

    @Override
    public boolean canMove(Direction direction) {
        if(!game.getWorld().isInside(direction.nextPosition(getPosition())) || game.getWorld().isDecor(direction.nextPosition(getPosition()))){
            return false;
        }
        Position nextPos = direction.nextPosition(getPosition());
        Decor decor = game.getWorld().get(nextPos);
        return game.getWorld().isEmpty(nextPos) || (!decor.isBox(decor) && !decor.isOpenDoor(decor) && !decor.isOpenNextDoor(decor));
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
        if(System.currentTimeMillis()-time>1000) {
            direction = Direction.random();
            if (canMove(direction)) {
                doMove(direction);
                time = System.currentTimeMillis();
            }else this.update(now);
        }
    }

    public void touchPlayer(){
        if(getPosition().equals(game.getPlayer().getPosition())){
            game.getPlayer().setLives(game.getPlayer().getLives()-1);
        }
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}
