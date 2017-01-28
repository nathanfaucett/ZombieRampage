package io.faucette.zombierampage;


import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

import io.faucette.math.Vec2;
import io.faucette.scene_graph.Plugin;


public class InputPlugin extends Plugin {
    private static long TOUCH_ID = 0;
    private List<Touch> touches;
    private float width;
    private float height;

    public InputPlugin() {
        super();

        touches = new ArrayList<>();

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
                activeTouches.add(touch);
            }
        }

        return activeTouches;
    }

    public Touch getTouch(int index) {
        synchronized (touches) {
            if (index < touches.size()) {
                return touches.get(index);
            }
        }
        return null;
    }

    public Touch getTouch(long id) {
        synchronized (touches) {
            for (Touch touch : touches) {
                if (touch.getId() == id) {
                    return touch;
                }
            }
        }
        return null;
    }

    private void touchDown(Touch touch, float x, float y) {
        touch.position.x = x;
        touch.position.y = y;
        touch.delta.x = 0f;
        touch.delta.y = 0f;
    }

    private void touchMove(int index, float ex, float ey) {
        if (index < touches.size()) {
            Touch touch = touches.get(index);

            if (touch != null) {
                float x = touch.position.x;
                float y = touch.position.y;
                touch.position.x = ex;
                touch.position.y = ey;
                touch.delta.x = touch.position.x - x;
                touch.delta.y = touch.position.y - y;
            }
        }
    }

    private void touchEnd(int index) {
        if (index < touches.size()) {
            touches.remove(index);
        }
    }

    private void touchCancel() {
        touches.clear();
    }

    public boolean onTouchEvent(GameView gameView, MotionEvent e) {
        synchronized (touches) {
            int pointerIndex = e.getActionIndex();
            int pointerId = e.getPointerId(pointerIndex);
            int pointerCount = e.getPointerCount();

            for (int i = pointerCount - 1; i >= 0; i--) {
                switch (e.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (pointerId == 0) {
                            Touch touch = new Touch(0);
                            touchDown(touch, e.getX(0), e.getY(0));
                            touches.add(touch);
                        }
                        break;
                    }
                    case MotionEvent.ACTION_POINTER_DOWN: {
                        if (pointerId == i) {
                            Touch touch = new Touch(i);
                            touchDown(touch, e.getX(i), e.getY(i));
                            touches.add(touch);
                        }
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        touchMove(i, e.getX(i), e.getY(i));
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        touchEnd(0);
                        break;
                    }
                    case MotionEvent.ACTION_POINTER_UP: {
                        if (i != 0) {
                            touchEnd(i);
                        }
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL: {
                        touchCancel();
                        break;
                    }
                }
            }
        }
        return true;
    }

    public class Touch {
        public Vec2 delta;
        public Vec2 position;
        private int index;
        private long id;

        public Touch(int index) {
            this.index = index;
            id = TOUCH_ID++;
            delta = new Vec2();
            position = new Vec2();
        }

        public long getId() {
            return id;
        }

        @Override
        public String toString() {
            return "Touch { index: " + index + ", id: " + id + ", " + delta + ", " + position + "}";
        }
    }
}
