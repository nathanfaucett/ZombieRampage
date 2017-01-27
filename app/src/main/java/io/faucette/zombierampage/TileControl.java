package io.faucette.zombierampage;

import io.faucette.scene_graph.Component;
import io.faucette.scene_graph.ComponentManager;

/**
 * Created by nathan on 1/26/17.
 */

public class TileControl extends Component {
    private LevelGenerator.Section section;
    private float size;


    public TileControl(LevelGenerator.Section section, float size) {
        this.section = section;
        this.size = size;
    }

    public int getX() {
        return section.getX();
    }
    public int getY() {
        return section.getY();
    }
    public float getSize() {
        return size;
    }

    @Override
    public Class<? extends ComponentManager> getComponentManagerClass() {
        return TileControlManager.class;
    }
    @Override
    public ComponentManager createComponentManager() {
        return new TileControlManager();
    }
}
