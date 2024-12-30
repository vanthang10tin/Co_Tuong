package se.xiangqigdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

import java.util.Random;

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
        game.batch.draw(game.currentSkinMode == SkinMode.CHINESE ? game.chinesePieceTexture : game.englishPieceTexture, 763, 1920-1681, 898-763, 898-763);

        game.batch.end();

        if (Gdx.input.justTouched()) {
            game.touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            game.viewport.unproject(game.touchPos);
            int x = (int) game.touchPos.x, y = (int) game.touchPos.y;
            if (y <= 1920-829 && y > 0 && 90 < x && x < 990){
                if (y >= 1920-1054)
                    game.setScreen(new GameScreen(game, GameMode.PVE));
                else if (y >= 1920-1279)
                    game.setScreen(new GameScreen(game, GameMode.PVP));
                else if (y >= 1920-1504) // Add history button check
                    game.setScreen(new GameHistoryScreen(game));
                else if (y >= 1920-1729) {
                    // Toggle skin mode
                    game.currentSkinMode = (game.currentSkinMode == SkinMode.CHINESE) ?
                        SkinMode.ENGLISH : SkinMode.CHINESE;
                    game.skinPreferences.putString("skinMode", game.currentSkinMode == SkinMode.CHINESE ? "CHINESE": "ENGLISH");
                    game.skinPreferences.flush();
                }
                else if (y < 1920-1729){
                    GameScreen gs = new GameScreen(game, GameMode.PVE);
                    Random rand = new Random();
                    int map = Math.abs(rand.nextInt())%3;
                    gs.board.setupPieces(1+map);
                    Gdx.app.log("TheCo", "Setup Pieces "+(1+map));
                    game.setScreen(gs);
                }
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
