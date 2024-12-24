package se.xiangqigdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import java.util.List;

public class GameHistoryScreen implements Screen {
    private Main game;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private List<GameRecord> games;
    private static final float LINE_HEIGHT = 100f; // Increased from 60f
    private static final float PADDING = 80f; // Increased from 50f
    private float scrollPosition = 0;
    private static final float SCROLL_SPEED = 500f;
    private BitmapFont titleFont;
    private BitmapFont historyFont;
    private boolean isLoading = true;
    private String loadingText = "Loading...";
    private float loadingDots = 0;
    private static final float DOT_INTERVAL = 0.5f;

    public GameHistoryScreen(Main game) {
        this.game = game;
        camera = new OrthographicCamera();
        viewport = new FitViewport(1080, 1920, camera);
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);

        // Create larger fonts
        titleFont = new BitmapFont();
        titleFont.getData().setScale(4.0f); // Large font for title

        historyFont = new BitmapFont();
        historyFont.getData().setScale(2.5f); // Medium font for history entries

        // Start loading games in background
        new Thread(() -> {
            games = game.gameDatabase.getGameHistory();
            isLoading = false;
            Gdx.app.log("GameHistoryScreen", "Loaded " + games.size() + " games");
        }).start();
    }

    @Override
    public void render(float delta) {
        // Handle scrolling
        if (Gdx.input.isTouched() && !isLoading) {
            scrollPosition += Gdx.input.getDeltaY() * SCROLL_SPEED * delta;
            // Limit scrolling
            scrollPosition = Math.min(Math.max(0, scrollPosition), 
                Math.max(0, games.size() * LINE_HEIGHT - viewport.getWorldHeight() + 2 * PADDING));
        }

        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        if (isLoading) {
            // Show loading animation
            loadingDots += delta;
            String dots = ".".repeat((int)((loadingDots / DOT_INTERVAL) % 4));
            titleFont.draw(game.batch, loadingText + dots, 
                viewport.getWorldWidth()/2 - 100, viewport.getWorldHeight()/2);
        } else {
            // Draw header with larger font
            String headerText = String.format("Game History");
            titleFont.draw(game.batch, headerText, PADDING, viewport.getWorldHeight() - PADDING);

            // Draw game records with medium font
            float y = viewport.getWorldHeight() - PADDING - LINE_HEIGHT - scrollPosition;
            for (GameRecord record : games) {
                if (y + LINE_HEIGHT > 0 && y < viewport.getWorldHeight()) {
                    historyFont.draw(game.batch, record.toString(), PADDING, y);
                }
                y -= LINE_HEIGHT;
            }

            // Draw back button with medium font
            historyFont.draw(game.batch, "Back", PADDING, PADDING * 2);

            // Draw total games count with medium font
            historyFont.draw(game.batch, "Total Games: " + games.size(), 
                viewport.getWorldWidth() - 400, viewport.getWorldHeight() - PADDING);
        }

        game.batch.end();

        // Handle input only when loaded
        if (!isLoading) {
            handleInput();
        }
    }

    private void handleInput() {
        // Handle back button
        if (Gdx.input.justTouched()) {
            game.touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(game.touchPos);
            
            // Check if back button was clicked
            if (game.touchPos.y < PADDING * 3) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void show() {
        // Force refresh games list when screen is shown
        isLoading = true;
        new Thread(() -> {
            games = game.gameDatabase.getGameHistory();
            isLoading = false;
            scrollPosition = 0;
            Gdx.app.log("GameHistoryScreen", "Refreshed history, found " + games.size() + " games");
        }).start();
    }

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        if (titleFont != null) titleFont.dispose();
        if (historyFont != null) historyFont.dispose();
    }
}
