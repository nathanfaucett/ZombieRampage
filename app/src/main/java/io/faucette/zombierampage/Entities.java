package io.faucette.zombierampage;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.faucette.camera_component.Camera;
import io.faucette.math.Vec2;
import io.faucette.math.Vec4;
import io.faucette.scene_graph.Entity;
import io.faucette.sprite_component.Sprite;
import io.faucette.transform_components.Transform2D;
import io.faucette.ui_component.UI;


public class Entities {
    public static HashMap<String, float[][]> animations = new HashMap<>();
    public static HashMap<String, float[][]> enemyAnimations = new HashMap<>();
    public static HashMap<String, float[][]> fireAnimations = new HashMap<>();

    public static int BG_LAYER = 0;
    public static int LAYER = 1;


    public static Entity createPlayer(int health) {
        return new Entity("player")
                .setTag("player")
                .addComponent(new Transform2D())
                .addComponent(new StatusControl(health, 0, 0.2f, 0f)
                        .setAllowHitWhileHit(false)
                        .setDropItem(false))
                .addComponent(new PlayerControl())
                .addComponent(new AnimationControl())
                .addComponent(new RigidBody(RigidBody.Type.Dynamic)
                        .addShape(new RigidBody.Shape(new Vec2(0f, -0.0625f), 0.125f, 0.125f)))
                .addComponent(new SpriteAnimation(animations, "down"))
                .addComponent(new Sprite()
                        .setLayer(LAYER)
                        .setWidth(0.25f)
                        .setHeight(0.25f)
                        .setImage(R.drawable.player));
    }

    public static Entity createBullet(Vec2 position, Vec2 direction, float speed, final int pp) {
        return new Entity()
                .setTag("bullet")
                .addComponent(new Transform2D()
                        .setPosition(position)
                        .setRotation((float) (Math.atan2(direction.y, direction.x) - (Math.PI * 0.5))))
                .addComponent(new RigidBody(RigidBody.Type.Kinematic)
                        .setDamping(0f)
                        .addVelocity(new Vec2(direction.x * speed, direction.y * speed))
                        .addOnCollision(new RigidBody.OnCollision() {
                            @Override
                            public void onCollision(RigidBody self, RigidBody other) {
                                Entity otherEntity = other.getEntity();

                                if (otherEntity.compareTag("enemy")) {
                                    otherEntity.getComponent(StatusControl.class).takeDamage(pp);
                                    Entity entity = self.getEntity();
                                    entity.getScene().removeEntity(entity);
                                } else if (otherEntity.compareTag("tile")) {
                                    Entity entity = self.getEntity();
                                    entity.getScene().removeEntity(entity);
                                }
                            }
                        })
                        .addShape(new RigidBody.Shape(0.046875f, 0.046875f)
                                .setIsTrigger(true)))
                .addComponent(new Sprite()
                        .setLayer(LAYER)
                        .setWidth(0.046875f)
                        .setHeight(0.234375f)
                        .setImage(R.drawable.bullet));
    }

    public static Entity createFlamethrowerBullet(Vec2 position, Vec2 direction, float speed, final int pp) {
        return new Entity()
                .setTag("bullet")
                .addComponent(new Transform2D()
                        .setPosition(position)
                        .setRotation((float) (Math.atan2(direction.y, direction.x) - (Math.PI * 0.5))))
                .addComponent(new RigidBody(RigidBody.Type.Kinematic)
                        .setDamping(0f)
                        .addVelocity(new Vec2(direction.x * speed, direction.y * speed))
                        .addOnCollision(new RigidBody.OnCollision() {
                            @Override
                            public void onCollision(RigidBody self, RigidBody other) {
                                Entity otherEntity = other.getEntity();

                                if (otherEntity.compareTag("enemy")) {
                                    otherEntity.getComponent(StatusControl.class).takeDamage(pp);
                                } else if (otherEntity.compareTag("tile")) {
                                    Entity entity = self.getEntity();
                                    entity.getScene().removeEntity(entity);
                                }
                            }
                        })
                        .addShape(new RigidBody.Shape(0.046875f, 0.046875f)
                                .setIsTrigger(true)))
                .addComponent(new FlamethrowerBulletControl((float) (0.5 + Math.random())))
                .addComponent(new SpriteAnimation(fireAnimations, "burn"))
                .addComponent(new Sprite()
                        .setLayer(LAYER)
                        .setWidth(0.234375f)
                        .setHeight(0.234375f)
                        .setImage(R.drawable.flamethrower_bullet));
    }

    public static Entity createHealth(Vec2 position) {
        return new Entity()
                .setTag("health")
                .addComponent(new Transform2D()
                        .setPosition(position))
                .addComponent(new RigidBody(RigidBody.Type.Kinematic)
                        .addOnCollision(new RigidBody.OnCollision() {
                            @Override
                            public void onCollision(RigidBody self, RigidBody other) {
                                Entity otherEntity = other.getEntity();

                                if (otherEntity.compareTag("player")) {
                                    otherEntity.getComponent(PlayerControl.class).getHealth();
                                    Entity entity = self.getEntity();
                                    entity.getScene().removeEntity(entity);
                                }
                            }
                        })
                        .addShape(new RigidBody.Shape(0.0625f, 0.0625f)
                                .setIsTrigger(true)))
                .addComponent(new Sprite()
                        .setLayer(LAYER)
                        .setWidth(0.125f)
                        .setHeight(0.125f)
                        .setImage(R.drawable.health));
    }

    public static Entity createAmmo(final PlayerControl.GunType type, Vec2 position) {
        return new Entity()
                .setTag("ammo")
                .addComponent(new Transform2D()
                        .setPosition(position))
                .addComponent(new RigidBody(RigidBody.Type.Kinematic)
                        .addOnCollision(new RigidBody.OnCollision() {
                            @Override
                            public void onCollision(RigidBody self, RigidBody other) {
                                Entity otherEntity = other.getEntity();

                                if (otherEntity.compareTag("player")) {
                                    otherEntity.getComponent(PlayerControl.class)
                                            .getAmmo(type);
                                    Entity entity = self.getEntity();
                                    entity.getScene().removeEntity(entity);
                                }
                            }
                        })
                        .addShape(new RigidBody.Shape(0.125f, 0.0625f)
                                .setIsTrigger(true)))
                .addComponent(new Sprite()
                        .setLayer(LAYER)
                        .setWidth(getAmmoWidth(type))
                        .setHeight(getAmmoHeight(type))
                        .setImage(getAmmoImage(type)));
    }

    private static float getAmmoWidth(PlayerControl.GunType type) {
        switch (type) {
            case Shotgun:
                return 0.125f;
            case Uzi:
                return 0.15625f;
            case FlameThrower:
                return 0.140625f;
        }
        return -1;
    }

    private static float getAmmoHeight(PlayerControl.GunType type) {
        switch (type) {
            case Shotgun:
                return 0.25f;
            case Uzi:
                return 0.171875f;
            case FlameThrower:
                return 0.203125f;
        }
        return -1;
    }

    private static Integer getAmmoImage(PlayerControl.GunType type) {
        switch (type) {
            case Shotgun:
                return R.drawable.shotgun_ammo;
            case Uzi:
                return R.drawable.uzi_ammo;
            case FlameThrower:
                return R.drawable.flamethrower_ammo;
        }
        return -1;
    }

    public static Entity createRegEnemy(float x, float y) {
        return new Entity()
                .setTag("enemy")
                .addComponent(new Transform2D()
                        .setPosition(new Vec2(x, y)))
                .addComponent(new StatusControl(4, 1, 0.1f))
                .addComponent(new EnemyControl())
                .addComponent(new AnimationControl())
                .addComponent(new RigidBody(RigidBody.Type.Dynamic)
                        .addOnCollision(new RigidBody.OnCollision() {
                            @Override
                            public void onCollision(RigidBody self, RigidBody other) {
                                if (other.getEntity().compareTag("player")) {
                                    self.getEntity().getComponent(StatusControl.class)
                                            .attack(other.getEntity().getComponent(StatusControl.class));
                                }
                            }
                        })
                        .addShape(new RigidBody.Shape(new Vec2(0f, -0.0625f), 0.125f, 0.125f))
                        .addShape(new RigidBody.Shape(0.125f, 0.25f).setIsTrigger(true)))
                .addComponent(new SpriteAnimation(enemyAnimations, "down"))
                .addComponent(new Sprite()
                        .setLayer(LAYER)
                        .setWidth(0.25f)
                        .setHeight(0.25f)
                        .setImage(R.drawable.reg_enemy));
    }

    public static Entity createCamera() {
        return new Entity("camera")
                .addComponent(new Camera()
                        .setOrthographicSize(1f)
                        .setBackground(new Vec4(0f, 0f, 0f, 1f)))
                .addComponent(new Transform2D())
                .addComponent(new CameraControl());
    }

    public static Entity createTile(LevelGenerator.Section section, float size) {
        LevelGenerator.Type type = section.getType();
        float x = section.getX();
        float y = section.getY();

        return new Entity()
                .setTag("tile")
                .addComponent(new TileControl(section, size))
                .addComponent(new RigidBody()
                        .setShapes(Entities.getShapes(type, size)))
                .addComponent(new Transform2D()
                        .setPosition(new Vec2(x * size, y * size)))
                .addComponent(new Sprite()
                        .setLayer(BG_LAYER)
                        .setWidth(size)
                        .setHeight(size)
                        .setImage(getImage(type)));
    }

    private static List<RigidBody.Shape> getShapes(LevelGenerator.Type type, float size) {
        List<RigidBody.Shape> shapes = new ArrayList<>();

        float w = 0.03125f;
        float h = 0.125f;

        float max = size * 0.5f;
        float maxx = max - w;
        float minx = -maxx + w;
        float maxy = max - h;
        float miny = -max + w;

        size += w * 2f;

        switch (type) {
            case TopEnd:
                shapes.add(new RigidBody.Shape(new Vec2(0f, maxy), size, h));
                shapes.add(new RigidBody.Shape(new Vec2(minx, 0), w, size));
                shapes.add(new RigidBody.Shape(new Vec2(maxx, 0), w, size));
                break;
            case RightEnd:
                shapes.add(new RigidBody.Shape(new Vec2(maxx, 0), w, size));
                shapes.add(new RigidBody.Shape(new Vec2(0f, miny), size, w));
                shapes.add(new RigidBody.Shape(new Vec2(0f, maxy), size, h));
                break;
            case BottomEnd:
                shapes.add(new RigidBody.Shape(new Vec2(0f, miny), size, w));
                shapes.add(new RigidBody.Shape(new Vec2(minx, 0), w, size));
                shapes.add(new RigidBody.Shape(new Vec2(maxx, 0), w, size));
                break;
            case LeftEnd:
                shapes.add(new RigidBody.Shape(new Vec2(minx, 0), w, size));
                shapes.add(new RigidBody.Shape(new Vec2(0f, miny), size, w));
                shapes.add(new RigidBody.Shape(new Vec2(0f, maxy), size, h));
                break;

            case TopRightTurn:
                shapes.add(new RigidBody.Shape(new Vec2(minx, 0f), w, size));
                shapes.add(new RigidBody.Shape(new Vec2(0f, miny), size, w));
                shapes.add(new RigidBody.Shape(new Vec2(maxx, maxy), w, h));
                break;
            case TopLeftTurn:
                shapes.add(new RigidBody.Shape(new Vec2(maxx, 0f), w, size));
                shapes.add(new RigidBody.Shape(new Vec2(0f, miny), size, w));
                shapes.add(new RigidBody.Shape(new Vec2(minx, maxy), w, h));
                break;
            case BottomRightTurn:
                shapes.add(new RigidBody.Shape(new Vec2(minx, 0f), w, size));
                shapes.add(new RigidBody.Shape(new Vec2(0f, maxy), size, h));
                shapes.add(new RigidBody.Shape(new Vec2(maxx, miny), w, w));
                break;
            case BottomLeftTurn:
                shapes.add(new RigidBody.Shape(new Vec2(maxx, 0f), w, size));
                shapes.add(new RigidBody.Shape(new Vec2(0f, maxy), size, h));
                shapes.add(new RigidBody.Shape(new Vec2(minx, miny), w, w));
                break;

            case VerticalLeftThreeWay:
                shapes.add(new RigidBody.Shape(new Vec2(maxx, 0f), w, size));
                shapes.add(new RigidBody.Shape(new Vec2(minx, maxy), w, h));
                shapes.add(new RigidBody.Shape(new Vec2(minx, miny), w, w));
                break;
            case VerticalRightThreeWay:
                shapes.add(new RigidBody.Shape(new Vec2(minx, 0f), w, size));
                shapes.add(new RigidBody.Shape(new Vec2(maxx, maxy), w, h));
                shapes.add(new RigidBody.Shape(new Vec2(maxx, miny), w, w));
                break;
            case HorizontalTopThreeWay:
                shapes.add(new RigidBody.Shape(new Vec2(0f, miny), size, w));
                shapes.add(new RigidBody.Shape(new Vec2(maxx, maxy), w, h));
                shapes.add(new RigidBody.Shape(new Vec2(minx, maxy), w, h));
                break;
            case HorizontalBottomThreeWay:
                shapes.add(new RigidBody.Shape(new Vec2(0f, maxy), size, h));
                shapes.add(new RigidBody.Shape(new Vec2(maxx, miny), w, w));
                shapes.add(new RigidBody.Shape(new Vec2(minx, miny), w, w));
                break;

            case Vertical:
                shapes.add(new RigidBody.Shape(new Vec2(minx, 0), w, size));
                shapes.add(new RigidBody.Shape(new Vec2(maxx, 0), w, size));
                break;
            case Horizontal:
                shapes.add(new RigidBody.Shape(new Vec2(0f, miny), size, w));
                shapes.add(new RigidBody.Shape(new Vec2(0f, maxy), size, h));
                break;

            case FourWay:
                shapes.add(new RigidBody.Shape(new Vec2(maxx, maxy), w, h));
                shapes.add(new RigidBody.Shape(new Vec2(minx, maxy), w, h));
                shapes.add(new RigidBody.Shape(new Vec2(maxx, miny), w, w));
                shapes.add(new RigidBody.Shape(new Vec2(minx, miny), w, w));
                break;
        }

        return shapes;
    }

    public static Integer getImage(LevelGenerator.Type type) {
        switch (type) {
            case TopEnd:
                return R.drawable.top_end;
            case RightEnd:
                return R.drawable.right_end;
            case BottomEnd:
                return R.drawable.bottom_end;
            case LeftEnd:
                return R.drawable.left_end;

            case TopRightTurn:
                return R.drawable.top_right_turn;
            case TopLeftTurn:
                return R.drawable.top_left_turn;
            case BottomRightTurn:
                return R.drawable.bottom_right_turn;
            case BottomLeftTurn:
                return R.drawable.bottom_left_turn;

            case VerticalLeftThreeWay:
                return R.drawable.vertical_left_three_way;
            case VerticalRightThreeWay:
                return R.drawable.vertical_right_three_way;
            case HorizontalTopThreeWay:
                return R.drawable.horizontal_top_three_way;
            case HorizontalBottomThreeWay:
                return R.drawable.horizontal_bottom_three_way;

            case Vertical:
                return R.drawable.vertical;
            case Horizontal:
                return R.drawable.horizontal;

            case FourWay:
                return R.drawable.four_way;
        }
        return 0;
    }

    public static void initAnimations() {
        putBasicAnimations(animations, 1f / 20f);
        putBasicAnimations(enemyAnimations, 1f / 23f);
        putBirthAnimations(enemyAnimations, 1f / 23f);
        putFireAnimations(fireAnimations, 1f / 8f);
    }

    private static void putFireAnimations(HashMap<String, float[][]> animations, float size) {
        fireAnimations.put("burn", new float[][]{
                {size * 0, 0f, size, 1f, size},
                {size * 1, 0f, size, 1f, size},
                {size * 2, 0f, size, 1f, size},
                {size * 3, 0f, size, 1f, size},
                {size * 4, 0f, size, 1f, size},
                {size * 5, 0f, size, 1f, size},
                {size * 6, 0f, size, 1f, size},
                {size * 7, 0f, size, 1f, 0f}
        });
    }

    private static void putBirthAnimations(HashMap<String, float[][]> animations, float size) {
        animations.put("birth", new float[][]{
                {size * 20, 0f, size, 1f, 1.5f},
                {size * 21, 0f, size, 1f, 0.5f},
                {size * 22, 0f, size, 1f, 0f},
        });
    }

    private static void putBasicAnimations(HashMap<String, float[][]> animations, float size) {
        animations.put("down", new float[][]{
                {size * 0, 0f, size, 1f, 1f},
                {size * 1, 0f, size, 1f, 1f},
        });
        animations.put("down_right", new float[][]{
                {size * 2, 0f, size, 1f, 1f},
                {size * 3, 0f, size, 1f, 1f},
        });
        animations.put("right", new float[][]{
                {size * 4, 0f, size, 1f, 1f},
                {size * 5, 0f, size, 1f, 1f},
        });
        animations.put("up_right", new float[][]{
                {size * 6, 0f, size, 1f, 1f},
                {size * 7, 0f, size, 1f, 1f},
        });
        animations.put("up", new float[][]{
                {size * 8, 0f, size, 1f, 1f},
                {size * 9, 0f, size, 1f, 1f},
        });
        animations.put("up_left", new float[][]{
                {size * 10, 0f, size, 1f, 1f},
                {size * 11, 0f, size, 1f, 1f},
        });
        animations.put("left", new float[][]{
                {size * 12, 0f, size, 1f, 1f},
                {size * 13, 0f, size, 1f, 1f},
        });
        animations.put("down_left", new float[][]{
                {size * 14, 0f, size, 1f, 1f},
                {size * 15, 0f, size, 1f, 1f},
        });
        animations.put("hit", new float[][]{
                {size * 16, 0f, size, 1f, 2f},
        });
        animations.put("die", new float[][]{
                {size * 16, 0f, size, 1f, 1f},
                {size * 17, 0f, size, 1f, 1f},
                {size * 18, 0f, size, 1f, 1f},
                {size * 19, 0f, size, 1f, 0f},
        });
    }
}
