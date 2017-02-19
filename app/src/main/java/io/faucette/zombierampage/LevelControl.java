package io.faucette.zombierampage;


import java.util.List;

import io.faucette.scene_graph.Component;
import io.faucette.scene_graph.Scene;


public class LevelControl extends Component {
    private int origTotal = 16;
    private int total = 16;
    private float currentTime = 0f;
    private float rate = 4f;
    private boolean playing;


    public LevelControl() {
        super();
        playing = true;
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

        if (total != 0 && currentTime > rate) {
            total -= 1;
            currentTime = 0f;
            spawn();
        }

        if (total == 0) {
            reset();
        }

        return this;
    }

    private void reset() {
        total = origTotal + (int) (origTotal * 0.5f);
        origTotal = total;
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
