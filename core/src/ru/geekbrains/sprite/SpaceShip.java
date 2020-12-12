package ru.geekbrains.sprite;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.base.Sprite;
import ru.geekbrains.math.Rect;

public class SpaceShip extends Sprite {

    private static final float ADD_VECTOR_LENGTH = 0.01f;
    private static final float PADDING = 0.005f;

    private Vector2 touchVector;
    private Vector2 addVector;
    private Vector2 tmpVector;
    private Rect worldBounds;


    public SpaceShip(TextureAtlas atlas) {
        super(new TextureRegion(atlas.findRegion("main_ship"), 0,
                0, atlas.findRegion("main_ship").getRegionWidth() / 2,
                atlas.findRegion("main_ship").getRegionHeight()));
        touchVector = new Vector2();
        addVector = new Vector2();
        tmpVector = new Vector2();
    }

    @Override
    public void resize(Rect worldBounds) {
        this.worldBounds = worldBounds;
        setHeightProportion(0.2f);
        this.pos.set(0, worldBounds.getBottom() + halfHeight + PADDING);
    }

    @Override
    public void update(float delta) {
        tmpVector.set(touchVector);
        if (addVector.len() < tmpVector.sub(pos).len())
            pos.add(addVector);
        else
            pos.set(touchVector);
        if (getLeft() < worldBounds.getLeft())
            pos.set(worldBounds.getLeft() + halfWidth + PADDING, pos.y);
        if (getRight() > worldBounds.getRight())
            pos.set(worldBounds.getRight() - halfWidth - PADDING, pos.y);
    }

    @Override
    public boolean touchDown(Vector2 touch, int pointer, int button) {
        if (touch.y != pos.y)
            touchVector.set(touch.x, pos.y);
        else
            touchVector = touch;
        addVector = touchVector.cpy().sub(pos).setLength(ADD_VECTOR_LENGTH);
        return super.touchDown(touch, pointer, button);
    }

    public void moveRight() {
        touchVector.set(worldBounds.getRight(), pos.y);
        addVector = touchVector.cpy().sub(pos).setLength(ADD_VECTOR_LENGTH);
    }

    public void moveLeft() {
        touchVector.set(worldBounds.getLeft(), pos.y);
        addVector = touchVector.cpy().sub(pos).setLength(ADD_VECTOR_LENGTH);
    }

    public void stop() {
        addVector.set(0, 0);
    }
}
