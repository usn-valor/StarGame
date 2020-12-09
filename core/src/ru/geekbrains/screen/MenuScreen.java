package ru.geekbrains.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.base.BaseScreen;

public class MenuScreen extends BaseScreen {

    private Texture img;
    private Vector2 pos;
    private Vector2 touchVector;
    private Vector2 addVector;
    private Vector2 tmpVector;


    @Override
    public void show() {
        super.show();
        img = new Texture("badlogic.jpg");
        pos = new Vector2();
        touchVector = new Vector2();
        addVector = new Vector2();
        tmpVector = new Vector2();

    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl.glClearColor(0.55f, 0.23f, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(img, pos.x, pos.y);
        batch.end();
        tmpVector.set(touchVector);
        if (addVector.len() < tmpVector.sub(pos).len())
            pos.add(addVector);
        else
            pos.set(touchVector);
    }

    @Override
    public void dispose() {
        img.dispose();
        super.dispose();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touchVector.set(screenX, Gdx.graphics.getHeight() - screenY);
        addVector = touchVector.cpy().sub(pos);
        addVector.nor();
        addVector.scl(5);
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        pos.set(screenX, Gdx.graphics.getHeight() - screenY);
        return super.touchDragged(screenX, screenY, pointer);
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.UP:
                pos.y += 10;
                break;
            case Input.Keys.DOWN:
                pos.y -= 10;
                break;
            case Input.Keys.RIGHT:
                pos.x += 10;
                break;
            case Input.Keys.LEFT:
                pos.x -= 10;
                break;
        }
        return super.keyDown(keycode);
    }
}
