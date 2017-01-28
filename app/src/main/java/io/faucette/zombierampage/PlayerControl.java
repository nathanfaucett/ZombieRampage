package io.faucette.zombierampage;

import io.faucette.math.Vec2;
import io.faucette.scene_graph.Component;
import io.faucette.scene_graph.Entity;
import io.faucette.scene_graph.Scene;


public class PlayerControl extends Component {
    private static float MIN_ANALOG_INPUT = 0.01f;
    private Vec2 velocity = new Vec2();


    public PlayerControl() {
        super();
    }

    @Override
    public PlayerControl update() {
        Entity entity = getEntity();
        Scene scene = entity.getScene();
        AnalogControl leftAnalog = scene.getEntity("left_analog").getComponent(AnalogControl.class);
        AnalogControl rightAnalog = scene.getEntity("right_analog").getComponent(AnalogControl.class);

        Vec2.smul(velocity, leftAnalog.analog, entity.getComponent(StatusControl.class).getSpeed());

        RigidBody rigidBody = entity.getComponent(RigidBody.class);
        rigidBody.velocity.add(velocity);

        DirectionControl directionControl = entity.getComponent(DirectionControl.class);

        if (rightAnalog.analog.length() > MIN_ANALOG_INPUT) {
            Vec2.normalize(directionControl.direction, rightAnalog.analog);
            directionControl.fromVelocity = false;
        } else {
            directionControl.fromVelocity = true;
        }

        return this;
    }
}
