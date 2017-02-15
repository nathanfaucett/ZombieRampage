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

    private long getTouchId() {
        switch (side) {
            case Left:
                return leftTouchId;
            case Right:
                return rightTouchId;
            default:
                return -1;
        }
    }

    private boolean canUseTouch(long id) {
        switch (side) {
            case Left:
                return leftTouchId == -1 && rightTouchId != id;
            case Right:
                return rightTouchId == -1 && leftTouchId != id;
        }
        return true;
    }

    private void setTouchIdUsed(long id) {
        switch (side) {
            case Left: {
                leftTouchId = id;
                break;
            }
            case Right: {
                rightTouchId = id;
                break;
            }
        }
    }

    private void setTouchIdUnused() {
        switch (side) {
            case Left: {
                if (leftTouchId != -1) {
                    leftTouchId = -1;
                }
                break;
            }
            case Right: {
                if (rightTouchId != -1) {
                    rightTouchId = -1;
                }
                break;
            }
        }
    }

    private boolean isOwnSide(InputPlugin.Touch touch) {
        switch (side) {
            case Left:
                return touch.position.x < screenWidth * 0.5;
            case Right:
                return touch.position.x > screenWidth * 0.5;
            default:
                return false;
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
            boolean stopDragging = true;

            for (InputPlugin.Touch touch : input.getTouches()) {
                if (isOwnSide(touch) && touch.getId() == getTouchId()) {
                    stopDragging = false;
                    Vec2.sub(tmp, touch.position, transform.getPosition());

                    float length = tmp.length();
                    if (length > MAX_SIZE) {
                        tmp.normalize();
                        tmp.smul(MAX_SIZE);
                    }
                }
            }

            if (stopDragging) {
                setTouchIdUnused();
                dragging = false;
            }
        } else {
            for (InputPlugin.Touch touch : input.getTouches()) {
                if (isOwnSide(touch) && canUseTouch(touch.getId())) {
                    float distance = Utils.circleToPoint(transform.getPosition(), MAX_SIZE, touch.position);

                    if (distance > 0f) {
                        setTouchIdUsed(touch.getId());
                        dragging = true;
                    }
                }
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
        Right;

        @Override
        public String toString() {
            switch (this) {
                case Left:
                    return "Left";
                case Right:
                    return "Right";
                default:
                    return "Invalid";
            }
        }
    }
}
