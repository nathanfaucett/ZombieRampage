package io.faucette.zombierampage;


import java.util.ArrayList;
import java.util.List;

import io.faucette.math.Vec2;
import io.faucette.scene_graph.ComponentManager;


public class RigidBodyManager extends ComponentManager{
    private List<Contact> contacts;


    public static class Contact {
        public float depth;
        public Vec2 normalA;
        public Vec2 normalB;
        public RigidBody bodyA;
        public RigidBody bodyB;


        public Contact(RigidBody bodyA, RigidBody bodyB) {
            this.depth = 0f;
            this.normalA = new Vec2();
            this.normalB = new Vec2();
            this.bodyA = bodyA;
            this.bodyB = bodyB;
        }
    }


    public RigidBodyManager() {
        super();
        contacts = new ArrayList<>();
    }

    @Override
    public RigidBodyManager update() {

        super.update();

        contacts.clear();

        getContacts();
        resolveContacts();

        return this;
    }

    private void resolveContacts() {
        for (Contact contact: contacts) {
            RigidBody a = contact.bodyA;
            RigidBody b = contact.bodyB;

            if (a.getType() == RigidBody.Type.Dynamic) {
                resolveBody(a, contact.normalB, contact.depth);
            }
            if (b.getType() == RigidBody.Type.Dynamic) {
                resolveBody(b, contact.normalA, contact.depth);
            }
        }
    }

    private void resolveBody(RigidBody body, Vec2 normal, float depth) {
        body.velocity.x += normal.x * depth;
        body.velocity.y += normal.y * depth;
    }

    private void getContacts() {
        for (int i = 0, il = components.size(); i < il; i++) {
            for (int j = 0; j != i; j++) {
                RigidBody a = (RigidBody) components.get(i);
                RigidBody b = (RigidBody) components.get(j);

                if (a.getType() != RigidBody.Type.Dynamic && b.getType() != RigidBody.Type.Dynamic) {
                    continue;
                }

                if (a.getAABB().intersects(b.getAABB())) {
                    List<RigidBody.Shape> shapesi = a.getShapes();
                    List<RigidBody.Shape> shapesj = b.getShapes();

                    for (RigidBody.Shape si : shapesi) {
                        for (RigidBody.Shape sj : shapesj) {
                            if (si.aabb.intersects(sj.aabb)) {
                                Contact contact = new Contact(si.getBody(), sj.getBody());
                                si.aabb.overlap(contact.normalA, sj.aabb);
                                contact.depth = contact.normalA.normalize();
                                Vec2.inverse(contact.normalB, contact.normalA);
                                contacts.add(contact);
                            }
                        }
                    }
                }
            }
        }
    }
}