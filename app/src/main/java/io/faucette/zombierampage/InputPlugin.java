package io.faucette.zombierampage;


import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.faucette.math.Mathf;
import io.faucette.math.Vec2;
import io.faucette.scene_graph.Plugin;



public class InputPlugin extends Plugin {
    private List<Touch> touches;
    private float width;
    private float height;


    public class Touch {
        private int id;
        private boolean active;
        public Vec2 delta;
        public Vec2 position;

        public Touch(int id) {
            this.id = id;
            active = false;
            delta = new Vec2();
            position = new Vec2();
        }

        public int getId() {
            return id;
        }
    }


    public InputPlugin() {
        super();

        touches = new ArrayList<>();
        touches.add(new Touch(0));
        touches.add(new Touch(1));

        width = 960f;
        height = 640f;
    }

    public float getWidth() {
        return width;
    }
    public float getHeight() {
        return height;
    }

    public void setDimensions(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public Iterable<Touch> getTouches() {
        List<Touch> activeTouches = new ArrayList<>();

        synchronized (touches) {
            for (Touch touch : touches) {
                if (touch.active) {
                    activeTouches.add(touch);
                }
            }
        }

        return activeTouches;
    }

    public Touch getTouch(int index) {
        synchronized (touches){
            if (index < touches.size()) {
                Touch touch = touches.get(index);

                if (touch.active) {
                    return touch;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }
    public Touch getTouch() {
        return getTouch(0);
    }

    public boolean onTouchEvent(GameView gameView, MotionEvent e) {
        synchronized (touches) {
            int count = Mathf.clamp(e.getPointerCount(), 0, 2);

            for (int i = 0; i < count; i++) {
                int pointerIndex = i;

                switch (e.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN: {
                        Touch touch = touches.get(pointerIndex);
                        touch.active = true;
                        touch.position.x = e.getX(pointerIndex);
                        touch.position.y = e.getY(pointerIndex);
                        touch.delta.x = 0f;
                        touch.delta.y = 0f;
                        touches.add(touch);
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        Touch touch = touches.get(pointerIndex);
                        float x = touch.position.x;
                        float y = touch.position.y;
                        touch.position.x = e.getX(pointerIndex);
                        touch.position.y = e.getY(pointerIndex);
                        touch.delta.x = touch.position.x - x;
                        touch.delta.y = touch.position.y - y;
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        Touch touch = touches.get(pointerIndex);
                        touch.active = false;
                        break;
                    }
                }
            }
        }
        return true;
    }
}
