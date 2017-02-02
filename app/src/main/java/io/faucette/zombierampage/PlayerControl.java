package io.faucette.zombierampage;

import io.faucette.math.Vec2;
import io.faucette.scene_graph.Component;
import io.faucette.scene_graph.Scene;
import io.faucette.transform_components.Transform2D;


public class PlayerControl extends Component {
    private static float MIN_FIRE_INPUT = 0.01f;
    private int shotgunAmmo = 0;
    private int uziAmmo = 0;
    private int flamethrowerAmmo = 0;
    private int bazookaAmmo = 0;
    private int gunAmmo = 0;
    private float gunFrequencyTime = 0f;
    private float gunFrequency = 0.5f;
    private int gunDamage = 10;
    private float gunSpeed = 2f;
    private Vec2 gunDir = new Vec2();
    private GunType gunType = GunType.Pistol;
    private Vec2 velocity = new Vec2();

    public PlayerControl() {
        super();

        setGun(GunType.Pistol);
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

                Vec2.normalize(gunDir, rightAnalog.analog);
                fire();
            } else {
                animationControl.fromVelocity = true;
            }
        } else {
            entity.getComponent(RigidBody.class).setType(RigidBody.Type.Static);
        }

        return this;
    }

    public void getHealth() {
        entity.getComponent(StatusControl.class).getHealth();
    }

    public void getAmmo(GunType gunType) {
        switch (gunType) {
            case Shotgun: {
                shotgunAmmo += 5 + (int) (Math.random() * 10);
                break;
            }
            case Uzi: {
                shotgunAmmo += 25 + (int) (Math.random() * 25);
                break;
            }
            case FlameThrower: {
                shotgunAmmo += 25 + (int) (Math.random() * 100);
                break;
            }
            case Bazooka: {
                shotgunAmmo += 1 + (int) (Math.random() * 2);
                break;
            }
        }
    }

    private void setGun(GunType gunType) {
        switch (gunType) {
            case Pistol: {
                gunAmmo = -1;
                gunFrequency = gunFrequencyTime = 0.5f;
                gunSpeed = 2f;
                gunDamage = 20;
                break;
            }
            case Shotgun: {
                if (shotgunAmmo == 0) {
                    setGun(GunType.Pistol);
                } else {
                    gunAmmo = shotgunAmmo;
                    gunFrequency = gunFrequencyTime = 1f;
                    gunSpeed = 2f;
                    gunDamage = 15;
                }
                break;
            }
            case Uzi: {
                if (uziAmmo == 0) {
                    setGun(GunType.Shotgun);
                } else {
                    gunAmmo = uziAmmo;
                    gunFrequency = gunFrequencyTime = 0.25f;
                    gunSpeed = 2.5f;
                    gunDamage = 10;
                }
                break;
            }
            case FlameThrower: {
                if (flamethrowerAmmo == 0) {
                    setGun(GunType.Uzi);
                } else {
                    gunAmmo = flamethrowerAmmo;
                    gunFrequency = gunFrequencyTime = 0.25f;
                    gunSpeed = 1f;
                    gunDamage = 10;
                }
                break;
            }
            case Bazooka: {
                if (bazookaAmmo == 0) {
                    setGun(GunType.FlameThrower);
                } else {
                    gunAmmo = bazookaAmmo;
                    gunFrequency = gunFrequencyTime = 2f;
                    gunSpeed = 1.5f;
                    gunDamage = 100;
                }
                break;
            }
        }
        this.gunType = gunType;
    }

    private void fire() {
        Scene scene = entity.getScene();

        if (gunAmmo == 0) {
            setGun(GunType.values()[gunType.ordinal() - 1]);
        }

        gunFrequencyTime += scene.getTime().getFixedDelta();

        if (gunFrequencyTime >= gunFrequency) {
            gunFrequencyTime = 0f;

            switch (gunType) {
                case Pistol: {
                    fireBullet();
                    break;
                }
                case Shotgun: {
                    fireShotgun();
                    break;
                }
                case Uzi: {
                    fireBullet();
                    break;
                }
                case FlameThrower: {
                    fireBullet();
                    break;
                }
                case Bazooka: {
                    fireBullet();
                    break;
                }
            }
        }
    }

    private void fireBullet() {
        entity.getScene().addEntity(
                Entities.createBullet(
                        entity.getComponent(Transform2D.class).getPosition(),
                        gunDir,
                        gunSpeed,
                        Utils.attack(gunDamage)
                )
        );
    }

    private void fireShotgun() {
        Scene scene = entity.getScene();
        Vec2 position = entity.getComponent(Transform2D.class).getPosition();

        scene.addEntity(Entities.createBullet(position, gunDir.transform((float) (Math.PI * -0.0625)), gunSpeed, Utils.attack(gunDamage)));
        scene.addEntity(Entities.createBullet(position, gunDir.transform((float) (Math.PI * 0.03125)), gunSpeed, Utils.attack(gunDamage)));
        scene.addEntity(Entities.createBullet(position, gunDir.transform((float) (Math.PI * 0.03125)), gunSpeed, Utils.attack(gunDamage)));
        scene.addEntity(Entities.createBullet(position, gunDir.transform((float) (Math.PI * 0.03125)), gunSpeed, Utils.attack(gunDamage)));
    }

    public enum GunType {
        Pistol,
        Shotgun,
        Uzi,
        FlameThrower,
        Bazooka,
    }
}
