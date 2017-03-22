package io.faucette.zombierampage;

import io.faucette.scene_graph.Component;
import io.faucette.ui_component.UI;

/**
 * Created by nathan on 3/22/17.
 */

public class GunUIAmmoControl extends Component {

    public GunUIAmmoControl() {
        super();
    }

    public GunUIAmmoControl update() {
        int ammoCount = entity.getScene().getEntity("player").getComponent(PlayerControl.class).getAmmoCount();
        entity.getComponent(UI.class).setText(ammoCount + "");
        return this;
    }
}
