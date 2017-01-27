package io.faucette.zombierampage;

import android.service.quicksettings.Tile;

import java.util.HashMap;

import io.faucette.math.Vec2;
import io.faucette.scene_graph.Component;
import io.faucette.scene_graph.ComponentManager;
import io.faucette.scene_graph.Entity;
import io.faucette.transform_components.Transform2D;

/**
 * Created by nathan on 1/26/17.
 */

public class TileControlManager extends ComponentManager {
    private HashMap<String, TileControl> tileMap;
    private float size;

    public TileControlManager() {
        super();
        tileMap = new HashMap<>();
    }

    @Override
    public TileControlManager init() {
        return this;
    }

    @Override
    public TileControlManager update() {
        return this;
    }

    @Override
    public TileControlManager sort() {
        return this;
    }

    @Override
    public <T extends Component> TileControlManager addComponent(T component) {
        super.addComponent(component);

        TileControl tileControl = (TileControl) component;
        size = tileControl.getSize();
        tileMap.put(tileControl.getX() + ":" + tileControl.getY(), tileControl);

        return this;
    }

    public TileControl find(int x, int y) {
        String id = x + ":" + y;
        return tileMap.get(id);
    }
    public TileControl find(float x, float y) {
        int ix = (int) Math.round(x / size);
        int iy = (int) Math.round(y / size);
        String id = ix + ":" + iy;
        return tileMap.get(id);
    }
    public TileControl find(Vec2 position) {
        return find(position.x, position.y);
    }
    public boolean hasTile(float x, float y) {
        return find(x, y) != null;
    }
    public boolean hasTile(Vec2 position) {
        return hasTile(position.x, position.y);
    }
}
