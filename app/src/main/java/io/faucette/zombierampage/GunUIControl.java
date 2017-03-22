package io.faucette.zombierampage;


import io.faucette.event_emitter.Emitter;
import io.faucette.math.Vec2;
import io.faucette.transform_components.Transform2D;
import io.faucette.ui_component.UI;


public class GunUIControl extends Pauseable {

    public GunUIControl() {
        super();
    }

    public GunUIControl init() {
        return this;
    }

    private GunUIControl switchGun() {
        PlayerControl playerControl = entity.getScene().getEntity("player")
                .getComponent(PlayerControl.class);

        int ordinal = playerControl.getGunType().ordinal() + 1;

        if (ordinal >= PlayerControl.GunType.values().length) {
            playerControl.setGunType(PlayerControl.GunType.Pistol);
        } else {
            playerControl.setGunType(PlayerControl.GunType.values()[ordinal]);
        }

        return updateGun();
    }

    public GunUIControl updateGun() {
        PlayerControl playerControl = entity.getScene().getEntity("player")
                .getComponent(PlayerControl.class);

        UI uiComponent = entity.getComponent(UI.class);

        switch (playerControl.getGunType()) {
            case Shotgun: {
                uiComponent
                        .setWidth(192f)
                        .setHeight(96f)
                        .setImage(R.drawable.shotgun);
                break;
            }
            case Uzi: {
                uiComponent
                        .setWidth(96f)
                        .setHeight(96f)
                        .setImage(R.drawable.uzi);
                break;
            }
            case FlameThrower: {
                uiComponent
                        .setWidth(192f)
                        .setHeight(96f)
                        .setImage(R.drawable.flamethrower);
                break;
            }
            case Pistol:
            default: {
                uiComponent
                        .setWidth(96f)
                        .setHeight(96f)
                        .setImage(R.drawable.pistol);
                break;
            }
        }

        return this;
    }
}
