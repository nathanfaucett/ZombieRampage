package io.faucette.zombierampage;

import io.faucette.event_emitter.Emitter;
import io.faucette.math.Vec2;
import io.faucette.scene_graph.Component;
import io.faucette.scene_graph.Entity;
import io.faucette.scene_graph.Scene;
import io.faucette.transform_components.Transform2D;
import io.faucette.ui_component.UI;

/**
 * Created by nathan on 2/15/17.
 */

public class UIEntities {


    public static Entity createPlay() {
        UIControl uiControl = new UIControl()
                .setOffset(new Vec2(0f, -80f))
                .setAnchor(UIControl.Anchor.Center);

        uiControl.on("touch", new Emitter.Callback() {
            @Override
            public void call(Emitter emitter, Object[] objects) {
                UIControl uiControl = (UIControl) emitter;
                Scene scene = uiControl.getEntity().getScene();
                MenuControl menuControl = scene.getEntity("menu_control").getComponent(MenuControl.class);
                menuControl.loadGame();
            }
        });

        return new Entity("play_btn_ui")
                .addComponent(uiControl)
                .addComponent(new Transform2D())
                .addComponent(new UI()
                        .setY(0.5f)
                        .setH(0.5f)
                        .setWidth(256f)
                        .setHeight(128f)
                        .setImage(R.drawable.play_btn));
    }

    public static Entity createSignIn() {
        UIControl uiControl = new UIControl()
                .setOffset(new Vec2(0f, 80f))
                .setAnchor(UIControl.Anchor.Center);

        uiControl.on("touch", new Emitter.Callback() {
            @Override
            public void call(Emitter emitter, Object[] objects) {
                UIControl uiControl = (UIControl) emitter;
                final Entity entity = uiControl.getEntity();
                final Scene scene = entity.getScene();

                MenuControl menuControl = scene.getEntity("menu_control").getComponent(MenuControl.class);
                menuControl.signIn();
                scene.addEntity(UIEntities.createLoading());

                menuControl.onSignIn(new MainActivity.SignInCallback() {
                    @Override
                    public void call(boolean signedIn) {
                        if (signedIn) {
                            scene.removeEntity(entity);
                            scene.removeEntity(scene.getEntity("loading_ui"));
                            scene.addEntity(UIEntities.createLeaderboard());
                        }
                    }
                });

                menuControl.signIn();
            }
        });

        return new Entity("signin_btn_ui")
                .addComponent(uiControl)
                .addComponent(new Transform2D())
                .addComponent(new UI()
                        .setY(0.5f)
                        .setH(0.5f)
                        .setWidth(512f)
                        .setHeight(128f)
                        .setImage(R.drawable.signin));
    }

    public static Entity createLeaderboard() {
        UIControl uiControl = new UIControl()
                .setOffset(new Vec2(0f, 80f))
                .setAnchor(UIControl.Anchor.Center);

        uiControl.on("touch", new Emitter.Callback() {
            @Override
            public void call(Emitter emitter, Object[] objects) {
                UIControl uiControl = (UIControl) emitter;
                Scene scene = uiControl.getEntity().getScene();
                MenuControl menuControl = scene.getEntity("menu_control").getComponent(MenuControl.class);
                menuControl.showLeaderBoard();
            }
        });

        return new Entity("leaderboard_btn_ui")
                .addComponent(uiControl)
                .addComponent(new Transform2D())
                .addComponent(new UI()
                        .setY(0.5f)
                        .setH(0.5f)
                        .setWidth(512f)
                        .setHeight(128f)
                        .setImage(R.drawable.leaderboard_btn));
    }

    public static Entity createMainMenu(boolean isSignedIn) {
        return new Entity("main_menu_ui")
                .addChild(UIEntities.createPlay())
                .addChild(isSignedIn ? UIEntities.createLeaderboard() : UIEntities.createSignIn());
    }

    public static Entity createLoading() {
        UIControl uiControl = new UIControl()
                .setOffset(new Vec2(0f, 0f))
                .setIsButton(false)
                .setAnchor(UIControl.Anchor.Center);

        return new Entity("loading_ui")
                .addComponent(uiControl)
                .addComponent(new Transform2D())
                .addComponent(new UI()
                        .setWidth(512f)
                        .setHeight(128f)
                        .setImage(R.drawable.loading));
    }

    public static Entity createResume() {
        UIControl uiControl = new UIControl()
                .setOffset(new Vec2(0f, -64f))
                .setAnchor(UIControl.Anchor.Center);

        uiControl.on("touch", new Emitter.Callback() {
            @Override
            public void call(Emitter emitter, Object[] objects) {
                UIControl uiControl = (UIControl) emitter;
                Scene scene = uiControl.getEntity().getScene();
                LevelControl levelControl = scene.getEntity("level_control").getComponent(LevelControl.class);

                if (levelControl.isPaused()) {
                    levelControl.resume();
                    Entity entity = scene.getEntity("pause_menu_ui");
                    scene.removeEntity(entity);
                }
            }
        });

        return new Entity("resume_btn_ui")
                .addComponent(uiControl)
                .addComponent(new Transform2D())
                .addComponent(new UI()
                        .setY(0.5f)
                        .setH(0.5f)
                        .setWidth(256f)
                        .setHeight(128f)
                        .setImage(R.drawable.resume_btn));
    }

    public static Entity createQuit() {
        UIControl uiControl = new UIControl()
                .setOffset(new Vec2(0f, 64f))
                .setAnchor(UIControl.Anchor.Center);

        uiControl.on("touch", new Emitter.Callback() {
            @Override
            public void call(Emitter emitter, Object[] objects) {
                UIControl uiControl = (UIControl) emitter;
                Scene scene = uiControl.getEntity().getScene();
                LevelControl levelControl = scene.getEntity("level_control").getComponent(LevelControl.class);
                levelControl.loadMenu();
            }
        });

        return new Entity("quit_btn_ui")
                .addComponent(uiControl)
                .addComponent(new Transform2D())
                .addComponent(new UI()
                        .setY(0.5f)
                        .setH(0.5f)
                        .setWidth(256f)
                        .setHeight(128f)
                        .setImage(R.drawable.quit_btn));
    }

    public static Entity createRestart() {
        UIControl uiControl = new UIControl()
                .setOffset(new Vec2(0f, -64f))
                .setAnchor(UIControl.Anchor.Center);

        uiControl.on("touch", new Emitter.Callback() {
            @Override
            public void call(Emitter emitter, Object[] objects) {
                UIControl uiControl = (UIControl) emitter;
                Scene scene = uiControl.getEntity().getScene();
                LevelControl levelControl = scene.getEntity("level_control").getComponent(LevelControl.class);
                levelControl.restartGame();
            }
        });

        return new Entity("restart_btn_ui")
                .addComponent(uiControl)
                .addComponent(new Transform2D())
                .addComponent(new UI()
                        .setY(0.5f)
                        .setH(0.5f)
                        .setWidth(256f)
                        .setHeight(128f)
                        .setImage(R.drawable.restart_btn));
    }

    public static Entity createPauseMenu() {
        return new Entity("pause_menu_ui")
                .addChild(UIEntities.createResume())
                .addChild(UIEntities.createQuit());
    }

    public static Entity createGameOverMenu() {
        return new Entity("game_over_ui")
                .addChild(UIEntities.createRestart())
                .addChild(UIEntities.createQuit());
    }

    public static Entity createPauseBtn() {
        UIControl uiControl = new UIControl()
                .setAnchor(UIControl.Anchor.TopLeft);

        uiControl.on("touch", new Emitter.Callback() {
            @Override
            public void call(Emitter emitter, Object[] objects) {
                UIControl uiControl = (UIControl) emitter;
                Scene scene = uiControl.getEntity().getScene();
                LevelControl levelControl = scene.getEntity("level_control").getComponent(LevelControl.class);

                if (levelControl.isPlaying()) {
                    levelControl.pause();
                    scene.addEntity(UIEntities.createPauseMenu());
                }
            }
        });

        return new Entity("pause_btn_ui")
                .addComponent(uiControl)
                .addComponent(new Transform2D()
                        .setPosition(new Vec2(40f, 40f)))
                .addComponent(new UI()
                        .setY(0.5f)
                        .setH(0.5f)
                        .setWidth(64f)
                        .setHeight(64f)
                        .setImage(R.drawable.pause_btn));
    }

    public static Entity createAnalog(AnalogControl.Side side) {
        return new Entity(side == AnalogControl.Side.Left ? "left_analog" : "right_analog")
                .addComponent(new Transform2D())
                .addComponent(new AnalogControl(side))
                .addComponent(new UI()
                        .setWidth(128f)
                        .setHeight(128f)
                        .setImage(R.drawable.analog_lg))
                .addChild(new Entity()
                        .addComponent(new Transform2D())
                        .addComponent(new UI()
                                .setWidth(64f)
                                .setHeight(64f)
                                .setImage(R.drawable.analog_sm)));
    }

    public static Entity createHealth(int hearts) {
        Entity entity = new Entity("health_ui")
                .addComponent(new Transform2D())
                .addComponent(new HealthUIControl(hearts));

        for (int i = 0; i < hearts; i++) {
            entity.addChild(createHealthHeart());
        }

        return entity;
    }

    public static Entity createHealthHeart() {
        return new Entity()
                .addComponent(new Transform2D())
                .addComponent(new UI()
                        .setWidth(32f)
                        .setHeight(32f)
                        .setImage(R.drawable.heart_4_4));
    }

    public static Entity createGun() {
        return new Entity("gun_ui")
                .addComponent(new GunUIControl())
                .addComponent(new Transform2D())
                .addComponent(new UIControl().setAnchor(UIControl.Anchor.CenterBottom))
                .addComponent(new UI()
                        .setWidth(96f)
                        .setHeight(96f)
                        .setImage(R.drawable.pistol));
    }

    public static Entity createGunAmmoCount() {
        return new Entity("gun_ui_ammo")
                .addComponent(new GunUIAmmoControl())
                .addComponent(new Transform2D())
                .addComponent(new UIControl().setAnchor(UIControl.Anchor.CenterBottom))
                .addComponent(new UI()
                        .setFontColor(0xFF931C1C)
                        .setFontSize(24)
                        .setText("0"));
    }

    public static Entity createPoints() {
        UIControl uiControl = new UIControl()
                .setOffset(new Vec2(-8f, 8f))
                .setAnchor(UIControl.Anchor.TopRight);

        return new Entity("points")
                .addComponent(uiControl)
                .addComponent(new PointsControl())
                .addComponent(new Transform2D())
                .addComponent(new UI()
                        .setFontColor(0xFF931C1C)
                        .setFontSize(32)
                        .setText("0"));
    }

    public static Entity createWaveText(int waveNo) {
        UIControl uiControl = new UIControl()
                .setOffset(new Vec2(0f, 0f))
                .setAnchor(UIControl.Anchor.Center);

        return new Entity("wave_text")
                .addComponent(uiControl)
                .addComponent(new WaveTextControl(0.25f, 0.25f))
                .addComponent(new Transform2D())
                .addComponent(new UI()
                        .setFontColor(0xFF931C1C)
                        .setFontSize(32)
                        .setText("Wave " + waveNo));
    }
}
