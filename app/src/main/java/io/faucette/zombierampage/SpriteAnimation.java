package io.faucette.zombierampage;


import java.util.HashMap;

import io.faucette.scene_graph.Component;
import io.faucette.sprite_component.Sprite;


public class SpriteAnimation extends Component {
    private HashMap<String, float[][]> animations;
    private String animation;
    private int currentFrame;
    private double frameStartTime;
    private float speed;


    public SpriteAnimation(HashMap<String, float[][]> animations, String animation) {
        super();

        this.animations = animations;
        this.animation = animation;
        currentFrame = 0;
        frameStartTime = 0d;
        speed = 1f;
    }

    @Override
    public SpriteAnimation update() {
        float[][] frames = animations.get(animation);

        if (frames != null) {
            float[] frame = frames[currentFrame];
            double now = getEntity().getScene().getTime().getCurrent();

            Sprite sprite = getEntity().getComponent(Sprite.class);
            sprite.setX(frame[0]);
            sprite.setY(frame[1]);
            sprite.setW(frame[2]);
            sprite.setH(frame[3]);

            if (frame[4] != 0f) {
                if (speed * frame[4] < (float) (now - frameStartTime)) {
                    frameStartTime = getEntity().getScene().getTime().getCurrent();

                    if (currentFrame + 1 < frames.length) {
                        currentFrame += 1;
                    } else {
                        currentFrame = 0;
                    }
                }
            }
        }

        return this;
    }

    public SpriteAnimation play(String animation) {
        this.animation = animation;
        return this;
    }

    public SpriteAnimation setSpeed(float s) {
        speed = s;
        return this;
    }
}
