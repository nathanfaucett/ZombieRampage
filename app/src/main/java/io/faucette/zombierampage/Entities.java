package io.faucette.zombierampage;

import java.util.HashMap;

import io.faucette.camera_component.Camera;
import io.faucette.scene_graph.Entity;
import io.faucette.sprite_component.Sprite;
import io.faucette.transform_components.Transform2D;

/**
 * Created by nathan on 1/14/17.
 */

public class Entities {
    public static HashMap<String, float[][]> animations = new HashMap<>();

    public static int BG_LAYER = 0;
    public static int LAYER = 1;
    public static int MENU_LAYER = 2;


    public static Entity createAnalog(AnalogControl.Side side) {
        return new Entity(side == AnalogControl.Side.Left ? "left_analog" : "right_analog")
                .addComponent(new Transform2D())
                .addComponent(new AnalogControl(side))
                .addComponent(new Sprite()
                        .setLayer(MENU_LAYER)
                        .setWidth(0.25f)
                        .setHeight(0.25f)
                        .setImage(R.drawable.analog))
                .addChild(new Entity()
                        .addComponent(new Transform2D())
                        .addComponent(new Sprite()
                                .setLayer(MENU_LAYER)
                                .setWidth(0.125f)
                                .setHeight(0.125f)
                                .setImage(R.drawable.analog)));
    }

    public static Entity createPlayer() {
        return new Entity()
                .addComponent(new Transform2D())
                .addComponent(new PlayerControl())
                .addComponent(new SpriteAnimation(animations, "down"))
                .addComponent(new Sprite()
                        .setLayer(LAYER)
                        .setWidth(0.25f)
                        .setHeight(0.25f)
                        .setImage(R.drawable.player));
    }

    public static Entity createCamera() {
        return new Entity("")
                .addComponent(new Camera())
                .addComponent(new Transform2D());
    }

    public static HashMap<String, float[][]> initAnimations() {

        animations.put("down", new float[][] {
                {0f, 0f, 0.05f, 1f, 1f},
                {0.05f, 0f, 0.05f, 1f, 1f},
        });
        animations.put("down_right", new float[][] {
                {0.10f, 0f, 0.05f, 1f, 1f},
                {0.15f, 0f, 0.05f, 1f, 1f},
        });
        animations.put("right", new float[][] {
                {0.20f, 0f, 0.05f, 1f, 1f},
                {0.25f, 0f, 0.05f, 1f, 1f},
        });
        animations.put("up_right", new float[][] {
                {0.30f, 0f, 0.05f, 1f, 1f},
                {0.35f, 0f, 0.05f, 1f, 1f},
        });
        animations.put("up", new float[][] {
                {0.40f, 0f, 0.05f, 1f, 1f},
                {0.45f, 0f, 0.05f, 1f, 1f},
        });
        animations.put("up_left", new float[][] {
                {0.50f, 0f, 0.05f, 1f, 1f},
                {0.55f, 0f, 0.05f, 1f, 1f},
        });
        animations.put("left", new float[][] {
                {0.60f, 0f, 0.05f, 1f, 1f},
                {0.65f, 0f, 0.05f, 1f, 1f},
        });
        animations.put("down_left", new float[][] {
                {0.70f, 0f, 0.05f, 1f, 1f},
                {0.75f, 0f, 0.05f, 1f, 1f},
        });
        animations.put("hit", new float[][] {
                {0.80f, 0f, 0.05f, 1f, 2f},
        });
        animations.put("die", new float[][] {
                {0.80f, 0f, 0.05f, 1f, 1f},
                {0.85f, 0f, 0.05f, 1f, 1f},
                {0.90f, 0f, 0.05f, 1f, 1f},
                {0.95f, 0f, 0.05f, 1f, 0f},
        });

        return animations;
    }
}
