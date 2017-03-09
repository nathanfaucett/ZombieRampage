package io.faucette.zombierampage;


import io.faucette.math.Vec2;
import io.faucette.scene_graph.Component;
import io.faucette.transform_components.Transform2D;
import io.faucette.ui_component.UI;


public class UIControl extends Component {
    public Vec2 position;
    public Vec2 offset;
    public Anchor anchor;

    private float lastWidth;
    private float lastHeight;
    private float screenWidth;
    private float screenHeight;
    private boolean hover;
    private boolean lastHover;


    public UIControl() {

        super();

        position = new Vec2();
        offset = new Vec2();
        anchor = Anchor.None;
        hover = false;
        lastHover = false;
    }

    public UIControl setAnchor(Anchor anchor) {
        this.anchor = anchor;
        return this;
    }

    public UIControl setOffset(Vec2 offset) {
        this.offset = offset;
        return this;
    }

    private UIControl updatePosition(InputPlugin input) {
        if (anchor != Anchor.None) {
            UI ui = entity.getComponent(UI.class);

            float hw = lastWidth * 0.5f;
            float hh = lastHeight * 0.5f;

            switch (anchor) {
                case TopLeft:
                    position.x = hw;
                    position.y = hh;
                    break;
                case TopRight:
                    position.x = screenWidth - hw;
                    position.y = hh;
                    break;
                case BottomLeft:
                    position.x = hw;
                    position.y = screenHeight - hh;
                    break;
                case BottomRight:
                    position.x = screenWidth - hw;
                    position.y = screenHeight - hh;
                    break;
                case Center:
                    position.x = screenWidth * 0.5f;
                    position.y = screenHeight * 0.5f;
                    break;
                case CenterTop:
                    position.x = screenWidth * 0.5f;
                    position.y = hh;
                    break;
                case CenterRight:
                    position.x = screenWidth - hw;
                    position.y = screenHeight * 0.5f;
                    break;
                case CenterBottom:
                    position.x = screenWidth * 0.5f;
                    position.y = screenHeight - hh;
                    break;
                case CenterLeft:
                    position.x = hw;
                    position.y = screenHeight * 0.5f;
                    break;
            }

            position.add(offset);
            entity.getComponent(Transform2D.class)
                    .setPosition(position);
        }
        return this;
    }

    @Override
    public UIControl init() {
        InputPlugin input = entity.getScene().getPlugin(InputPlugin.class);
        updatePosition(input);
        return this;
    }

    @Override
    public UIControl update() {
        InputPlugin input = entity.getScene().getPlugin(InputPlugin.class);

        if (screenWidth != input.getWidth() || screenHeight != input.getHeight()) {
            screenWidth = input.getWidth();
            screenHeight = input.getHeight();
            updatePosition(input);
        }

        UI ui = entity.getComponent(UI.class);

        if (lastWidth != ui.getWidth() || lastHeight != ui.getHeight()) {
            lastWidth = ui.getWidth();
            lastHeight = ui.getHeight();
            updatePosition(input);
        }

        hover = false;
        for (InputPlugin.Touch touch : input.getTouches()) {
            if (ui.contains(touch.position)) {
                entity.getComponent(UI.class).setY(0f);
                hover = true;
            }
        }
        if (lastHover == true && hover == false) {
            entity.getComponent(UI.class).setY(0.5f);
            emit("touch");
        }
        lastHover = hover;

        return this;
    }

    public enum Anchor {
        TopLeft,
        TopRight,
        BottomLeft,
        BottomRight,

        Center,

        CenterTop,
        CenterRight,
        CenterBottom,
        CenterLeft,

        None,
    }
}
