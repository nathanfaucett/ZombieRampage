package io.faucette.zombierampage;

import io.faucette.math.Vec2;
import io.faucette.scene_graph.Component;
import io.faucette.scene_graph.Scene;
import io.faucette.transform_components.Transform2D;


public class PlayerControl extends Component {
    private static float MIN_FIRE_INPUT = 0.01f;

    public enum Gun {
        Pistol,
        Shotgun,
        Uzi,
        FlameThrower,
        Bazooka,
    }

    private float fireFrequencyTime = 0f;
    private float fireFrequency = 0.5f;
    private int fireDamage = 10;
    private float fireSpeed = 2f;

    private Vec2 fireDir = new Vec2();
    private Gun fireType = Gun.Pistol;

    private Vec2 velocity = new Vec2();


    public PlayerControl() {
        super();

        setGun(Gun.Pistol);
    }

    @Override
    public PlayerControl update() {
        StatusControl.State state = entity.getComponent(StatusControl.class).getState();

        if (state != StatusControl.State.Dying && state != StatusControl.State.Dead) {
            Scene scene = entity.getScene();
            AnalogControl leftAnalog = scene.getEntity("left_analog").getComponent(AnalogControl.class);
            AnalogControl rightAnalog = scene.getEntity("right_analog").getComponent(AnalogControl.class);

            Vec2.smul(velocity, leftAnalog.analog, entity.getComponent(StatusControl.class).getSpeed());

            RigidBody rigidBody = entity.getComponent(RigidBody.class);
            rigidBody.velocity.add(velocity);

            AnimationControl animationControl = entity.getComponent(AnimationControl.class);

            if (rightAnalog.analog.length() > MIN_FIRE_INPUT) {
                Vec2.normalize(animationControl.direction, rightAnalog.analog);
                animationControl.fromVelocity = false;

                Vec2.normalize(fireDir, rightAnalog.analog);
                fire();
            } else {
                animationControl.fromVelocity = true;
            }
        } else {
            entity.getComponent(RigidBody.class).setType(RigidBody.Type.Static);
        }

        return this;
    }

    private void setGun(Gun fireType) {
        switch (fireType) {
            case Pistol: {
                fireFrequency = fireFrequencyTime = 0.5f;
                fireSpeed = 2f;
                fireDamage = 20;
                break;
            }
            case Shotgun: {
                fireFrequency = fireFrequencyTime = 1f;
                fireSpeed = 2f;
                fireDamage = 15;
                break;
            }
            case Uzi: {
                fireFrequency = fireFrequencyTime = 0.25f;
                fireSpeed = 2.5f;
                fireDamage = 10;
                break;
            }
            case FlameThrower: {
                fireFrequency = fireFrequencyTime = 0.25f;
                fireSpeed = 1f;
                fireDamage = 10;
                break;
            }
            case Bazooka: {
                fireFrequency = fireFrequencyTime = 2f;
                fireSpeed = 1.5f;
                fireDamage = 100;
                break;
            }
        }
        this.fireType = fireType;
    }

    private void fire() {
        Scene scene = entity.getScene();

        fireFrequencyTime += scene.getTime().getFixedDelta();

        if (fireFrequencyTime >= fireFrequency) {
            fireFrequencyTime = 0f;

            scene.addEntity(
                    Entities.createBullet(
                            entity.getComponent(Transform2D.class).getPosition(),
                            fireDir,
                            fireSpeed,
                            Utils.attack(fireDamage)
                    )
            );
        }
    }
}
