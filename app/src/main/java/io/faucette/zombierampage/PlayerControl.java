package io.faucette.zombierampage;

import io.faucette.math.Vec2;
import io.faucette.scene_graph.Scene;
import io.faucette.transform_components.Transform2D;


public class PlayerControl extends Pauseable {
    private static float MIN_FIRE_INPUT = 0.01f;
    private int shotgunAmmo = 0;
    private int uziAmmo = 0;
    private int flamethrowerAmmo = 0;
    private int gunAmmo = 0;
    private float gunFrequencyTime = 0f;
    private float gunFrequency = 0.5f;
    private int gunDamage = 1;
    private float gunSpeed = 2f;
    private Vec2 gunDir = new Vec2();
    private GunType gunType = GunType.Pistol;
    private Vec2 velocity = new Vec2();

    public PlayerControl() {
        super();

        setGunType(GunType.Pistol);
    }

    @Override
    public PlayerControl update() {
        if (this.isPaused()) {
            return this;
        }

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
                shotgunAmmo += 5 + (int) (Math.random() * 5);

                if (this.gunType == GunType.Shotgun) {
                    this.gunAmmo = shotgunAmmo;
                }

                if (this.gunType.ordinal() < GunType.Shotgun.ordinal()) {
                    updateGunType(GunType.Shotgun);
                }
                break;
            }
            case Uzi: {
                uziAmmo += 10 + (int) (Math.random() * 10);

                if (this.gunType == GunType.Uzi) {
                    this.gunAmmo = uziAmmo;
                }

                if (this.gunType.ordinal() < GunType.Uzi.ordinal()) {
                    updateGunType(GunType.Uzi);
                }
                break;
            }
            case FlameThrower: {
                flamethrowerAmmo += 10 + (int) (Math.random() * 10);

                if (this.gunType == GunType.FlameThrower) {
                    this.gunAmmo = uziAmmo;
                }

                if (this.gunType.ordinal() < GunType.FlameThrower.ordinal()) {
                    updateGunType(GunType.FlameThrower);
                }
                break;
            }
        }
    }

    public int getAmmoCount() {
        return gunAmmo;
    }

    public GunType getGunType() {
        return gunType;
    }

    public GunType setGunType(GunType gunType) {
        switch (gunType) {
            case Pistol: {
                gunAmmo = -1;
                gunFrequency = 0.5f;
                gunFrequencyTime = 0f;
                gunSpeed = 2f;
                gunDamage = 1;
                break;
            }
            case Shotgun: {
                if (shotgunAmmo == 0) {
                    return setGunType(GunType.Pistol);
                } else {
                    gunAmmo = shotgunAmmo;
                    gunFrequency = 1f;
                    gunFrequencyTime = 0f;
                    gunSpeed = 1.5f;
                    gunDamage = 1;
                }
                break;
            }
            case Uzi: {
                if (uziAmmo == 0) {
                    return setGunType(GunType.Shotgun);
                } else {
                    gunAmmo = uziAmmo;
                    gunFrequency = 0.1f;
                    gunFrequencyTime = 0f;
                    gunSpeed = 3f;
                    gunDamage = 1;
                }
                break;
            }
            case FlameThrower: {
                if (flamethrowerAmmo == 0) {
                    return setGunType(GunType.Uzi);
                } else {
                    gunAmmo = flamethrowerAmmo;
                    gunFrequency = 0.1f;
                    gunFrequencyTime = 0f;
                    gunSpeed = 1f;
                    gunDamage = 1;
                }
                break;
            }
            default: {
                return setGunType(GunType.Pistol);
            }

        }

        this.gunType = gunType;

        return this.gunType;
    }

    public void updateGunType(GunType gunType) {
        setGunType(gunType);
        entity.getScene().getEntity("gun_ui")
                .getComponent(GunUIControl.class).updateGun();
    }

    private void fire() {
        Scene scene = entity.getScene();

        if (gunAmmo == 0) {
            int ordinal = gunType.ordinal() - 1;

            if (ordinal < 0) {
                updateGunType(GunType.Pistol);
            } else {
                updateGunType(GunType.values()[ordinal]);
            }
        }

        gunFrequencyTime += scene.getTime().getFixedDelta();

        if (gunFrequencyTime >= gunFrequency) {
            gunFrequencyTime = 0f;

            if (gunAmmo != -1) {
                gunAmmo -= 1;
            }

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
                    fireFlamethrowerBullet();
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
                        gunDamage
                )
        );
    }

    private void fireShotgun() {
        Scene scene = entity.getScene();
        Vec2 position = entity.getComponent(Transform2D.class).getPosition();

        scene.addEntity(Entities.createBullet(position, gunDir.transform((float) (Math.PI * -0.0625)), gunSpeed, gunDamage));
        scene.addEntity(Entities.createBullet(position, gunDir.transform((float) (Math.PI * 0.03125)), gunSpeed, gunDamage));
        scene.addEntity(Entities.createBullet(position, gunDir.transform((float) (Math.PI * 0.03125)), gunSpeed, gunDamage));
        scene.addEntity(Entities.createBullet(position, gunDir.transform((float) (Math.PI * 0.03125)), gunSpeed, gunDamage));
    }

    private void fireFlamethrowerBullet() {
        entity.getScene().addEntity(
                Entities.createFlamethrowerBullet(
                        entity.getComponent(Transform2D.class).getPosition(),
                        gunDir.transform(
                                (float) ((Math.PI * -0.03125) + (Math.random() * (Math.PI * 0.0625)))
                        ),
                        gunSpeed,
                        gunDamage
                )
        );
    }

    public enum GunType {
        Pistol,
        Shotgun,
        Uzi,
        FlameThrower,
    }
}
