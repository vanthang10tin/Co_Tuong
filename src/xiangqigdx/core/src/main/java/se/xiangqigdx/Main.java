package se.xiangqigdx;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Main extends Game {
    public SpriteBatch batch;
    public BitmapFont font;
    public OrthographicCamera camera;
    public FitViewport viewport;
    public Texture texture;
    public Vector2 touchPos;
    public SkinMode currentSkinMode; // Add field
    public Texture chinesePieceTexture, englishPieceTexture;

    public void create(){
        batch = new SpriteBatch();
        font = new BitmapFont();
        camera = new OrthographicCamera();
        viewport = new FitViewport(8, 8, camera);
        texture = new Texture("mainmenu.png");
        chinesePieceTexture = new Texture("Pieces/Chinese-Pawn-Red.png");
        englishPieceTexture = new Texture("Pieces/English-Pawn-Red.png");
        font.setUseIntegerPositions(false);
        font.getData().setScale(viewport.getWorldHeight()/ Gdx.graphics.getHeight());
        touchPos = new Vector2();
        currentSkinMode = SkinMode.CHINESE; // Initialize skin mode
        this.setScreen(new MainMenuScreen(this));
    }

    public void render(){
        super.render(); // important
    }

    public void dispose(){
        batch.dispose();
        font.dispose();
        texture.dispose();
    }
}