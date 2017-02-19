package io.faucette.zombierampage;


import io.faucette.scene_graph.ComponentManager;


public class PauseableManager extends ComponentManager {
    public boolean isPaused() {
        return scene.getEntity("level_control").getComponent(LevelControl.class).isPaused();
    }
    public boolean isPlaying() {
        return scene.getEntity("level_control").getComponent(LevelControl.class).isPlaying();
    }
    public void pause() {
        scene.getEntity("level_control").getComponent(LevelControl.class).pause();
    }
    public void resume() {
        scene.getEntity("level_control").getComponent(LevelControl.class).resume();
    }
}
