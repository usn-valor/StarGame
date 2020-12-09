package ru.geekbrains.sprite;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.base.Sprite;
import ru.geekbrains.math.Rect;

public class Logo extends Sprite {

    private Vector2 touchVector;
    private Vector2 addVector;
    private Vector2 tmpVector;

    public Logo(Texture region) {
        super(new TextureRegion(region));
        touchVector = new Vector2();
        addVector = new Vector2();
        tmpVector = new Vector2();

    }

    @Override
    public void resize(Rect worldBounds) {
        setHeightProportion(worldBounds.getHeight());
        this.pos.set(worldBounds.pos);
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(
                regions[frame],
                getLeft(), getBottom(),
                halfWidth, halfHeight,
                getWidth() / 2, getHeight() / 2,
                scale, scale,
                angle
        );
        tmpVector.set(touchVector);
        if (addVector.len() < tmpVector.sub(getLeft(), getBottom()).len())
            pos.add(addVector);
        else
            pos.set(touchVector.x + halfWidth, touchVector.y + halfHeight);
    }

    @Override
    public boolean touchDown(Vector2 touch, int pointer, int button) {
        System.out.println(toString());
        touchVector = touch;
        addVector = touch.cpy().sub(getLeft(), getBottom()).setLength(0.01f);
        return super.touchDown(touch, pointer, button);
    }

    @Override
    public String toString() {
        return "Logo{" +
                "pos=" + pos +
                '}';
    }
}
