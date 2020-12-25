package ru.geekbrains.sprite;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.base.Ship;
import ru.geekbrains.math.Rect;
import ru.geekbrains.pool.BulletPool;
import ru.geekbrains.pool.ExplosionPool;

public class EnemyShip extends Ship {

    private EnemyType type;

    public EnemyShip(BulletPool bulletPool, ExplosionPool explosionPool, Rect worldBounds) {
        super(bulletPool, explosionPool);
        this.worldBounds = worldBounds;
        this.v = new Vector2();
        this.v0 = new Vector2();
        this.bulletPos = new Vector2();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        bulletPos.set(pos.x, pos.y - getHalfHeight());
        if (getBottom() < worldBounds.getBottom()) {
            destroy();
        }
    }

    public void shootReloader () {
        reloadTimer = reloadInterval;
    }

    public void setAfterReleaseVector(Vector2 afterRelease) {
        this.v.set(afterRelease);
    }

    public float getVy() {
        return v.y;
    }

    public EnemyType getType() {
        return type;
    }

    public void set(EnemyType type,
                    TextureRegion[] regions,
                    TextureRegion bulletRegion,
                    Sound bulletSound,
                    float bulletHeight,
                    Vector2 bulletV,
                    int damage,
                    int hp,
                    float reloadInterval,
                    Vector2 v0,
                    float height
    ) {
        this.type = type;
        this.regions = regions;
        this.bulletRegion = bulletRegion;
        this.bulletSound = bulletSound;
        this.bulletHeight = bulletHeight;
        this.bulletV = bulletV;
        this.damage = damage;
        this.hp = hp;
        this.reloadInterval = reloadInterval;
        this.v.set(v0);
        setHeightProportion(height);
    }

    public boolean isBulletCollision(Bullet bullet) {
        return !(bullet.getRight() < getLeft()
                || bullet.getLeft() > getRight()
                || bullet.getBottom() > getTop()
                || bullet.getTop() < pos.y);
    }
}
