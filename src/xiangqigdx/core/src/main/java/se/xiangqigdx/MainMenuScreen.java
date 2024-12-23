package se.xiangqigdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

public class MainMenuScreen implements Screen {

    final Main game;
    public MainMenuScreen(final Main game){
        this.game = game;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta){
        Gdx.gl.glClearColor(0.8f, 0.8f, 0.8f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.camera.update();
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.camera.combined);

        game.batch.begin();
        game.batch.draw(game.texture, 0, 0, game.viewport.getWorldWidth(), game.viewport.getWorldHeight());
        // Draw skin mode text
        game.batch.draw(game.currentSkinMode == SkinMode.CHINESE ? game.chinesePieceTexture : game.englishPieceTexture, 5.5F, 0.25F, 1.5F, 1.5F);
        game.batch.end();

        if (Gdx.input.justTouched()) {
            game.touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            game.viewport.unproject(game.touchPos);
            int x = (int) game.touchPos.x, y = (int) game.touchPos.y;
            if (y >= 6 )
                game.setScreen(new GameScreen(game, GameMode.PVE));
            else if (y >= 4)
                game.setScreen(new GameScreen(game, GameMode.PVP));
            else if (y < 2) {
                // Toggle skin mode
                game.currentSkinMode = (game.currentSkinMode == SkinMode.CHINESE) ?
                    SkinMode.ENGLISH : SkinMode.CHINESE;
            }
            dispose();
        }

    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

}