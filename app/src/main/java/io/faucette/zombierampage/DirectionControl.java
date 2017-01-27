package io.faucette.zombierampage;

import io.faucette.math.Mathf;
import io.faucette.math.Vec2;
import io.faucette.scene_graph.Component;
import io.faucette.scene_graph.Entity;


public class DirectionControl extends Component {
    private static float MIN_SPEED = 0.01f;
    private Mathf.Direction dir = Mathf.Direction.UP;
    public Vec2 direction = new Vec2();
    public boolean fromVelocity = true;


    public DirectionControl() {
        super();
    }

    @Override
    public DirectionControl update() {
        Entity entity = getEntity();
        SpriteAnimation spriteAnimation = entity.getComponent(SpriteAnimation.class);

        if (fromVelocity) {
            RigidBody rigidBody = entity.getComponent(RigidBody.class);
            Vec2 velocity = rigidBody.velocity;
            float velLength = velocity.length();

            if (velLength > MIN_SPEED) {
                Vec2.normalize(direction, velocity);
                dir = Mathf.direction(direction.x, direction.y);
            }

            if (velLength > 0f) {
                spriteAnimation.setSpeed(0.1f / velLength);
            }
        }

        if (!fromVelocity) {
            spriteAnimation.setSpeed(2f);
        }

        switch (dir) {
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
