package io.faucette.zombierampage;


import io.faucette.scene_graph.Component;


public class Pauseable extends Component {
    public LevelControl getLevelControl() {
        return entity.getScene().getEntity("level_control").getComponent(LevelControl.class);
    }
    public boolean isPaused() {
        return getLevelControl().isPaused();
    }
    public boolean isPlaying() {
        return getLevelControl().isPlaying();
    }
    public void pause() {
        getLevelControl().pause();
    }
    public void resume() {
        getLevelControl().resume();
    }
}
