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

    public void showLeaderBoard() {
        renderer.activityControl.showLeaderBoard();
    }

    public void signIn() {
        renderer.activityControl.signIn();
    }

    public void onSignIn(final MainActivity.SignInCallback callback) {
        renderer.activityControl.onSignIn(callback);
    }
}
