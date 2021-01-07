package fr.ubx.poo.game;

import fr.ubx.poo.model.decor.*;

import java.util.Hashtable;
import java.util.Map;

public class WorldBuilder {
    private final Map<Position, Decor> grid = new Hashtable<>();

    private WorldBuilder() {
    }

    public static Map<Position, Decor> build(WorldEntity[][] raw, Dimension dimension) {
        WorldBuilder builder = new WorldBuilder();
        for (int x = 0; x < dimension.width; x++) {
            for (int y = 0; y < dimension.height; y++) {
                Position pos = new Position(x, y);
                Decor decor = processEntity(raw[y][x]);
                if (decor != null)
                    builder.grid.put(pos, decor);
            }
        }
        return builder.grid;
    }

    private static Decor processEntity(WorldEntity entity) {
        return switch (entity) {
            case Stone -> new Stone();
            case Tree -> new Tree();
            case Box -> new Box();
            case Key -> new Key();
            case Princess -> new Princess();
            case Heart -> new Heart();
            case BombNumberDec -> new BombNumberDec();
            case BombNumberInc -> new BombNumberInc();
            case BombRangeDec -> new BombRangeDec();
            case BombRangeInc -> new BombRangeInc();
            case DoorCloseNext -> new DoorCloseNext();
            case DoorNextOpened -> new DoorNextOpened();
            case DoorPrevOpened -> new DoorPrevOpened();
            default -> null;
        };
    }

}
