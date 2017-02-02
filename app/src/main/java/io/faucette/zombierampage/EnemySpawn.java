package io.faucette.zombierampage;


import java.util.List;

import io.faucette.scene_graph.Component;
import io.faucette.scene_graph.Scene;


public class EnemySpawn extends Component {
    private int total = 16;
    private float currentTime = 0f;
    private float rate = 4f;


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

        int index = (int) (Math.random() * ((float) tiles.size()));
        TileControl tile = tiles.get(index);
        float size = tile.getSize();
        float smallSize = size * 0.9f;
        float x = ((tile.getX() * size) - (smallSize * 0.5f)) + ((float) Math.random() * smallSize);
        float y = ((tile.getY() * size) - (smallSize * 0.5f)) + ((float) Math.random() * smallSize);

        scene.addEntity(Entities.createRegEnemy(x, y));
    }
}
