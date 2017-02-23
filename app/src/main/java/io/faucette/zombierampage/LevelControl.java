package io.faucette.zombierampage;


import java.util.List;

import io.faucette.scene_graph.Component;
import io.faucette.scene_graph.Scene;


public class LevelControl extends Component {
    private GLRenderer renderer;

    private int enemiesOrigTotal = 16;
    private int enemiesTotal = 16;
    private int enemiesLeft = 16;

    private float currentTime = 0f;
    private float rate = 4f;
    private boolean playing;


    public LevelControl(GLRenderer renderer) {
        super();
        this.renderer = renderer;
        playing = true;
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

        currentTime += entity.getScene().getTime().getDelta();

        if (enemiesTotal != 0 && currentTime > rate) {
            enemiesTotal -= 1;
            currentTime = 0f;
            spawn();
        }

        if (enemiesTotal == 0 && enemiesLeft == 0) {
            reset();
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

        scene.addEntity(Entities.createRegEnemy(x, y));
    }
}
