package io.faucette.zombierampage;


import io.faucette.scene_graph.ComponentManager;


public class PauseableManager extends ComponentManager {
    public LevelControl getLevelControl() {
        return scene.getEntity("level_control").getComponent(LevelControl.class);
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
