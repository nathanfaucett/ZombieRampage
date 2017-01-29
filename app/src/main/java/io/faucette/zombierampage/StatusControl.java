package io.faucette.zombierampage;

import java.util.Random;

import io.faucette.scene_graph.Component;

/**
 * Created by nathan on 1/28/17.
 */

public class StatusControl extends Component {
    private static Random random = new Random();

    private int hp;
    private int pp;
    private float speed;
    private float birthTime;
    private float hitTime;
    private float deadTime;
    private State state;
    private float birthTimeCurrent = 0f;
    private float hitTimeCurrent = 0f;
    private float deadTimeCurrent = 0f;
    public StatusControl(int hp, int pp, float speed, float birthTime, float hitTime, float deadTime) {
        this.hp = hp;
        this.pp = pp;
        this.speed = speed;
        this.birthTime = birthTime;
        this.hitTime = hitTime;
        this.deadTime = deadTime;
        state = State.Birth;
    }

    public StatusControl(int hp, int pp, float speed, float birthTime) {
        this(hp, pp, speed, birthTime, 0.25f, 1f);
    }
    public StatusControl(int hp, int pp, float speed) {
        this(hp, pp, speed, 2f);
    }

    public float getSpeed() {
        return speed;
    }
    public State getState() { return state; }

    public void takeDamage(int amount) {
        if (state != State.Hit && state != State.Dead) {
            hp -= amount;

            if (hp <= 0) {
                state = State.Dying;
            } else {
                state = State.Hit;
            }
        }
    }

    public void attack(StatusControl other) {
        int base = (int) (pp * 0.5f);
        int amount = base + ((int) (random.nextFloat() * (pp - base)));
        other.takeDamage(amount);
    }

    @Override
    public StatusControl update() {
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
                deadTimeCurrent += dt;

                if (deadTimeCurrent >= deadTime) {
                    deadTimeCurrent = 0f;
                    state = State.Dead;
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
