package io.faucette.zombierampage;


import io.faucette.scene_graph.Component;


public class Pauseable extends Component {
    public boolean isPaused() {
        return entity.getScene().getEntity("level_control").getComponent(LevelControl.class).isPaused();
    }
    public boolean isPlaying() {
        return entity.getScene().getEntity("level_control").getComponent(LevelControl.class).isPlaying();
    }
    public void pause() {
        entity.getScene().getEntity("level_control").getComponent(LevelControl.class).pause();
    }
    public void resume() {
        entity.getScene().getEntity("level_control").getComponent(LevelControl.class).resume();
    }
}
