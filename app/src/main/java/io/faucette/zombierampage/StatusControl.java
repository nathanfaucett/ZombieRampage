package io.faucette.zombierampage;

import io.faucette.math.Vec2;
import io.faucette.scene_graph.Scene;
import io.faucette.transform_components.Transform2D;

/**
 * Created by nathan on 1/28/17.
 */

public class StatusControl extends Pauseable {
    private int hp;
    private int maxHp;
    private int pp;
    private float speed;
    private float birthTime;
    private float hitTime;
    private float dyingTime;
    private float deadTime;
    private State state;
    private boolean allowHitWhileHit = true;
    private boolean dropItem = true;
    private float dropChance = 0.75f;
    private float birthTimeCurrent = 0f;
    private float hitTimeCurrent = 0f;
    private float dyingTimeCurrent = 0f;
    private float deadTimeCurrent = 0f;

    public StatusControl(int hp, int pp, float speed, float birthTime, float hitTime, float dyingTime, float deadTime) {
        this.maxHp = this.hp = hp;
        this.pp = pp;
        this.speed = speed;
        this.birthTime = birthTime;
        this.hitTime = hitTime;
        this.dyingTime = dyingTime;
        this.deadTime = deadTime;
        state = State.Birth;
    }

    public StatusControl(int hp, int pp, float speed, float birthTime) {
        this(hp, pp, speed, birthTime, 0.25f, 1f, 3f);
    }

    public StatusControl(int hp, int pp, float speed) {
        this(hp, pp, speed, 2f);
    }

    public StatusControl setAllowHitWhileHit(boolean allowHitWhileHit) {
        this.allowHitWhileHit = allowHitWhileHit;
        return this;
    }

    public StatusControl setDropItem(boolean dropItem) {
        this.dropItem = dropItem;
        return this;
    }

    public StatusControl setDropChance(float dropChance) {
        this.dropChance = dropChance;
        return this;
    }

    public float getSpeed() {
        return speed;
    }

    public State getState() {
        return state;
    }

    public void getHealth() {
        int amount = 4;

        if (hp + amount > maxHp) {
            hp = maxHp;
        } else {
            hp += amount;
        }

        if (entity.getName() == "player") {
            entity.getScene()
                    .getEntity("health_ui")
                    .getComponent(HealthUIControl.class)
                    .updateHearts(hp);
        }
    }

    public void takeDamage(int amount) {
        if ((allowHitWhileHit || state != State.Hit) && state != State.Dead) {
            hp -= amount;

            if (hp <= 0) {
                state = State.Dying;
                entity.getComponent(RigidBody.class).setDisabled(true);
            } else {
                state = State.Hit;
            }

            if (entity.getName() == "player") {
                entity.getScene()
                        .getEntity("health_ui")
                        .getComponent(HealthUIControl.class)
                        .updateHearts(hp);
            }
        }
    }

    public void attack(StatusControl other) {
        if (state == State.Alive) {
            other.takeDamage(pp);
        }
    }

    @Override
    public StatusControl update() {
        if (this.isPaused()) {
            return this;
        }

        float dt = (float) entity.getScene().getTime().getDelta();

        switch (state) {
            case Alive: {
                break;
            }
            case Hit: {
                hitTimeCurrent += dt;

                if (hitTimeCurrent >= hitTime) {
                    hitTimeCurrent = 0f;
                    state = State.Alive;
                }
                break;
            }
            case Dying: {
                dyingTimeCurrent += dt;

                if (dyingTimeCurrent >= dyingTime) {
                    dyingTimeCurrent = 0f;
                    state = State.Dead;
                }
                break;
            }
            case Dead: {
                deadTimeCurrent += dt;

                if (deadTimeCurrent >= deadTime) {
                    Scene scene = entity.getScene();
                    scene.removeEntity(entity);

                    if (entity.getTag() == "enemy") {
                        getLevelControl().enemyKilled();
                    }

                    if (dropItem && (Math.random() < dropChance)) {
                        Vec2 position = entity.getComponent(Transform2D.class).getPosition();
                        float chance = (float) Math.random();

                        if (chance < 0.4f) {
                            scene.addEntity(Entities.createAmmo(PlayerControl.GunType.Shotgun, position));
                        } else if (chance < 0.6f) {
                            scene.addEntity(Entities.createAmmo(PlayerControl.GunType.Uzi, position));
                        } else if (chance < 0.8f) {
                            scene.addEntity(Entities.createHealth(position));
                        } else if (chance < 1.0f) {
                            scene.addEntity(Entities.createAmmo(PlayerControl.GunType.FlameThrower, position));
                        }
                    }
                }
                break;
            }
            case Birth: {
                birthTimeCurrent += dt;

                if (birthTimeCurrent >= birthTime) {
                    birthTimeCurrent = 0f;
                    state = State.Alive;
                }
                break;
            }
        }
        return this;
    }

    public enum State {
        Birth,
        Alive,
        Hit,
        Dying,
        Dead,
    }
}
