package io.faucette.zombierampage;


public class FlamethrowerBulletControl extends Pauseable {
    private float life;
    private float lifeTime;


    public FlamethrowerBulletControl(float life) {
        super();

        this.life = 1f;
        lifeTime = 0f;
    }

    @Override
    public FlamethrowerBulletControl update() {
        lifeTime += entity.getScene().getTime().getDelta();

        if (lifeTime > life) {
            entity.getScene().removeEntity(entity);
        }

        return this;
    }
}
