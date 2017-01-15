package io.faucette.zombierampage;


import java.util.HashMap;

import io.faucette.camera_component.Camera;
import io.faucette.math.Vec2;
import io.faucette.math.Vec4;
import io.faucette.scene_graph.Entity;
import io.faucette.sprite_component.Sprite;
import io.faucette.transform_components.Transform2D;


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
                        .setLocal(true)
                        .setWidth(0.25f)
                        .setHeight(0.25f)
                        .setImage(R.drawable.analog))
                .addChild(new Entity()
                        .addComponent(new Transform2D())
                        .addComponent(new Sprite()
                                .setLayer(MENU_LAYER)
                                .setLocal(true)
                                .setWidth(0.125f)
                                .setHeight(0.125f)
                                .setImage(R.drawable.analog)));
    }

    public static Entity createPlayer() {
        return new Entity("player")
                .addComponent(new Transform2D())
                .addComponent(new PlayerControl())
                .addComponent(new RigidBody(RigidBody.Type.Dynamic)
                    .addShape(new RigidBody.Shape(new Vec2(0f, -0.125f), 0.25f, 0.125f)))
                .addComponent(new SpriteAnimation(animations, "down"))
                .addComponent(new Sprite()
                        .setLayer(LAYER)
                        .setWidth(0.125f)
                        .setHeight(0.125f)
                        .setImage(R.drawable.player));
    }

    public static Entity createEnemy() {
        return new Entity()
                .addComponent(new Transform2D())
                .addComponent(new RigidBody(RigidBody.Type.Dynamic)
                        .addShape(new RigidBody.Shape(new Vec2(0f, -0.125f), 0.25f, 0.125f)))
                .addComponent(new SpriteAnimation(animations, "down"))
                .addComponent(new Sprite()
                        .setLayer(LAYER)
                        .setWidth(0.125f)
                        .setHeight(0.125f)
                        .setImage(R.drawable.player));
    }

    public static Entity createCamera() {
        return new Entity("camera")
                .addComponent(new Camera()
                    .setBackground(new Vec4(0.5f, 0.5f, 0.5f, 1f)))
                .addComponent(new Transform2D())
                .addComponent(new CameraControl());
    }

    public static Entity createTile(LevelGenerator.Type type, float size, int x, int y) {
        return new Entity()
                .addComponent(new RigidBody()
                        .addShape(new RigidBody.Shape(new Vec2(size, 0f), 0.2f, size * 2f))
                        .addShape(new RigidBody.Shape(new Vec2(-size, 0f), 0.2f, size * 2f))
                        .addShape(new RigidBody.Shape(new Vec2(0f, size), size * 2f, 0.2f))
                        .addShape(new RigidBody.Shape(new Vec2(0f, -size), size * 2f, 0.2f)))
                .addComponent(new Transform2D()
                    .setPosition(new Vec2(x * size * 2f, y * size * 2f)))
                .addComponent(new Sprite()
                        .setLayer(BG_LAYER)
                        .setWidth(size)
                        .setHeight(size)
                        .setImage(getImage(type)));
    }

    public static Integer getImage(LevelGenerator.Type type) {
        switch (type) {
            case TopEnd:
                return R.drawable.top_end;
            case RightEnd:
                return R.drawable.right_end;
            case BottomEnd:
                return R.drawable.bottom_end;
            case LeftEnd:
                return R.drawable.left_end;

            case TopRightTurn:
                return R.drawable.top_right_turn;
            case TopLeftTurn:
                return R.drawable.top_left_turn;
            case BottomRightTurn:
                return R.drawable.bottom_right_turn;
            case BottomLeftTurn:
                return R.drawable.bottom_left_turn;

            case VerticalLeftThreeWay:
                return R.drawable.vertical_left_three_way;
            case VerticalRightThreeWay:
                return R.drawable.vertical_right_three_way;
            case HorizontalTopThreeWay:
                return R.drawable.horizontal_top_three_way;
            case HorizontalBottomThreeWay:
                return R.drawable.horizontal_bottom_three_way;

            case Vertical:
                return R.drawable.vertical;
            case Horizontal:
                return R.drawable.horizontal;

            case FourWay:
                return R.drawable.four_way;
        }
        return 0;
    }

    public static HashMap<String, float[][]> initAnimations() {

        animations.put("down", new float[][]{
                {0f, 0f, 0.05f, 1f, 1f},
                {0.05f, 0f, 0.05f, 1f, 1f},
        });
        animations.put("down_right", new float[][]{
                {0.10f, 0f, 0.05f, 1f, 1f},
                {0.15f, 0f, 0.05f, 1f, 1f},
        });
        animations.put("right", new float[][]{
                {0.20f, 0f, 0.05f, 1f, 1f},
                {0.25f, 0f, 0.05f, 1f, 1f},
        });
        animations.put("up_right", new float[][]{
                {0.30f, 0f, 0.05f, 1f, 1f},
                {0.35f, 0f, 0.05f, 1f, 1f},
        });
        animations.put("up", new float[][]{
                {0.40f, 0f, 0.05f, 1f, 1f},
                {0.45f, 0f, 0.05f, 1f, 1f},
        });
        animations.put("up_left", new float[][]{
                {0.50f, 0f, 0.05f, 1f, 1f},
                {0.55f, 0f, 0.05f, 1f, 1f},
        });
        animations.put("left", new float[][]{
                {0.60f, 0f, 0.05f, 1f, 1f},
                {0.65f, 0f, 0.05f, 1f, 1f},
        });
        animations.put("down_left", new float[][]{
                {0.70f, 0f, 0.05f, 1f, 1f},
                {0.75f, 0f, 0.05f, 1f, 1f},
        });
        animations.put("hit", new float[][]{
                {0.80f, 0f, 0.05f, 1f, 2f},
        });
        animations.put("die", new float[][]{
                {0.80f, 0f, 0.05f, 1f, 1f},
                {0.85f, 0f, 0.05f, 1f, 1f},
                {0.90f, 0f, 0.05f, 1f, 1f},
                {0.95f, 0f, 0.05f, 1f, 0f},
        });

        return animations;
    }
}
