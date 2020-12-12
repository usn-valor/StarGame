package ru.geekbrains.sprite;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.base.Sprite;
import ru.geekbrains.math.Rect;

public class Logo extends Sprite {

    private static final float ADD_VECTOR_LENGTH = 0.01f;

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
        setHeightProportion(0.3f);
        this.pos.set(worldBounds.pos);
    }

    @Override
    public void update(float delta) {
        tmpVector.set(touchVector);
        if (addVector.len() < tmpVector.sub(pos).len())
            pos.add(addVector);
        else
            pos.set(touchVector);
    }

    @Override
    public boolean touchDown(Vector2 touch, int pointer, int button) {
        touchVector = touch;
        addVector = touch.cpy().sub(pos).setLength(ADD_VECTOR_LENGTH);
        return false;
    }
}
