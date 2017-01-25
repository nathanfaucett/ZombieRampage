package io.faucette.zombierampage;

import io.faucette.camera_component.Camera;
import io.faucette.camera_component.CameraManager;
import io.faucette.math.Vec2;
import io.faucette.scene_graph.Component;
import io.faucette.scene_graph.Entity;
import io.faucette.scene_graph.Scene;
import io.faucette.scene_renderer.SceneRenderer;
import io.faucette.transform_components.Transform2D;

/**
 * Created by nathan on 1/15/17.
 */

public class CameraControl extends Component {


    public CameraControl() {
        super();
    }

    @Override
    public CameraControl update() {
        Entity entity = getEntity();
        Scene scene = entity.getScene();
        Transform2D playerTransform = scene.getEntity("player").getComponent(Transform2D.class);
        Transform2D transform = entity.getComponent(Transform2D.class);
        transform.setPosition(playerTransform.getPosition());
        return this;
    }
}
