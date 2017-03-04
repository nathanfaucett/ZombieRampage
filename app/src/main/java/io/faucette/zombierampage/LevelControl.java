package io.faucette.zombierampage;


import java.util.List;

import io.faucette.scene_graph.Component;
import io.faucette.scene_graph.Entity;
import io.faucette.scene_graph.Scene;


public class LevelControl extends Component {
    public static enum State {
        Wave,
        Done,
    }

    private GLRenderer renderer;

    private int wave = 0;
    private int enemiesOrigTotal = 8;
    private int enemiesTotal = enemiesOrigTotal;
    private int enemiesLeft = enemiesOrigTotal;

    private float currentTime = 0f;
    private float rate = 4f;
    private boolean playing;

    private float inBetweenTime = 4f;
    private float inBetweenTimeCurrent = 0f;
    private State state;


    public LevelControl(GLRenderer renderer) {
        super();
        this.renderer = renderer;
        playing = true;
        state = State.Done;
    }

    public void loadMenu() {
        renderer.game.loadMenu();
    }

    public boolean isPaused() {
        return playing == false;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void pause() {
        playing = false;
    }

    public void resume() {
        playing = true;
    }

    public LevelControl update() {
        if (isPaused()) {
            return this;
        }
        float dt = (float) entity.getScene().getTime().getDelta();

        switch (state) {
            case Done:
                if (inBetweenTimeCurrent == 0) {
                    wave += 1;
                    entity.getScene().addEntity(UIEntities.createWaveText(wave));
                }

                inBetweenTimeCurrent += dt;

                if (inBetweenTimeCurrent >= inBetweenTime) {
                    inBetweenTimeCurrent = 0f;
                    state = State.Wave;
                    reset();
                }
                break;
            case Wave:
                currentTime += dt;

                if (enemiesTotal != 0 && currentTime > rate) {
                    enemiesTotal -= 1;
                    currentTime = 0f;
                    spawn();
                }

                if (enemiesTotal == 0 && enemiesLeft == 0) {
                    state = State.Done;
                }
                break;
        }

        return this;
    }

    public void enemyKilled() {
        enemiesLeft -= 1;
    }

    private void reset() {
        enemiesTotal = enemiesOrigTotal + (int) (enemiesOrigTotal * 0.5f);
        enemiesOrigTotal = enemiesTotal;
        enemiesLeft = enemiesTotal;
        rate *= 0.9f;
        currentTime = 0f;
    }

    private void spawn() {
        Scene scene = entity.getScene();

        TileControlManager tileControlManager = scene.getComponentManager(TileControlManager.class);
        List<TileControl> tiles = tileControlManager.getTiles();

        int index = (int) (Math.random() * ((float) tiles.size()));
        TileControl tile = tiles.get(index);
        float size = tile.getSize();
        float smallSize = size * 0.9f;
        float x = ((tile.getX() * size) - (smallSize * 0.5f)) + ((float) Math.random() * smallSize);
        float y = ((tile.getY() * size) - (smallSize * 0.5f)) + ((float) Math.random() * smallSize);

        Entity entity;
        double r = Math.random();

        if (r < 0.4) {
            entity = Entities.createRegEnemy(x, y);
        } else if (r < 0.7) {
            entity = Entities.createFastEnemy(x, y);
        } else {
            entity = Entities.createSlowEnemy(x, y);
        }

        scene.addEntity(entity);
    }
}
