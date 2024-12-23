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
    private static final float BOARD_RATIO = 0.85f;  // Board takes 85% of available space
    private static final float BOARD_ASPECT_RATIO = 0.9f; // Board width/height ratio
    private float boardWidth;
    private float boardHeight;
    private float boardX;
    private float boardY;
    private float cellSize;
    private float pieceSize;
    private float validMoveHintSize;
    private Piece selectedPiece;
    private Side currentPlayer = Side.RED;  // Red goes first
    private BitmapFont font;
    private GameMode gameMode;
    private SkinMode skinMode;
    private static final float SCREEN_WIDTH = 1100;  // Wider than board
    private static final float SCREEN_HEIGHT = 1200; // Taller than board
    private static final float BACK_BUTTON_SIZE = 40;
    private static final float BACK_BUTTON_PADDING = 20;
    private float BACK_BUTTON_X;
    private float BACK_BUTTON_Y;
    private Texture backButtonTexture;  // Add this field


    public GameScreen(){
        this.game = game;

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);

        // Initialize dimensions
        updateBoardDimensions(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Load textures
        boardTexture = new Texture("xiangqi_gmchess_wood.png");
        skinMode = SkinMode.CHINESE; // Default skin
        loadPieceTextures();
        hintTexture = new Texture("validMovesHint.png");
        backButtonTexture = new Texture("back_arrow.png");  // Add after other texture loading

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
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);

        // Initialize dimensions
        updateBoardDimensions(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Load textures
        boardTexture = new Texture("xiangqi_gmchess_wood.png");
        skinMode = game.currentSkinMode; // Use skin mode from Main
        loadPieceTextures();
        hintTexture = new Texture("validMovesHint.png");
        backButtonTexture = new Texture("back_arrow.png");  // Add after other texture loading

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
        String skinPrefix = getSkinPrefix();

        for (int i = 0; i < types.length; i++) {
            pieceTextures[i] = new Texture("Pieces/" + skinPrefix + types[i] + "-Red.png");
            pieceTextures[i + 7] = new Texture("Pieces/" + skinPrefix + types[i] + "-Black.png");
        }
    }

    private String getSkinPrefix() {
        switch (skinMode) {
            case CHINESE:
                return "Chinese-";
            case ENGLISH:
                return "English-";
            default:
                return "Chinese-";
        }
    }

    public void setSkinMode(SkinMode newMode) {
        if (this.skinMode != newMode) {
            this.skinMode = newMode;
            // Dispose old textures
            for (Texture texture : pieceTextures) {
                texture.dispose();
            }
            // Load new textures
            loadPieceTextures();
        }
    }

    public SkinMode getSkinMode() {
        return skinMode;
    }

    private void updateBoardDimensions(int screenWidth, int screenHeight) {
        boolean isPortrait = screenHeight > screenWidth;

        if (isPortrait) {
            boardWidth = screenWidth * BOARD_RATIO;
            boardHeight = boardWidth / BOARD_ASPECT_RATIO;

            // Ensure board height doesn't exceed screen height
            if (boardHeight > screenHeight * BOARD_RATIO) {
                boardHeight = screenHeight * BOARD_RATIO;
                boardWidth = boardHeight * BOARD_ASPECT_RATIO;
            }
        } else {
            boardHeight = screenHeight * BOARD_RATIO;
            boardWidth = boardHeight * BOARD_ASPECT_RATIO;

            // Ensure board width doesn't exceed screen width
            if (boardWidth > screenWidth * BOARD_RATIO) {
                boardWidth = screenWidth * BOARD_RATIO;
                boardHeight = boardWidth / BOARD_ASPECT_RATIO;
            }
        }

        // Update dependent dimensions
        cellSize = boardWidth / 9;
        pieceSize = cellSize * 0.9f;
        validMoveHintSize = cellSize * 0.5f;

        // Center the board
        boardX = (screenWidth - boardWidth) / 2;
        boardY = (screenHeight - boardHeight) / 2;

        // Update back button position relative to screen size
        BACK_BUTTON_X = BACK_BUTTON_PADDING;
        BACK_BUTTON_Y = screenHeight - BACK_BUTTON_SIZE - BACK_BUTTON_PADDING;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);  // Darker background
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        // Draw board with current dimensions
        batch.draw(boardTexture, boardX, boardY, boardWidth, boardHeight);

        // Draw pieces with calculated positions
        for (Piece piece : board.pieces) {
            Texture pieceTexture = getPieceTexture(piece);
            float x = boardX + piece.pos.x * cellSize + (cellSize - pieceSize) / 2;
            float y = boardY + (9 - piece.pos.y) * cellSize + (cellSize - pieceSize) / 2;
            if (piece == selectedPiece) {
                batch.setColor(1, 1, 0, 0.5f);
                batch.draw(pieceTexture, x, y, pieceSize, pieceSize);
                batch.setColor(1, 1, 1, 1);
            }
            batch.draw(pieceTexture, x, y, pieceSize, pieceSize);
        }

        // Draw hints with calculated positions
        if (validMovesPositions != null && !validMovesPositions.isEmpty()) {
            for (Position pos : validMovesPositions) {
                float x = boardX + pos.x * cellSize + (cellSize - validMoveHintSize) / 2;
                float y = boardY + (9 - pos.y) * cellSize + (cellSize - validMoveHintSize) / 2;
                batch.draw(hintTexture, x, y, validMoveHintSize, validMoveHintSize);
            }
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
            float textX = (SCREEN_WIDTH - layout.width) / 2;
            float textY = (SCREEN_HEIGHT + layout.height) / 2;

            // Draw shadow/outline effect
            font.setColor(Color.BLACK);
            font.draw(batch, resultText, textX-2, textY-2);
            font.draw(batch, resultText, textX+2, textY+2);

            // Draw main text
            font.setColor(Color.YELLOW);
            font.draw(batch, resultText, textX, textY);
        }

        // Draw back button
        batch.setColor(0.8f, 0.8f, 0.8f, 0.8f);  // Light gray, semi-transparent
        batch.draw(backButtonTexture, BACK_BUTTON_X, BACK_BUTTON_Y, BACK_BUTTON_SIZE, BACK_BUTTON_SIZE);
        batch.setColor(Color.WHITE);

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
        camera.position.set(width / 2, height / 2, 0);
        updateBoardDimensions(width, height);
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
        backButtonTexture.dispose();  // Add this line
    }

    public void input(){
        if (Gdx.input.justTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPos);

            // Check back button first
            if (touchPos.x >= BACK_BUTTON_X && touchPos.x <= BACK_BUTTON_X + BACK_BUTTON_SIZE &&
                touchPos.y >= BACK_BUTTON_Y && touchPos.y <= BACK_BUTTON_Y + BACK_BUTTON_SIZE) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
                return;
            }

            // Adjust touch position for board coordinates
            touchPos.x -= boardX;
            touchPos.y -= boardY;

            // Only process board touches if within board bounds
            if (touchPos.x >= 0 && touchPos.x < boardWidth &&
                touchPos.y >= 0 && touchPos.y < boardHeight) {
                int x = (int) (touchPos.x/cellSize);
                int y = 9 - (int) (touchPos.y/cellSize);

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
}
