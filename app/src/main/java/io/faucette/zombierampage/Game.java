package io.faucette.zombierampage;


import android.util.Log;

import java.util.Comparator;

import io.faucette.scene_graph.Entity;
import io.faucette.scene_graph.Scene;
import io.faucette.sprite_component.Sprite;
import io.faucette.sprite_component.SpriteManager;
import io.faucette.transform_components.Transform2D;


public class Game {
    private static int hearts = 4;
    public Scene scene = null;
    private GLRenderer renderer;
    private Scene nextScene = null;


    public Game(GLRenderer renderer) {
        this.renderer = renderer;
    }

    public void init() {
        loadMenu();
    }

    public void loadMenu() {
        Scene scene = new Scene();

        scene.addPlugin(new InputPlugin());
        scene.addEntity(Entities.createMenuCamera());
        scene.addEntity(UIEntities.createMainMenu(renderer.activityControl.isSignedIn()));
        scene.addEntity(new Entity("menu_control").addComponent(new MenuControl(renderer)));

        scene.init();
        nextScene = scene;
    }

    public void loadGame() {
        Scene scene = new Scene();

        addGameEntitiesToScene(scene);
        addGameUIEntitiesToScene(scene);

        scene.init();
        nextScene = scene;
    }

    private void addGameEntitiesToScene(Scene scene) {
        scene.addPlugin(new InputPlugin());

        scene.addEntity(Entities.createGameCamera());
        scene.addEntity(Entities.createPlayer(hearts * 4));

        scene.getComponentManager(SpriteManager.class).setLayerComparators(Entities.LAYER, new Comparator<Sprite>() {
            @Override
            public int compare(Sprite a, Sprite b) {
                float ay = a.getEntity().getComponent(Transform2D.class).getPosition().y;
                float by = b.getEntity().getComponent(Transform2D.class).getPosition().y;
                return ay > by ? -1 : 0;
            }
        });

        LevelGenerator level = new LevelGenerator();
        float size = 2f;
        for (LevelGenerator.Section section : level) {
            scene.addEntity(Entities.createTile(section, size));
        }

        scene.addEntity(new Entity("level_control").addComponent(new LevelControl(renderer)));
    }

    private void addGameUIEntitiesToScene(Scene scene) {
        scene.addEntity(UIEntities.createHealth(hearts));
        scene.addEntity(UIEntities.createGun());
        scene.addEntity(UIEntities.createGunAmmoCount());
        scene.addEntity(UIEntities.createAnalog(AnalogControl.Side.Left));
        scene.addEntity(UIEntities.createAnalog(AnalogControl.Side.Right));
        scene.addEntity(UIEntities.createPoints());
        scene.addEntity(UIEntities.createPauseBtn());
    }

    public void setScene() {
        if (scene != null) {
            scene.clear();
        }

        scene = nextScene;
        nextScene = null;

        renderer.setScene(scene);
    }

    public void update() {

        if (nextScene != null) {
            setScene();
        }

        if (scene != null) {

            scene.update();

            SpriteManager spriteManager = scene.getComponentManager(SpriteManager.class);
            if (spriteManager != null) {
                scene.getComponentManager(SpriteManager.class).setDirtyLayer(Entities.LAYER);
            }
        }
    }
}
