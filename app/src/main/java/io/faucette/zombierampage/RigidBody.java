package io.faucette.zombierampage;


import java.util.ArrayList;
import java.util.List;

import io.faucette.math.AABB2;
import io.faucette.math.Vec2;
import io.faucette.scene_graph.ComponentManager;
import io.faucette.scene_graph.Entity;
import io.faucette.scene_graph.Scene;
import io.faucette.transform_components.Transform2D;


public class RigidBody extends Pauseable {
    protected float damping;
    protected Vec2 velocity;
    private Type type;
    private AABB2 aabb;
    private List<Shape> shapes;
    private List<OnCollision> onCollisions;
    private boolean disabled;

    public RigidBody(Type t) {
        super();
        damping = 0.2f;
        velocity = new Vec2();
        type = t;
        aabb = new AABB2();
        shapes = new ArrayList<>();
        onCollisions = new ArrayList<>();
        disabled = false;
    }


    public RigidBody() {
        this(Type.Static);
    }

    public RigidBody addOnCollision(OnCollision onCollision) {
        onCollisions.add(onCollision);
        return this;
    }

    public RigidBody removeOnCollision(OnCollision onCollision) {
        onCollisions.remove(onCollision);
        return this;
    }

    public RigidBody emitOnCollision(RigidBody other) {
        for (OnCollision onCollision : onCollisions) {
            onCollision.onCollision(this, other);
        }
        return this;
    }

    public RigidBody setDamping(float damping) {
        this.damping = damping;
        return this;
    }

    public RigidBody addShape(Shape shape) {
        shape.body = this;
        shapes.add(shape);
        return this;
    }

    public Type getType() {
        return type;
    }

    public RigidBody setType(Type type) {
        this.type = type;
        return this;
    }

    public RigidBody addVelocity(Vec2 velocity) {
        this.velocity.add(velocity);
        return this;
    }

    public AABB2 getAABB() {
        return aabb;
    }

    public List<Shape> getShapes() {
        return shapes;
    }

    public RigidBody setShapes(Iterable<Shape> s) {
        for (Shape shape : s) {
            shape.body = this;
            shapes.add(shape);
        }
        return this;
    }

    @Override
    public RigidBody update() {
        if (this.isPaused()) {
            return this;
        }

        Transform2D transform2D = entity.getComponent(Transform2D.class);
        Vec2 position = transform2D.getLocalPosition();

        for (Shape shape : shapes) {
            shape.update(position);
            aabb.union(shape.aabb);
        }

        if (type != Type.Static) {
            Entity entity = getEntity();
            Scene scene = entity.getScene();
            float delta = (float) scene.getTime().getDelta();

            aabb.clear();

            position.x += velocity.x * delta;
            position.y += velocity.y * delta;

            for (Shape shape : shapes) {
                shape.update(position);
                aabb.union(shape.aabb);
            }

            transform2D.setNeedsUpdate();

            velocity.smul(1f - damping);
        }

        return this;
    }

    @Override
    public Class<? extends ComponentManager> getComponentManagerClass() {
        return RigidBodyManager.class;
    }

    @Override
    public ComponentManager createComponentManager() {
        return new RigidBodyManager();
    }

    public boolean isDisabled() {
        return disabled;
    }

    public RigidBody setDisabled(boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    public enum Type {
        Static,
        Dynamic,
        Kinematic
    }

    public interface OnCollision {
        void onCollision(RigidBody self, RigidBody other);
    }

    public static class Shape {
        public AABB2 aabb;
        public AABB2 localAABB;
        private RigidBody body;
        private boolean isTrigger;

        public Shape(float w, float h) {
            aabb = new AABB2();
            localAABB = new AABB2();
            localAABB.min.set(-w / 2, -h / 2);
            localAABB.max.set(w / 2, h / 2);
            isTrigger = false;
        }

        public Shape(Vec2 position, float w, float h) {
            aabb = new AABB2();
            localAABB = new AABB2();
            localAABB.fromCenterSize(position, new Vec2(w, h));
            isTrigger = false;
        }

        public boolean getIsTrigger() {
            return isTrigger;
        }

        public Shape setIsTrigger(boolean isTrigger) {
            this.isTrigger = isTrigger;
            return this;
        }

        public Shape update(Vec2 position) {
            aabb.copy(localAABB);
            aabb.min.add(position);
            aabb.max.add(position);
            return this;
        }

        public RigidBody getBody() {
            return body;
        }
    }
}
