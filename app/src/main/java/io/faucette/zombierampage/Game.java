package io.faucette.zombierampage;


import io.faucette.camera_component.Camera;
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
    private Transform2D transform2D;


    public Game() {
        Scene scene = new Scene();

        scene.addEntity(new Entity()
                .addComponent(new Camera())
                .addComponent(new Transform2D()));

        transform2D = new Transform2D();

        scene.addEntity(new Entity()
                .addComponent(transform2D)
                .addComponent(new Sprite().setImage(R.drawable.arrows)));

        setScene(scene);
    }

    public void setScene(Scene s) {
        if (scene != null) {
            scene.clear();
        }

        scene = s;
        scene.init();
    }

    public void init() {
        scene.init();
    }

    public void update() {

        scene.update();

        transform2D.getLocalPosition().x = (float) Math.cos(scene.getTime().getCurrent()) * 2f;
        transform2D.setNeedsUpdate();
    }
}
