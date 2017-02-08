package io.faucette.zombierampage;


import io.faucette.math.Vec2;
import io.faucette.scene_graph.Component;
import io.faucette.scene_graph.Entity;
import io.faucette.scene_graph.Scene;
import io.faucette.transform_components.Transform2D;


public class AnalogControl extends Component {
    private static long leftTouchId = -1;
    private static long rightTouchId = -1;
    private static float MAX_SIZE = 64f;
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

            Transform2D transform = entity.getComponent(Transform2D.class);
            Vec2 position = transform.getLocalPosition();

            if (side == Side.Left) {
                position.x = 128f;
            } else {
                position.x = screenWidth - 128f;
            }
            position.y = screenHeight - 128f;

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
                Vec2 touchPosition = touch.position;
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
                    Vec2 touchPosition = touch.position;

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
        analog.y = -analog.y;
        tmp.smul(0.5f);

        return this;
    }

    public enum Side {
        Left,
        Right
    }
}
