package io.faucette.zombierampage;


import io.faucette.scene_graph.Component;


public class MenuControl extends Component {
    private GLRenderer renderer;


    public MenuControl(GLRenderer renderer) {
        super();
        this.renderer = renderer;
    }

    public void loadGame() {
        renderer.game.loadGame();
    }
}
