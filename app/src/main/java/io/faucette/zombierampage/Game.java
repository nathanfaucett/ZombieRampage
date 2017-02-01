package io.faucette.zombierampage;


import java.util.Comparator;

import io.faucette.scene_graph.Entity;
import io.faucette.scene_graph.Scene;
import io.faucette.sprite_component.Sprite;
import io.faucette.sprite_component.SpriteManager;
import io.faucette.transform_components.Transform2D;


public class Game {
    public Scene scene = null;


    public Game() {
        setScene(createGameScene());
    }

    private Scene createGameScene() {
        Scene scene = new Scene();

        scene.addPlugin(new InputPlugin());

        scene.addEntity(Entities.createCamera());
        scene.addEntity(Entities.createPlayer());
        scene.addEntity(Entities.createAnalog(AnalogControl.Side.Left));
        scene.addEntity(Entities.createAnalog(AnalogControl.Side.Right));

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

        scene.addEntity(new Entity().addComponent(new EnemySpawn()));

        return scene;
    }

    public void setScene(Scene s) {
        if (scene != null) {
            scene.clear();
        }

        scene = s;
    }

    public void init() {
        scene.init();
    }

    public void update() {
        scene.update();
        scene.getComponentManager(SpriteManager.class).setDirtyLayer(Entities.LAYER);
    }
}
