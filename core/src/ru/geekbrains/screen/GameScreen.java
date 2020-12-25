package ru.geekbrains.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

import java.util.List;

import ru.geekbrains.base.BaseScreen;
import ru.geekbrains.math.Rect;
import ru.geekbrains.pool.BulletPool;
import ru.geekbrains.pool.EnemyPool;
import ru.geekbrains.pool.ExplosionPool;
import ru.geekbrains.sprite.Background;
import ru.geekbrains.sprite.Bullet;
import ru.geekbrains.sprite.EnemyShip;
import ru.geekbrains.sprite.GameOver;
import ru.geekbrains.sprite.NewGame;
import ru.geekbrains.sprite.SpaceShip;
import ru.geekbrains.sprite.Star;
import ru.geekbrains.sprite.TrackingStar;
import ru.geekbrains.utils.EnemyEmitter;
import ru.geekbrains.utils.Font;

public class GameScreen extends BaseScreen {

    private static final float MARGIN = 0.01f;
    private static final int STAR_COUNT = 128;
    private static final String FRAGS = "Frags: ";
    private static final String HP = "HP: ";
    private static final String LEVEL = "Level: ";

    private enum State {PLAYING, GAME_OVER}

    private Texture bg;
    private Background background;

    private TextureAtlas atlas;
    private TrackingStar[] stars;

    private BulletPool bulletPool;
    private ExplosionPool explosionPool;
    private EnemyPool enemyPool;

    private EnemyEmitter enemyEmitter;
    private SpaceShip ship;

    private Sound bulletSound;
    private Sound explosionSound;

    private State state;

    private GameOver gameOver;
    private NewGame newGame;

    private int frags;

    private Font font;
    private StringBuilder sbFrags;
    private StringBuilder sbHp;
    private StringBuilder sbLvl;

    @Override
    public void show() {
        super.show();
        bg = new Texture("textures/bg.png");
        background = new Background(bg);

        atlas = new TextureAtlas("textures/mainAtlas.tpack");

        stars = new TrackingStar[STAR_COUNT];

        bulletPool = new BulletPool();
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.wav"));
        explosionPool = new ExplosionPool(atlas, explosionSound);
        enemyPool = new EnemyPool(bulletPool, explosionPool, worldBounds);

        ship = new SpaceShip(atlas, bulletPool, explosionPool);

        for (int i = 0; i < STAR_COUNT; i++)
            stars[i] = new TrackingStar(atlas, ship.getV());

        bulletSound = Gdx.audio.newSound(Gdx.files.internal("sounds/bullet.wav"));

        enemyEmitter = new EnemyEmitter(atlas, worldBounds, bulletSound, enemyPool);

        gameOver = new GameOver(atlas);
        newGame = new NewGame(atlas, this);

        music.setLooping(true);
        music.play();
        music.setPosition(playPos);

        state = State.PLAYING;

        frags = 0;

        font = new Font("font/font.fnt", "font/font.png");
        font.setSize(0.02f);
        sbFrags = new StringBuilder();
        sbHp = new StringBuilder();
        sbLvl = new StringBuilder();
    }

    public void newGame() {
        frags = 0;
        ship.resetAllParameters();
        bulletPool.clearActiveSpritesPool();
        enemyPool.clearActiveSpritesPool();
        explosionPool.clearActiveSpritesPool();
        state = State.PLAYING;
    }

    public GameOver getGameOver() {
        return gameOver;
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        update(delta);
        checkCollision();
        freeAllDestroyed();
        draw();
    }

    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);
        background.resize(worldBounds);
        for (Star star : stars)
            star.resize(worldBounds);
        ship.resize(worldBounds);
        gameOver.resize(worldBounds);
        newGame.resize(worldBounds);
    }

    @Override
    public void dispose() {
        bg.dispose();
        atlas.dispose();
        bulletPool.dispose();
        enemyPool.dispose();
        explosionPool.dispose();
        music.dispose();
        bulletSound.dispose();
        explosionSound.dispose();
        ship.dispose();
        font.dispose();
        super.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        ship.keyDown(keycode);
        newGame.keyDown(keycode);
        return super.keyDown(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        ship.keyUp(keycode);
        newGame.keyUp(keycode);
        return super.keyUp(keycode);
    }

    @Override
    public boolean touchDown(Vector2 touch, int pointer, int button) {
        ship.touchDown(touch, pointer, button);
        newGame.touchDown(touch, pointer, button);
        return super.touchDown(touch, pointer, button);
    }

    @Override
    public boolean touchUp(Vector2 touch, int pointer, int button) {
        ship.touchUp(touch, pointer, button);
        newGame.touchUp(touch, pointer, button);
        return super.touchUp(touch, pointer, button);
    }

    private void update(float delta) {
        for (TrackingStar star : stars)
            star.update(delta);
        if (state == State.PLAYING) {
            explosionPool.updateActiveObjects(delta);
            bulletPool.updateActiveObjects(delta);
            enemyPool.updateActiveObjects(delta);
            ship.update(delta);
            enemyEmitter.generate(delta, frags);
        }
        else if (state == State.GAME_OVER) {
            gameOver.update(delta);
            newGame.update(delta);
        }
    }

    private void checkCollision() {
        if (state == State.GAME_OVER) {
            return;
        }
        List<EnemyShip> enemyList = enemyPool.getActiveObjects();
        List<Bullet> bulletList = bulletPool.getActiveObjects();
        for (EnemyShip enemy : enemyList) {
            float minDist = enemy.getHalfWidth() + ship.getHalfWidth();
            if (ship.pos.dst(enemy.pos) < minDist) {
                enemy.destroy();
                ship.damage(enemy.getDamage());
            }
            for (Bullet bullet : bulletList) {
                if (bullet.getOwner() == ship && enemy.isBulletCollision(bullet)) {
                    enemy.damage(bullet.getDamage());
                    bullet.destroy();
                    if (enemy.isDestroyed())
                        frags++;
                }
            }
        }
        for (Bullet bullet : bulletList) {
            if (bullet.getOwner() != ship && ship.isBulletCollision(bullet)) {
                ship.damage(bullet.getDamage());
                bullet.destroy();
            }
        }
        if (ship.isDestroyed()) {
            state = State.GAME_OVER;
        }
    }

    private void freeAllDestroyed() {
        bulletPool.freeAllDestroyedActiveObjects();
        enemyPool.freeAllDestroyedActiveObjects();
        explosionPool.freeAllDestroyedActiveObjects();
    }

    private void draw() {
        Gdx.gl.glClearColor(0.55f, 0.23f, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        background.draw(batch);
        for (TrackingStar star : stars) {
            star.draw(batch);
        }
        if (state == State.PLAYING) {
            bulletPool.drawActiveObjects(batch);
            enemyPool.drawActiveObjects(batch);
            explosionPool.drawActiveObjects(batch);
            ship.draw(batch);
        }
        else if (state == State.GAME_OVER) {
            gameOver.draw(batch);
            newGame.draw(batch);
        }
        printInfo();
        batch.end();
    }

    private void printInfo() {
        sbFrags.setLength(0);
        font.setColor(Color.PURPLE);
        font.draw(batch, sbFrags.append(FRAGS).append(frags), worldBounds.getLeft() + MARGIN, worldBounds.getTop() - MARGIN);
        sbHp.setLength(0);
        colorHp();
        font.draw(batch, sbHp.append(HP).append(ship.getHp()), worldBounds.pos.x, worldBounds.getTop() - MARGIN, Align.center);
        sbLvl.setLength(0);
        font.setColor(Color.ORANGE);
        font.draw(batch, sbLvl.append(LEVEL).append(enemyEmitter.getLevel()), worldBounds.getRight() - MARGIN, worldBounds.getTop() - MARGIN, Align.right);
    }

    private void colorHp () {
        if (ship.getHp() >= 50)
            font.setColor(Color.GREEN);
        else if (ship.getHp() < 50 && ship.getHp() >= 10)
            font.setColor(Color.YELLOW);
        else
            font.setColor(Color.RED);
    }
}
