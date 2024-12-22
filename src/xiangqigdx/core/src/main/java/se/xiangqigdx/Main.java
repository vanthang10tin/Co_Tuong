package se.xiangqigdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture boardTexture;
    private Texture[] pieceTextures;
    private Board board;
    private OrthographicCamera camera;
    private Viewport viewport;

    private static final float BOARD_WIDTH = 800;
    private static final float BOARD_HEIGHT = 900;
    private static final float CELL_SIZE = BOARD_WIDTH / 9; // 9 columns
    private static final float PIECE_SIZE = CELL_SIZE * 0.9f; // slightly smaller than cell

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(BOARD_WIDTH, BOARD_HEIGHT, camera);

        // Load textures
        boardTexture = new Texture("xiangqi_gmchess_wood.png");
        loadPieceTextures();

        // Initialize game board
        board = new Board();
    }

    private void loadPieceTextures() {
        pieceTextures = new Texture[14]; // 7 piece types * 2 sides
        String[] types = {"King", "Advisor", "Elephant", "Horse", "Rook", "Cannon", "Pawn"};
        for (int i = 0; i < types.length; i++) {
            pieceTextures[i] = new Texture("Pieces/Chinese-" + types[i] + "-Red.png");
            pieceTextures[i + 7] = new Texture("Pieces/Chinese-" + types[i] + "-Black.png");
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.8f, 0.8f, 0.8f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        // Draw board
        batch.draw(boardTexture, 0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        // Draw pieces
        for (Piece piece : board.pieces) {
            Texture pieceTexture = getPieceTexture(piece);
            float x = piece.pos.x * CELL_SIZE + (CELL_SIZE - PIECE_SIZE) / 2;
            float y = (9 - piece.pos.y) * CELL_SIZE + (CELL_SIZE - PIECE_SIZE) / 2;
            batch.draw(pieceTexture, x, y, PIECE_SIZE, PIECE_SIZE);
        }
        batch.end();
    }

    private Texture getPieceTexture(Piece piece) {
        int index = getPieceTextureIndex(piece.type, piece.side);
        return pieceTextures[index];
    }

    private int getPieceTextureIndex(Type type, Side side) {
        int baseIndex = side == Side.RED ? 0 : 7;
        switch (type) {
            case GENERAL: return baseIndex + 0;
            case ADVISOR: return baseIndex + 1;
            case ELEPHANT: return baseIndex + 2;
            case HORSE: return baseIndex + 3;
            case CHARIOT: return baseIndex + 4;
            case CANNON: return baseIndex + 5;
            case SOLDIER: return baseIndex + 6;
            default: return 0;
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        boardTexture.dispose();
        for (Texture texture : pieceTextures) {
            texture.dispose();
        }
    }
}
