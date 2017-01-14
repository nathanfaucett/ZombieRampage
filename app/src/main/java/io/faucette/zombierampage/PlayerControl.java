package io.faucette.zombierampage;

import io.faucette.math.Vec2;
import io.faucette.scene_graph.Component;
import io.faucette.scene_graph.Entity;
import io.faucette.scene_graph.Scene;
import io.faucette.transform_components.Transform2D;


public class PlayerControl extends Component {
    private Vec2 velocity = new Vec2();
    private Vec2 delta = new Vec2();


    public PlayerControl() {
        super();
    }

    @Override
    public PlayerControl update() {
        Entity entity = getEntity();
        Scene scene = entity.getScene();
        InputPlugin input = scene.getPlugin(InputPlugin.class);
        InputPlugin.Touch touch = input.getTouch();

        if (touch != null) {
            Transform2D transform = entity.getComponent(Transform2D.class);
            delta.x = touch.delta.x * 0.25f;
            delta.y = -touch.delta.y * 0.25f;
            transform.translate(Vec2.smul(velocity, delta, (float) scene.getTime().getDelta()));
        }

        return this;
    }
}
