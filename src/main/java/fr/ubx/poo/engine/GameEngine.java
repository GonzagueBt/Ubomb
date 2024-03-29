/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.engine;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.view.sprite.Sprite;
import fr.ubx.poo.view.sprite.SpriteFactory;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.model.go.character.Player;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;


public final class GameEngine {

    private static AnimationTimer gameLoop;
    private final String windowTitle;
    private final Game game;
    private final Player player;
    private final List<Sprite> sprites = new ArrayList<>();
    private StatusBar statusBar;
    private Pane layer;
    private Input input;
    private Stage stage;
    private Sprite spritePlayer;
    private final ArrayList<Sprite> spriteMonsters = new ArrayList<>();
    private final ArrayList<Sprite> spriteBombs = new ArrayList<>();
    private int canChangeLevel = 0; //to prevent the player from changing world indefinitely when he is on an open door
    // (since this takes him to another open door), we have canChangeLevel that allows the game to be updated only if the
    // player has made 2 moves since the last world change

    public GameEngine(final String windowTitle, Game game, final Stage stage) {
        this.windowTitle = windowTitle;
        this.game = game;
        this.player = game.getPlayer();
        initialize(stage, game);
        buildAndSetGameLoop();
    }

    private void initialize(Stage stage, Game game) {
        this.stage = stage;
        Group root = new Group();
        layer = new Pane();

        int height = game.getWorld().get(game.getActualLevel()).dimension.height;
        int width = game.getWorld().get(game.getActualLevel()).dimension.width;
        int sceneWidth = width * Sprite.size;
        int sceneHeight = height * Sprite.size;
        Scene scene = new Scene(root, sceneWidth, sceneHeight + StatusBar.height);
        scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());

        stage.setTitle(windowTitle);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        input = new Input(scene);
        root.getChildren().add(layer);
        statusBar = new StatusBar(root, sceneWidth, sceneHeight, game);
        // Create decor sprites
        game.getWorld().get(game.getActualLevel()).forEach( (pos,d) -> sprites.add(SpriteFactory.createDecor(layer, pos, d)));
        spritePlayer = SpriteFactory.createPlayer(layer, player);
        for(int i=0 ; i< game.getWorld().get(game.getActualLevel()).getMonsters().size() ; i++){
            spriteMonsters.add(SpriteFactory.createMonster(layer, game.getWorld().get(game.getActualLevel()).getMonsters().get(i)));
        }
    }

    protected final void buildAndSetGameLoop() {
        gameLoop = new AnimationTimer() {
            public void handle(long now) {
                // Check keyboard actions
                processInput(now);

                // Do actions
                update(now);

                // Graphic update
                render();
                statusBar.update(game);
            }
        };
    }

    private void processInput(long now) {
        if (input.isExit()) {
            gameLoop.stop();
            Platform.exit();
            System.exit(0);
        }
        if(input.isKey()){
            player.processKey();
        }
        if(input.isBomb() && player.getBomb()>0){
            game.createBomb();
            spriteBombs.add(SpriteFactory.createBomb(layer, game.getWorld().get(game.getActualLevel()).getBombs().
                    get(game.getWorld().get(game.getActualLevel()).getBombs().size()-1)));
        }
        if (input.isMoveDown()) {
            player.requestMove(Direction.S);
            canChangeLevel++;
        }
        if (input.isMoveLeft()) {
            player.requestMove(Direction.W);
            canChangeLevel++;
        }
        if (input.isMoveRight()) {
            player.requestMove(Direction.E);
            canChangeLevel++;
        }
        if (input.isMoveUp()) {
            player.requestMove(Direction.N);
            canChangeLevel++;
        }
        input.clear();
    }

    private void showMessage(String msg, Color color) {
        Text waitingForKey = new Text(msg);
        waitingForKey.setTextAlignment(TextAlignment.CENTER);
        waitingForKey.setFont(new Font(60));
        waitingForKey.setFill(color);
        StackPane root = new StackPane();
        root.getChildren().add(waitingForKey);
        Scene scene = new Scene(root, 400, 200, Color.WHITE);
        stage.setTitle(windowTitle);
        stage.setScene(scene);
        input = new Input(scene);
        stage.show();
        new AnimationTimer() {
            public void handle(long now) {
                processInput(now);
            }
        }.start();
    }


    private void update(long now) {
        // update game only if player have made 2 move since last update (to avoid change infinitely the level)
        if(canChangeLevel != 0 && canChangeLevel!=1) {game.update();  }

        //update level only if game has been update
        if(game.isChangeLevel()){
            stage.close();
            initialize(stage,game);
            game.setChangeLevel(false);
            canChangeLevel=0;
        }

        //update player
        player.update(now);

        // update monsters
        game.updateMonsters(now);
        spriteMonsters.forEach(Sprite::remove);
        spriteMonsters.clear();
        game.getWorld().get(game.getActualLevel()).getMonsters().forEach(monster ->
                spriteMonsters.add(SpriteFactory.createMonster(layer, monster)));

        //update Bomb
        game.updateBombs(now);
        spriteBombs.forEach(Sprite::remove);
        spriteBombs.clear();
        game.getWorld().get(game.getActualLevel()).getBombs().forEach(bomb ->
                spriteBombs.add(SpriteFactory.createBomb(layer, bomb)));

        // update decor
        if(game.getWorld().get(game.getActualLevel()).isChanged()){
            sprites.forEach(Sprite::remove);
            sprites.clear();
            game.getWorld().get(game.getActualLevel()).forEach( (pos,d) ->
                    sprites.add(SpriteFactory.createDecor(layer, pos, d)));
            game.getWorld().get(game.getActualLevel()).setChanged(false);
        }

        // update status game (win or lose)
        if (!player.isAlive()) {
            gameLoop.stop();
            showMessage("Perdu!", Color.RED);
        }
        if (player.isWinner()) {
            gameLoop.stop();
            showMessage("Gagné", Color.BLUE);
        }
    }

    private void render() {
        sprites.forEach(Sprite::render); //decors
        spriteBombs.forEach(Sprite::render); //bombs
        spriteMonsters.forEach(Sprite::render); //monsters
        spritePlayer.render(); //player
    }

    public void start() {
        gameLoop.start();
    }
}
