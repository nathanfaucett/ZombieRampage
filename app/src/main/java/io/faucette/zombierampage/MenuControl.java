package io.faucette.zombierampage;


import io.faucette.scene_graph.Component;


public class MenuControl extends Component {
    private GLRenderer renderer;


    public MenuControl(GLRenderer renderer) {
        super();
        this.renderer = renderer;
    }

    @Override
    public MenuControl init() {
        showBanner();
        return this;
    }

    public void loadGame() {
        hideBanner();
        renderer.game.loadGame();
    }

    public void showBanner() {
        renderer.activityControl.showBanner();
    }

    public void hideBanner() {
        renderer.activityControl.hideBanner();
    }
}
