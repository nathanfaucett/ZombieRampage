package io.faucette.zombierampage;

import io.faucette.math.Mathf;
import io.faucette.math.Vec2;
import io.faucette.scene_graph.Component;


public class AnimationControl extends Pauseable {
    private static float MIN_SPEED = 0.1f;
    public Vec2 direction = new Vec2();
    public boolean fromVelocity = true;
    private Mathf.Direction dir = Mathf.Direction.UP;


    public AnimationControl() {
        super();
    }

    @Override
    public AnimationControl update() {
        if (this.isPaused()) {
            return this;
        }

        SpriteAnimation spriteAnimation = entity.getComponent(SpriteAnimation.class);
        StatusControl.State state = entity.getComponent(StatusControl.class).getState();

        if (state == StatusControl.State.Alive) {
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
                } else {
                    spriteAnimation.setSpeed(2f);
                }
            }

            if (!fromVelocity) {
                dir = Mathf.direction(direction.x, direction.y);
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
        } else if (state == StatusControl.State.Hit) {
            spriteAnimation.play("hit");
        } else if (state == StatusControl.State.Dying) {
            spriteAnimation.play("die");
        } else if (state == StatusControl.State.Birth) {
            spriteAnimation.play("birth");
        }

        return this;
    }
}
