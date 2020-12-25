package ru.geekbrains.sprite;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ru.geekbrains.base.BaseButton;
import ru.geekbrains.math.Rect;
import ru.geekbrains.screen.GameScreen;

public class NewGame extends BaseButton {

    private static final float GAMER_OVER_MARGIN = 0.05f;

    private final GameScreen gameScreen;

    public NewGame(TextureAtlas atlas, GameScreen gameScreen) {
        super(atlas.findRegion("button_new_game"));
        this.gameScreen = gameScreen;
    }


    @Override
    public void resize(Rect worldBounds) {
        setHeightProportion(0.05f);
        setTop(worldBounds.pos.y - gameScreen.getGameOver().getHalfHeight() - GAMER_OVER_MARGIN);
    }

    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ENTER)
            setScale(SCALE);
        return false;
    }

    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.ENTER) {
            action();
            setScale(1f);
        }
        return false;
    }

    @Override
    public void action() {
        gameScreen.newGame();
    }
}