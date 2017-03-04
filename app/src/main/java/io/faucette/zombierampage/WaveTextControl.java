package io.faucette.zombierampage;


import io.faucette.ui_component.UI;

public class WaveTextControl extends Pauseable {
    private static float TIME = 2f;
    private int state;

    private float timeIn;
    private float timeOut;
    private float time;

    private float timeInTotal;
    private float timeOutTotal;

    public WaveTextControl(float timeIn, float timeOut) {
        this.state = 0;

        this.timeIn = timeIn;
        this.timeInTotal = timeIn;
        this.timeOut = timeOut;
        this.timeOutTotal = timeOut;
        this.time = TIME;
    }

    @Override
    public WaveTextControl update() {
        if (isPaused()) {
            return this;
        }

        float dt = (float) entity.getScene().getTime().getDelta();
        UI ui = entity.getComponent(UI.class);

        switch (state) {
            case 0: // fade in
                ui.setAlpha(1 - (timeIn / timeInTotal));
                timeIn -= dt;

                if (this.timeIn <= 0) {
                    state = 1;
                }
                break;
            case 1: // stay
                ui.setAlpha(1f);
                time -= dt;

                if (time <= 0) {
                    state = 2;
                }
                break;
            case 2: // fade out
                ui.setAlpha(timeOut / timeOutTotal);
                timeOut -= dt;

                if (timeOut <= 0) {
                    state = 3;
                }
                break;
            case 3: // delete
                entity.getScene().removeEntity(entity);
                break;
        }
        return this;
    }
}
