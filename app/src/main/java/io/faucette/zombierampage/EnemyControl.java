package io.faucette.zombierampage;


import io.faucette.math.Vec2;
import io.faucette.scene_graph.Component;
import io.faucette.scene_graph.Entity;
import io.faucette.scene_graph.Scene;
import io.faucette.transform_components.Transform2D;


public class EnemyControl extends Pauseable {
    private static float MIN_PLAYER_DISTANCE = 1.5f;
    private static float MIN_TILE_DISTANCE = 0.5f;

    private TileControl prevTile = null;
    private Vec2 playerDistance = new Vec2();
    private Vec2 tileDistance = new Vec2();
    private Vec2 direction = new Vec2();
    private Vec2 velocity = new Vec2();


    public EnemyControl() {
        super();
    }

    @Override
    public EnemyControl update() {
        if (this.isPaused()) {
            return this;
        }

        StatusControl statusControl = entity.getComponent(StatusControl.class);
        StatusControl.State state = statusControl.getState();

        if (state == StatusControl.State.Alive) {
            entity.getComponent(RigidBody.class).setType(RigidBody.Type.Dynamic);

            Vec2 position = entity.getComponent(Transform2D.class).getPosition();
            Entity player = entity.getScene().getEntity("player");

            if (player != null) {
                Vec2.sub(
                        playerDistance,
                        entity.getScene().getEntity("player").getComponent(Transform2D.class).getPosition(),
                        position
                );
            }
            float distance = Vec2.normalize(direction, playerDistance);

            if (distance > MIN_PLAYER_DISTANCE) {
                Scene scene = entity.getScene();
                TileControlManager tileControlManager = scene.getComponentManager(TileControlManager.class);
                TileControl tile = tileControlManager.find(position);

                if (tile != null) {
                    followPlayer(tileControlManager, tile, position);
                }
            }
        } else if (state == StatusControl.State.Birth || state == StatusControl.State.Dying || state == StatusControl.State.Dead) {
            velocity.set(0f, 0f);
            entity.getComponent(RigidBody.class).setType(RigidBody.Type.Static);
        }

        Vec2.smul(velocity, direction, statusControl.getSpeed());
        entity.getComponent(RigidBody.class).velocity.add(velocity);

        return this;
    }

    private void followPlayer(TileControlManager tileControlManager, TileControl tile, Vec2 position) {
        if (prevTile == null) {
            prevTile = tile;
        }
        TileControl nextTile = prevTile;

        Vec2.sub(
                tileDistance,
                tile.getEntity().getComponent(Transform2D.class).getPosition(),
                position
        );

        if (tileDistance.length() < MIN_TILE_DISTANCE) {
            mapDirection(direction, direction);

            int x = tile.getX();
            int y = tile.getY();

            TileControl topTile = tileControlManager.find(x, y + 1);
            TileControl leftTile = tileControlManager.find(x - 1, y);
            TileControl bottomTile = tileControlManager.find(x, y - 1);
            TileControl rightTile = tileControlManager.find(x + 1, y);

            if (direction.y == 1f) {
                if (topTile != null) {
                    nextTile = topTile;
                } else if (leftTile != null) {
                    nextTile = leftTile;
                } else if (rightTile != null) {
                    nextTile = rightTile;
                } else {
                    nextTile = bottomTile;
                }
            } else if (direction.y == -1f) {
                if (bottomTile != null) {
                    nextTile = bottomTile;
                } else if (leftTile != null) {
                    nextTile = leftTile;
                } else if (rightTile != null) {
                    nextTile = rightTile;
                } else {
                    nextTile = topTile;
                }
            } else if (direction.x == 1f) {
                if (rightTile != null) {
                    nextTile = rightTile;
                } else if (topTile != null) {
                    nextTile = topTile;
                } else if (bottomTile != null) {
                    nextTile = bottomTile;
                } else {
                    nextTile = leftTile;
                }
            } else if (direction.x == -1f) {
                if (leftTile != null) {
                    nextTile = leftTile;
                } else if (topTile != null) {
                    nextTile = topTile;
                } else if (bottomTile != null) {
                    nextTile = bottomTile;
                } else {
                    nextTile = rightTile;
                }
            }
        }

        if (nextTile != null) {
            Vec2.sub(
                    direction,
                    nextTile.getEntity().getComponent(Transform2D.class).getPosition(),
                    position
            ).normalize();

            prevTile = nextTile;
        }
    }

    private Vec2 mapDirection(Vec2 out, Vec2 direction) {
        float signX = Math.signum(direction.x);
        float signY = Math.signum(direction.y);
        float x = Math.abs(direction.x);
        float y = Math.abs(direction.y);

        if (x > y) {
            out.x = 1f * signX;
            out.y = 0f;
        } else {
            out.x = 0f;
            out.y = 1f * signY;
        }

        return out;
    }
}
