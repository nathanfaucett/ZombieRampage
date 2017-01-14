package io.faucette.zombierampage;


import io.faucette.camera_component.Camera;
import io.faucette.math.Vec2;
import io.faucette.scene_graph.Entity;
import io.faucette.scene_graph.Scene;
import io.faucette.sprite_component.Sprite;
import io.faucette.transform_components.Transform2D;

/**
 * Created by nathan on 1/12/17.
 */

public class Game {
    private boolean playing;

    public Scene scene = null;


    public Game() {
        Scene scene = new Scene();

        scene.addPlugin(new InputPlugin());

        scene.addEntity(Entities.createCamera());
        scene.addEntity(Entities.createPlayer());
        scene.addEntity(Entities.createAnalog(AnalogControl.Side.Left));
        scene.addEntity(Entities.createAnalog(AnalogControl.Side.Right));

        setScene(scene);
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
    }
}
