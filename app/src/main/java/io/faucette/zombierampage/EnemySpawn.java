package io.faucette.zombierampage;


import java.util.List;
import java.util.Random;

import io.faucette.scene_graph.Component;
import io.faucette.scene_graph.Scene;


public class EnemySpawn extends Component {
    private static Random random = new Random();

    private int total = 16;
    private float currentTime = 0f;
    private float rate = 5f;


    public EnemySpawn() {
        super();
    }

    public EnemySpawn setTotal(int total) {
        this.total = total;
        currentTime = 0f;
        return this;
    }

    public EnemySpawn update() {
        currentTime += entity.getScene().getTime().getDelta();

        if (total != 0 && currentTime > rate) {
            setTotal(total - 1);
            spawn();
        }

        return this;
    }

    private void spawn() {
        Scene scene = entity.getScene();

        TileControlManager tileControlManager = scene.getComponentManager(TileControlManager.class);
        List<TileControl> tiles = tileControlManager.getTiles();

        int index = (int) (random.nextFloat() * ((float) tiles.size()));
        TileControl tile = tiles.get(index);
        float size = tile.getSize();
        float smallSize = size * 0.75f;
        float x = ((tile.getX() * size) - (smallSize * 0.5f)) + (random.nextFloat() * smallSize);
        float y = ((tile.getY() * size) - (smallSize * 0.5f)) + (random.nextFloat() * smallSize);

        scene.addEntity(Entities.createEnemy(x, y));
    }
}
