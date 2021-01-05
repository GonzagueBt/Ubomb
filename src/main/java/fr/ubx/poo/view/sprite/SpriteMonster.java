package fr.ubx.poo.view.sprite;

import fr.ubx.poo.model.go.GameObject;
import fr.ubx.poo.model.go.character.Monster;
import fr.ubx.poo.view.image.ImageFactory;
import javafx.scene.layout.Pane;

public class SpriteMonster extends SpriteGameObject{
    public SpriteMonster(Pane layer, GameObject go) {
        super(layer, null, go);
        updateImage();
    }

    @Override
    public void updateImage() {
        Monster monster = (Monster) go;
        setImage(ImageFactory.getInstance().getMonster(monster.getDirection()));
    }
}
