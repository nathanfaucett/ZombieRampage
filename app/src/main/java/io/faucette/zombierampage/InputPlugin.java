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
    private float actualWidth;
    private float actualHeight;
    private float scaleX = 1f;
    private float scaleY = 1f;

    public InputPlugin() {
        super();

        touches = new ArrayList<>();

        width = 960f;
        height = 640f;
        actualWidth = 960f;
        actualHeight = 640f;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void setDimensions(float width, float height, float actualWidth, float actualHeight) {
        this.width = width;
        this.height = height;
        this.actualWidth = actualWidth;
        this.actualHeight = actualHeight;

        scaleX = this.width / this.actualWidth;
        scaleY = this.height / this.actualHeight;
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
        touch.position.x = x * scaleX;
        touch.position.y = y * scaleY;
        touch.delta.x = 0f;
        touch.delta.y = 0f;
    }

    private void touchMove(int index, float ex, float ey) {
        if (index < touches.size()) {
            Touch touch = touches.get(index);

            if (touch != null) {
                float x = touch.position.x;
                float y = touch.position.y;
                touch.position.x = ex * scaleX;
                touch.position.y = ey * scaleY;
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

    public boolean onTouchEvent(MotionEvent e) {
        synchronized (touches) {
            int pointerIndex = e.getActionIndex();
            int pointerCount = e.getPointerCount();
            int actionMasked = e.getActionMasked();

            if (pointerCount > 0) {
                for (int i = 0; i < pointerCount; i++) {
                    touchMove(i, e.getX(i), e.getY(i));
                }

                switch (actionMasked) {
                    case MotionEvent.ACTION_POINTER_DOWN: {
                        Touch touch = new Touch(pointerIndex);
                        touchDown(touch, e.getX(pointerIndex), e.getY(pointerIndex));
                        touches.add(touch);
                        break;
                    }
                    case MotionEvent.ACTION_POINTER_UP: {
                        touchEnd(pointerIndex);
                        break;
                    }
                    case MotionEvent.ACTION_DOWN: {
                        Touch touch = new Touch(pointerIndex);
                        touchDown(touch, e.getX(pointerIndex), e.getY(pointerIndex));
                        touches.add(touch);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        touchEnd(pointerIndex);
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL: {
                        touchCancel();
                        break;
                    }
                }
            } else {
                touchCancel();
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
