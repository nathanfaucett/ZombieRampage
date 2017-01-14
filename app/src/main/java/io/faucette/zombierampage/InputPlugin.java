package io.faucette.zombierampage;


import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.faucette.math.Vec2;
import io.faucette.scene_graph.Plugin;



public class InputPlugin extends Plugin {
    private List<Touch> touches;
    private float width;
    private float height;


    public class Touch {
        private int id;
        public Vec2 delta;
        public Vec2 position;

        public Touch(int id) {
            this.id = id;
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

    public synchronized Iterable<Touch> getTouches() {
        return new ArrayList<>(touches);
    }

    public synchronized Touch getTouch(int index) {
        if (index < touches.size()) {
            return touches.get(index);
        } else {
            return null;
        }
    }
    public Touch getTouch() {
        return getTouch(0);
    }

    public synchronized boolean hasTouch() {
        return touches.size() != 0;
    }

    private synchronized Touch findTouch(int id) {
        for (Touch touch: touches) {
            if (touch.id == id) {
                return touch;
            }
        }
        return null;
    }
    private synchronized void removeTouch(int id) {
        Iterator<Touch> it = touches.iterator();

        while (it.hasNext()) {
            Touch touch = it.next();

            if (touch.id == id) {
                it.remove();
            }
        }
    }

    public boolean onTouchEvent(GameView gameView, MotionEvent e) {
        int pointerIndex = e.getActionIndex();
        int pointerId = e.getPointerId(pointerIndex);
        int maskedAction = e.getActionMasked();

        switch (maskedAction) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
                Touch touch = new Touch(pointerId);
                touch.position.x = e.getX(pointerIndex);
                touch.position.y = e.getY(pointerIndex);
                touches.add(touch);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                Touch touch = findTouch(pointerId);
                if (touch != null) {
                    float x = touch.position.x;
                    float y = touch.position.y;
                    touch.position.x = e.getX(pointerIndex);
                    touch.position.y = e.getY(pointerIndex);
                    touch.delta.x = touch.position.x - x;
                    touch.delta.y = touch.position.y - y;
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL: {
                removeTouch(pointerId);
                break;
            }
        }

        return true;
    }
}
