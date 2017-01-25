package io.faucette.zombierampage;


import java.util.ArrayList;
import java.util.List;

import io.faucette.camera_component.Camera;
import io.faucette.camera_component.CameraManager;
import io.faucette.math.Vec2;
import io.faucette.scene_graph.Component;
import io.faucette.scene_graph.Entity;
import io.faucette.scene_graph.Scene;
import io.faucette.transform_components.Transform2D;


public class AnalogControl extends Component {


    public enum Side {
        Left,
        Right
    }


    private static long leftTouchId = -1;
    private static long rightTouchId = -1;

    private static float MAX_SIZE = 0.25f;
    private static float OFFSET = 0.6f;


    public Vec2 analog;
    private Vec2 tmp;
    private Side side;
    private boolean dragging;
    private float screenWidth;
    private float screenHeight;


    public AnalogControl(Side s) {
        super();

        side = s;
        dragging = false;

        tmp = new Vec2();
        analog = new Vec2();
    }

    private static boolean isTouchIdUsed(long id) {
        return leftTouchId == id || rightTouchId == id;
    }

    private long getTouchId() {
        if (side == Side.Left) {
            return leftTouchId;
        } else if (side == Side.Right) {
            return rightTouchId;
        } else {
            return -1;
        }
    }
    private void setTouchIdUsed(long id) {
        if (side == Side.Left) {
            leftTouchId = id;
        } else if (side == Side.Right) {
            rightTouchId = id;
        }
    }

    private AnalogControl updatePosition(InputPlugin input) {
        if (screenWidth != input.getWidth() || screenHeight != input.getHeight()) {
            screenWidth = input.getWidth();
            screenHeight = input.getHeight();

            Entity entity = getEntity();
            Transform2D transform = entity.getComponent(Transform2D.class);

            Scene scene = entity.getScene();
            Camera camera = scene.getComponentManager(CameraManager.class).getActiveCamera();

            Vec2 position = transform.getLocalPosition();

            if (side == Side.Left) {
                position.x = 0f;
            } else {
                position.x = input.getWidth();
            }
            position.y = input.getHeight();

            camera.toWorld(position, position);

            if (side == Side.Left) {
                position.x += OFFSET;
            } else {
                position.x -= OFFSET;
            }
            position.y += OFFSET;

            transform.setNeedsUpdate();
        }

        return this;
    }

    @Override
    public AnalogControl update() {
        Entity entity = getEntity();
        Scene scene = entity.getScene();
        InputPlugin input = scene.getPlugin(InputPlugin.class);

        updatePosition(input);

        Entity child = entity.getChildren().get(0);
        Transform2D childTransform = child.getComponent(Transform2D.class);
        Transform2D transform = entity.getComponent(Transform2D.class);

        if (dragging) {
            InputPlugin.Touch touch = input.getTouch(getTouchId());

            if (touch != null) {
                Camera camera = scene.getComponentManager(CameraManager.class).getActiveCamera();
                Vec2 touchPosition = new Vec2();
                camera.toWorld(touchPosition, touch.position);
                touchPosition.sub(camera.getEntity().getComponent(Transform2D.class).getPosition());

                Vec2.sub(tmp, touchPosition, transform.getPosition());

                float length = tmp.length();
                if (length > MAX_SIZE) {
                    tmp.normalize();
                    tmp.smul(MAX_SIZE);
                }
            } else {
                setTouchIdUsed(-1);
                dragging = false;
            }
        } else {
            boolean noTouch = true;
            for (InputPlugin.Touch touch : input.getTouches()) {
                if (!isTouchIdUsed(touch.getId())) {
                    Camera camera = scene.getComponentManager(CameraManager.class).getActiveCamera();
                    Vec2 touchPosition = new Vec2();
                    camera.toWorld(touchPosition, touch.position);
                    touchPosition.sub(camera.getEntity().getComponent(Transform2D.class).getPosition());

                    float distance = Utils.circleToPoint(transform.getPosition(), MAX_SIZE, touchPosition);
                    if (distance > 0f) {
                        setTouchIdUsed(touch.getId());
                        dragging = true;
                        noTouch = false;
                    }
                }
            }

            if (noTouch) {
                setTouchIdUsed(-1);
                dragging = false;
            }
        }

        childTransform.setPosition(tmp);
        analog.copy(tmp).sdiv(MAX_SIZE);
        tmp.smul(0.5f);

        return this;
    }
}
