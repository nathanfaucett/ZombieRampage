package io.faucette.zombierampage;

import io.faucette.math.Mathf;
import io.faucette.math.Vec2;
import io.faucette.scene_graph.Component;
import io.faucette.scene_graph.Entity;
import io.faucette.scene_graph.Scene;
import io.faucette.transform_components.Transform2D;


public class PlayerControl extends Component {
    private static float MIN_SPEED = 0.01f;
    private Vec2 vel = new Vec2();
    private Vec2 dir = new Vec2();
    private Mathf.Direction direction = Mathf.Direction.UP;
    private float speed = 0.25f;


    public PlayerControl() {
        super();
    }

    @Override
    public PlayerControl update() {
        Entity entity = getEntity();
        Scene scene = entity.getScene();
        AnalogControl leftAnalog = scene.getEntity("left_analog").getComponent(AnalogControl.class);
        AnalogControl rightAnalog = scene.getEntity("right_analog").getComponent(AnalogControl.class);

        Vec2.smul(vel, leftAnalog.analog, speed);

        RigidBody rigidBody = entity.getComponent(RigidBody.class);
        rigidBody.velocity.add(vel);
        float velLength = rigidBody.velocity.length();

        if (rightAnalog.analog.length() > MIN_SPEED) {
            Vec2.normalize(dir, rightAnalog.analog);
            direction = Mathf.direction(dir.x, dir.y);
        } else if (velLength > MIN_SPEED) {
            Vec2.normalize(dir, vel);
            direction = Mathf.direction(dir.x, dir.y);
        }

        SpriteAnimation spriteAnimation = entity.getComponent(SpriteAnimation.class);

        if (velLength > 0f) {
            spriteAnimation.setSpeed(0.1f / velLength);
        } else {
            spriteAnimation.setSpeed(2f);
        }

        switch (direction) {
            case RIGHT:
                spriteAnimation.play("right");
                break;
            case UP_RIGHT:
                spriteAnimation.play("up_right");
                break;
            case UP:
                spriteAnimation.play("up");
                break;
            case UP_LEFT:
                spriteAnimation.play("up_left");
                break;
            case LEFT:
                spriteAnimation.play("left");
                break;
            case DOWN_LEFT:
                spriteAnimation.play("down_left");
                break;
            case DOWN:
                spriteAnimation.play("down");
                break;
            case DOWN_RIGHT:
                spriteAnimation.play("down_right");
                break;
        }

        return this;
    }
}
