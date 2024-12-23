package se.xiangqigdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import java.util.ArrayList;

public class GameScreen implements Screen {
    Main game;

    private SpriteBatch batch;
    private Texture boardTexture;
    private Texture[] pieceTextures;
    private Texture hintTexture;
    private Board board;
    ArrayList<Position> validMovesPositions;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Vector2 touchPos;
    private static final float BOARD_WIDTH = 900;
    private static final float BOARD_HEIGHT = 1000;
    private static final float CELL_SIZE = BOARD_WIDTH / 9; // 9 columns
    private static final float PIECE_SIZE = CELL_SIZE * 0.9f; // slightly smaller than cell
    private static final float VALID_MOVE_HINT_SIZE = CELL_SIZE/2;
    private Piece selectedPiece;
    private Side currentPlayer = Side.RED;  // Red goes first
    private BitmapFont font;
    private GameMode gameMode;

    public GameScreen(Main game){
        this.game = game;

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(BOARD_WIDTH, BOARD_HEIGHT, camera);

        // Load textures
        boardTexture = new Texture("xiangqi_gmchess_wood.png");
        loadPieceTextures();
        hintTexture = new Texture("validMovesHint.png");

        // Set the game mode - you can modify this to be set by user input
        gameMode = GameMode.PVE; // or GameMode.PVP

        // Initialize game board with selected mode
        board = new Board(gameMode);
        validMovesPositions = new ArrayList<>();

        touchPos = new Vector2();
        selectedPiece = null;

        font = new BitmapFont();
        font.getData().setScale(4); // Make text larger
    }

    public GameScreen(Main game, GameMode gameMode){
        this.game = game;

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(BOARD_WIDTH, BOARD_HEIGHT, camera);

        // Load textures
        boardTexture = new Texture("xiangqi_gmchess_wood.png");
        loadPieceTextures();
        hintTexture = new Texture("validMovesHint.png");

        // Set the game mode - you can modify this to be set by user input
        this.gameMode = gameMode;// or GameMode.PVP

        // Initialize game board with selected mode
        board = new Board(gameMode);
        validMovesPositions = new ArrayList<>();

        touchPos = new Vector2();
        selectedPiece = null;

        font = new BitmapFont();
        font.getData().setScale(4); // Make text larger
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
    public void render(float delta) {
        Gdx.gl.glClearColor(0.8f, 0.8f, 0.8f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        // Draw non-transparent objects (board & pieces)
        Color c = batch.getColor();
        batch.setColor(c.r, c.g, c.b, 1f);

        // Draw board
        batch.draw(boardTexture, 0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        // Draw pieces
        for (Piece piece : board.pieces) {
            Texture pieceTexture = getPieceTexture(piece);
            float x = piece.pos.x * CELL_SIZE + (CELL_SIZE - PIECE_SIZE) / 2;
            float y = (9 - piece.pos.y) * CELL_SIZE + (CELL_SIZE - PIECE_SIZE) / 2;
            // Highlight selected piece
            if (piece == selectedPiece) {
                batch.setColor(1, 1, 0, 0.5f);  // Yellow highlight
                batch.draw(pieceTexture, x, y, PIECE_SIZE, PIECE_SIZE);
                batch.setColor(1, 1, 1, 1);  // Reset color
            }
            batch.draw(pieceTexture, x, y, PIECE_SIZE, PIECE_SIZE);
        }

        // Draw transparent objects (valid moves hints)
        c = batch.getColor();
        batch.setColor(c.r, c.g, c.b, 0.8f);

        if (validMovesPositions != null && !validMovesPositions.isEmpty())
            for (Position pos : validMovesPositions){
                float x = pos.x * CELL_SIZE + (CELL_SIZE - VALID_MOVE_HINT_SIZE) / 2;
                float y = (9 - pos.y) * CELL_SIZE + (CELL_SIZE - VALID_MOVE_HINT_SIZE) / 2;
                batch.draw(hintTexture, x, y, VALID_MOVE_HINT_SIZE, VALID_MOVE_HINT_SIZE);
            }

        // Draw game result if game is over
        if (board.isGameOver()) {
            String resultText;
            if (board.isStalemate()) {
                resultText = "Stalemate!";
            } else {
                resultText = (board.getWinner() == Side.RED ? "Red" : "Black") + " Wins!";
            }

            // Center the text using GlyphLayout
            com.badlogic.gdx.graphics.g2d.GlyphLayout layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout();
            layout.setText(font, resultText);
            float textX = (BOARD_WIDTH - layout.width) / 2;
            float textY = (BOARD_HEIGHT + layout.height) / 2;

            // Draw shadow/outline effect
            font.setColor(Color.BLACK);
            font.draw(batch, resultText, textX-2, textY-2);
            font.draw(batch, resultText, textX+2, textY+2);

            // Draw main text
            font.setColor(Color.YELLOW);
            font.draw(batch, resultText, textX, textY);
        }

        batch.end();

        input();
    }

    private Texture getPieceTexture(Piece piece) {
        int index = getPieceTextureIndex(piece.type, piece.side);
        return pieceTextures[index];
    }

    private int getPieceTextureIndex(Type type, Side side) {
        int baseIndex = side == Side.RED ? 0 : 7;
        switch (type) {
            case GENERAL: return baseIndex;
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
    public void show() {

    }
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
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
        batch.dispose();
        boardTexture.dispose();
        for (Texture texture : pieceTextures) {
            texture.dispose();
        }
        font.dispose();
    }

    public void input(){
        if (!board.isGameOver() && Gdx.input.justTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPos);
            int x = (int) (touchPos.x/CELL_SIZE);
            int y = 9 - (int) (touchPos.y/CELL_SIZE);

            Piece touchedPiece = board.getPieceAt(x, y);

            if (selectedPiece == null) {
                if (touchedPiece != null && touchedPiece.side == currentPlayer) {
                    selectedPiece = touchedPiece;
                    validMovesPositions = board.getValidMovesPositions(selectedPiece);
                }
            } else {
                Position newPos = new Position(x, y);
                if (validMovesPositions.contains(newPos)) {
                    board.movePiece(selectedPiece, newPos);
                    currentPlayer = board.getCurrentPlayer(); // Update current player from board
                }
                selectedPiece = null;
                validMovesPositions.clear();
            }
        }
    }
}
