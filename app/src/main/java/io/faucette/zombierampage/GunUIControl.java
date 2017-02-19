package io.faucette.zombierampage;


import io.faucette.math.Vec2;
import io.faucette.scene_graph.Component;
import io.faucette.transform_components.Transform2D;
import io.faucette.ui_component.UI;


public class GunUIControl extends Pauseable {
    private float screenWidth;
    private float screenHeight;

    private boolean hover;
    private boolean lastHover;

    public GunUIControl() {

        super();

        hover = false;
        lastHover = false;
    }

    private GunUIControl updatePosition(InputPlugin input) {
        float size = 128f;

        entity.getComponent(Transform2D.class)
                .setPosition(new Vec2(input.getWidth() * 0.5f, input.getHeight() - (size * 0.5f)));

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
            case Pistol: {
                uiComponent
                        .setWidth(96f)
                        .setHeight(96f)
                        .setImage(R.drawable.pistol);
                break;
            }
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
        }

        return this;
    }

    @Override
    public GunUIControl update() {
        InputPlugin input = entity.getScene().getPlugin(InputPlugin.class);

        if (screenWidth != input.getWidth() || screenHeight != input.getHeight()) {
            screenWidth = input.getWidth();
            screenHeight = input.getHeight();
            updatePosition(input);
        }

        if (this.isPaused()) {
            return this;
        }

        hover = false;
        UI ui = entity.getComponent(UI.class);
        for (InputPlugin.Touch touch : input.getTouches()) {
            if (ui.contains(touch.position)) {
                hover = true;
            }
        }
        if (lastHover == true && hover == false) {
            //switchGun();
        }
        lastHover = hover;

        return this;
    }
}
