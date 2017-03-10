package io.faucette.zombierampage;


import io.faucette.ui_component.UI;

public class PointsControl extends Pauseable {
    private int points = 0;


    public PointsControl() {
    }

    @Override
    public PointsControl update() {
        if (isPaused()) {
            return this;
        }
        int pts = getLevelControl().getPoints();

        if (pts != points) {
            points = pts;
            entity.getComponent(UI.class).setText(points + "");
        }

        return this;
    }
}
