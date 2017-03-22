package io.faucette.zombierampage;


import java.util.List;

import io.faucette.scene_graph.Component;
import io.faucette.scene_graph.Entity;
import io.faucette.scene_graph.Scene;


public class LevelControl extends Component {
    private GLRenderer renderer;
    private int points = 0;
    private int wave = 0;
    private int enemiesOrigTotal = 16;
    private int enemiesTotal = enemiesOrigTotal;
    private int enemiesLeft = enemiesOrigTotal;
    private float currentTime = 0f;
    private float rate = 2f;
    private boolean playing = true;
    private float inBetweenTime = 4f;
    private float inBetweenTimeCurrent = 0f;
    private State state = State.Done;

    public LevelControl(GLRenderer renderer) {
        super();
        this.renderer = renderer;
    }

    public void loadMenu() {
        renderer.game.loadMenu();
    }

    public void restartGame() {
        renderer.game.loadGame();
    }

    public void showBanner() {
        renderer.activityControl.showBanner();
    }

    public void hideBanner() {
        renderer.activityControl.hideBanner();
    }

    public void gameOver() {
        Scene scene = entity.getScene();
        scene.removeEntity(scene.getEntity("pause_btn_ui"));
        scene.removeEntity(scene.getEntity("left_analog"));
        scene.removeEntity(scene.getEntity("right_analog"));
        scene.removeEntity(scene.getEntity("health_ui"));
        scene.removeEntity(scene.getEntity("gun_ui"));
        scene.removeEntity(scene.getEntity("gun_ui_ammo"));
        showBanner();
        scene.addEntity(UIEntities.createGameOverMenu());
    }

    public boolean isPaused() {
        return playing == false;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void pause() {
        showBanner();
        playing = false;
    }

    public void resume() {
        hideBanner();
        playing = true;
    }

    public float getDropChance() {
        if (wave > 1) {
            return 1f / (float) (Math.log((double) wave) / Math.log(2f));
        } else {
            return 0.5f;
        }
    }

    public LevelControl init() {
        hideBanner();
        return this;
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

    public void enemyKilled(int points) {
        this.points += points;
        this.enemiesLeft -= 1;
    }

    public int getPoints() {
        return points;
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

    public enum State {
        Wave,
        Done,
    }
}
