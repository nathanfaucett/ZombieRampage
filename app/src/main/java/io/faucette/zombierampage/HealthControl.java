package io.faucette.zombierampage;

import io.faucette.math.Vec2;
import io.faucette.scene_graph.Component;
import io.faucette.scene_graph.Entity;
import io.faucette.transform_components.Transform2D;
import io.faucette.ui_component.UI;

/**
 * Created by nathan on 2/10/17.
 */
public class HealthControl extends Component {
    private int hearts;
    private float screenWidth;
    private float screenHeight;


    public HealthControl(int hearts) {
        super();

        this.hearts = hearts;
    }

    public HealthControl updateHearts(int health) {
        for (Entity child : entity.getChildren()) {
            UI ui = child.getComponent(UI.class);
            int count = 4;

            if (health < 0) {
                count = 0;
            } else if (health < 4) {
                count = health;
            }

            setHeart(ui, count);
            health -= 4;
        }

        return this;
    }

    private void setHeart(UI ui, int count) {
        switch (count) {
            case 1:
                ui.setVisible(true);
                ui.setImage(R.drawable.heart_1_4);
                break;
            case 2:
                ui.setVisible(true);
                ui.setImage(R.drawable.heart_2_4);
                break;
            case 3:
                ui.setVisible(true);
                ui.setImage(R.drawable.heart_3_4);
                break;
            case 4:
                ui.setVisible(true);
                ui.setImage(R.drawable.heart_4_4);
                break;
            default:
                ui.setVisible(false);
                break;
        }
    }

    private HealthControl updatePosition(InputPlugin input) {
        float heartSize = 48f;
        float width = hearts * heartSize;
        float height = heartSize;

        entity.getComponent(Transform2D.class)
                .setPosition(new Vec2(input.getWidth() * 0.5f, height * 0.5f));

        float startX = (-width * 0.5f) + (heartSize * 0.5f);

        for (Entity child : entity.getChildren()) {
            child.getComponent(Transform2D.class)
                    .setPosition(new Vec2(startX, 0f));
            startX += heartSize;
        }

        return this;
    }

    @Override
    public HealthControl update() {
        InputPlugin input = entity.getScene().getPlugin(InputPlugin.class);

        if (screenWidth != input.getWidth() || screenHeight != input.getHeight()) {
            screenWidth = input.getWidth();
            screenHeight = input.getHeight();
            updatePosition(input);
        }
        return this;
    }
}
