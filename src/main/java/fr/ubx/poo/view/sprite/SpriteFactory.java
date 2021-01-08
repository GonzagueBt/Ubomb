/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.view.sprite;

import static fr.ubx.poo.view.image.ImageResource.*;

import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.decor.*;
import fr.ubx.poo.model.go.Bomb;
import fr.ubx.poo.model.go.character.Monster;
import fr.ubx.poo.model.go.character.Player;
import fr.ubx.poo.view.image.ImageFactory;
import javafx.scene.layout.Pane;


public final class SpriteFactory {

    public static Sprite createDecor(Pane layer, Position position, Decor decor) {
        ImageFactory factory = ImageFactory.getInstance();
        if (decor.isStone())
            return new SpriteDecor(layer, factory.get(STONE), position);
        if (decor.isTree())
            return new SpriteDecor(layer, factory.get(TREE), position);
        if (decor.isBox())
            return new SpriteDecor(layer, factory.get(BOX), position);
        if (decor.isKey())
            return new SpriteDecor(layer, factory.get(KEY), position);
        if (decor.isPrincess())
            return new SpriteDecor(layer, factory.get(PRINCESS), position);
        if (decor.isHeart())
            return new SpriteDecor(layer, factory.get(HEART), position);
        if (decor.isBNDec())
            return new SpriteDecor(layer, factory.get(BOMBNUMBERDEC), position);
        if (decor.isBNInc())
            return new SpriteDecor(layer, factory.get(BOMBNUMBERINC), position);
        if (decor.isBRDec())
            return new SpriteDecor(layer, factory.get(BOMBRANGEDEC), position);
        if (decor.isBRInc())
            return new SpriteDecor(layer, factory.get(BOMBRANGEINC), position);
        if (decor.isCloseDoor())
            return new SpriteDecor(layer, factory.get(DOOR_CLOSED), position);
        if (decor.isOpenPrevDoor())
            return new SpriteDecor(layer, factory.get(DOOR_OPEN), position);
        if (decor.isOpenNextDoor())
            return new SpriteDecor(layer, factory.get(DOOR_OPEN), position);
        throw new RuntimeException("Unsupported sprite for decor " + decor);
    }

    public static Sprite createPlayer(Pane layer, Player player) {
        return new SpritePlayer(layer, player);
    }
    public static Sprite createMonster(Pane layer, Monster monster) { return new SpriteMonster(layer, monster);}
    public static Sprite createBomb(Pane layer, Bomb bomb) {return new SpriteBomb(layer, bomb);}
}
