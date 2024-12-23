package se.xiangqigdx;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.Stack;

enum GameMode {
    PVP,
    PVE
}

public class Board {
    ArrayList<Piece> pieces;
    Side player1Side;
    Side player2Side;
    Engine engine;

    Side currentPlayer;
    Piece selectedPiece;
    ArrayList<Position> validMovesPositions;
    Move lastMove;
    boolean gameOver;
    Side winner;
    boolean isStalemate;
    Stack<Move> moveStack;
    private GameMode gameMode;
    private boolean isProcessingAIMove = false;
    private boolean inCheck = false;  // Add field to cache check status
    private Side lastCheckedSide = null;  // Track which side was last checked

    public Side getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Side currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public boolean isStalemate() {
        return isStalemate;
    }

    public void setStalemate(boolean stalemate) {
        isStalemate = stalemate;
    }

    public Move getLastMove() {
        return lastMove;
    }

    public void setLastMove(Move lastMove) {
        this.lastMove = lastMove;
    }


    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }


    public Side getWinner() {
        return winner;
    }

    public void setWinner(Side winner) {
        this.winner = winner;
    }


    public void setPieces() {
        pieces = new ArrayList<Piece>();
        pieces.add(new Piece(Side.BLACK, Type.CHARIOT, 0, 0));
        pieces.add(new Piece(Side.BLACK, Type.CHARIOT, 8, 0));
        pieces.add(new Piece(Side.BLACK, Type.HORSE, 1, 0));
        pieces.add(new Piece(Side.BLACK, Type.HORSE, 7, 0));
        pieces.add(new Piece(Side.BLACK, Type.ELEPHANT, 2, 0));
        pieces.add(new Piece(Side.BLACK, Type.ELEPHANT, 6, 0));
        pieces.add(new Piece(Side.BLACK, Type.ADVISOR, 3, 0));
        pieces.add(new Piece(Side.BLACK, Type.ADVISOR, 5, 0));
        pieces.add(new Piece(Side.BLACK, Type.CANNON, 1, 2));
        pieces.add(new Piece(Side.BLACK, Type.CANNON, 7, 2));
        pieces.add(new Piece(Side.BLACK, Type.GENERAL, 4, 0));

        pieces.add(new Piece(Side.RED, Type.CHARIOT, 0, 9));
        pieces.add(new Piece(Side.RED, Type.CHARIOT, 8, 9));
        pieces.add(new Piece(Side.RED, Type.HORSE, 1, 9));
        pieces.add(new Piece(Side.RED, Type.HORSE, 7, 9));
        pieces.add(new Piece(Side.RED, Type.ELEPHANT, 2, 9));
        pieces.add(new Piece(Side.RED, Type.ELEPHANT, 6, 9));
        pieces.add(new Piece(Side.RED, Type.ADVISOR, 3, 9));
        pieces.add(new Piece(Side.RED, Type.ADVISOR, 5, 9));
        pieces.add(new Piece(Side.RED, Type.CANNON, 1, 7));
        pieces.add(new Piece(Side.RED, Type.CANNON, 7, 7));
        pieces.add(new Piece(Side.RED, Type.GENERAL, 4, 9));
        for (int i = 0; i <= 9; i += 2) {
            pieces.add(new Piece(Side.BLACK, Type.SOLDIER, i, 3));
            pieces.add(new Piece(Side.RED, Type.SOLDIER, i, 6));
        }
    }

    public Board(GameMode mode) {
        gameMode = mode;
        setPieces();
        selectedPiece = null;
        currentPlayer = Side.RED;
        player1Side = Side.RED;
        if (player1Side == Side.RED) player2Side = Side.BLACK;
        else player2Side = Side.RED;

        engine = new MinimaxEngine(player2Side);

        validMovesPositions = new ArrayList<>();
        gameOver = false;
        winner = null;
        isStalemate = false;
        lastMove = null;
        moveStack = new Stack<Move>();
    }

    // Add default constructor that defaults to PVP mode
    public Board() {
        this(GameMode.PVP);
    }

    public Piece getPieceAt(Position pos) {
        for (var p : pieces) if (p.pos.equals(pos)) return p;
        return null;
    }

    public Piece getPieceAt(int posX, int posY) {
        for (var p : pieces) {
            if (p.pos.x == posX && p.pos.y == posY) return p;
        }
        return null;
    }

    public void movePiece(Piece piece, Position newPos) {
        // Save last move
        Piece capturedPiece = getPieceAt(newPos);
        lastMove = new Move(piece, piece.pos, newPos, capturedPiece);
        moveStack.push(lastMove);

        // Make move
        if (capturedPiece != null) {
            pieces.remove(capturedPiece);
        }
        piece.pos = newPos;

        // Switch player first
        switchPlayer();

        // Reset check status cache since a move was made
        lastCheckedSide = null;

        // Then check game status
        checkGameStatus();
    }

    public Move makeAIMove() {
        isProcessingAIMove = true;
        Move aiMove = null;
        try {
            MinimaxResult result = Minimax.minimax(this, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, currentPlayer, true);
            if (result.move != null) {
                movePiece(result.move.pieceToMove, result.move.to);
                aiMove = result.move;
            }
        } finally {
            isProcessingAIMove = false;
        }
        return aiMove;
    }

    public void undoMove() {
        if (!moveStack.isEmpty()) {
            Move moveToRestore = moveStack.pop();
            if (moveToRestore != null) {
                if (moveToRestore.capturedPiece != null) {
                    pieces.add(moveToRestore.capturedPiece);
                }
                Piece movedPiece = getPieceAt(moveToRestore.to);
                if (movedPiece != null) {
                    movedPiece.pos = moveToRestore.from;
                }
                switchPlayer(); // Switch back to previous player
            }
        }
    }

    // handle AI moves here

    // handle click here

    public boolean isInCheck(Side side) {
        // Return cached result if checking same side and no moves made since last check
        if (side == lastCheckedSide && lastMove == null) {
            return inCheck;
        }

        // Find the general
        Piece general = null;
        for (var piece : pieces) {
            if (piece.type == Type.GENERAL && piece.side == side) {
                general = piece;
                break;
            }
        }

        if (general == null) return false;

        // Check each opposing piece
        for (var piece : pieces) {
            if (piece.side != side) {
                ArrayList<Position> moves = piece.getPseudoValidMovesPositions(this);
                if (moves.contains(general.pos)) {
                    inCheck = true;
                    lastCheckedSide = side;
                    return true;
                }
            }
        }

        inCheck = false;
        lastCheckedSide = side;
        return false;
    }

    public boolean isCheckAfterMove(Piece piece, Position move) {
        // Store original state
        Position originalPos = new Position(piece.pos.x, piece.pos.y);
        Piece capturedPiece = getPieceAt(move);

        // Simulate move
        if (capturedPiece != null) {
            pieces.remove(capturedPiece);
        }
        piece.pos = move;

        // Check if the move results in check
        boolean inCheck = isInCheck(piece.side);

        // Restore original state
        piece.pos = originalPos;
        if (capturedPiece != null) {
            pieces.add(capturedPiece);
        }

        return inCheck;
    }

    public ArrayList<Position> getValidMovesPositions(Piece piece) {
        ArrayList<Position> validMoves = new ArrayList<>();
        ArrayList<Position> potentialMoves = piece.getPseudoValidMovesPositions(this);

        // Create copy to avoid concurrent modification
        ArrayList<Position> movesToCheck = new ArrayList<>(potentialMoves);

        for (Position move : movesToCheck) {
            if (!isCheckAfterMove(piece, move)) {
                validMoves.add(move);
            }
        }

        return validMoves;
    }

    public boolean isCheckmate() {
        Side defendingSide = currentPlayer;

        if (!isInCheck(defendingSide)) {
            return false;
        }

        // Create a copy of pieces to avoid concurrent modification
        ArrayList<Piece> piecesToCheck = new ArrayList<>(pieces);

        // Check every piece of the defending side
        for (Piece piece : piecesToCheck) {
            if (piece.side == defendingSide) {
                ArrayList<Position> validMoves = getValidMovesPositions(piece);
                if (!validMoves.isEmpty()) {
                    return false;
                }
            }
        }

        return true;
    }

    public void checkGameStatus() {
        // First check if either general is captured
        boolean hasBlackGeneral = false;
        boolean hasRedGeneral = false;

        for (Piece piece : pieces) {
            if (piece.type == Type.GENERAL) {
                if (piece.side == Side.BLACK) hasBlackGeneral = true;
                if (piece.side == Side.RED) hasRedGeneral = true;
            }
        }

        if (!hasBlackGeneral) {
            gameOver = true;
            winner = Side.RED;
            return;
        }
        if (!hasRedGeneral) {
            gameOver = true;
            winner = Side.BLACK;
            return;
        }

        // Check for checkmate
        if (isInCheck(currentPlayer)) {
            if (isCheckmate()) {
                gameOver = true;
                winner = (currentPlayer == Side.RED) ? Side.BLACK : Side.RED;
                return;
            }
        }
        // Check for stalemate
        else if (checkStalemate()) {
            gameOver = true;
            isStalemate = true;
            winner = null;
            return;
        }

        // Check for flying general mate
        if (isGeneralsFacing()) {
            gameOver = true;
            winner = (currentPlayer == Side.RED) ? Side.BLACK : Side.RED;
            return;
        }
    }

    private boolean isGeneralsFacing() {
        Piece redGeneral = null;
        Piece blackGeneral = null;

        // Find both generals
        for (Piece piece : pieces) {
            if (piece.type == Type.GENERAL) {
                if (piece.side == Side.RED) redGeneral = piece;
                if (piece.side == Side.BLACK) blackGeneral = piece;
            }
        }

        if (redGeneral == null || blackGeneral == null) return false;

        // Check if generals are on same column
        if (redGeneral.pos.x != blackGeneral.pos.x) return false;

        // Check if there are pieces between generals
        int minY = Math.min(redGeneral.pos.y, blackGeneral.pos.y);
        int maxY = Math.max(redGeneral.pos.y, blackGeneral.pos.y);

        for (int y = minY + 1; y < maxY; y++) {
            if (getPieceAt(redGeneral.pos.x, y) != null) return false;
        }

        return true;
    }

    public boolean checkStalemate() {
        // Must not be in check
        if (isInCheck(currentPlayer)) {
            return false;
        }

        // Check if any piece has legal moves
        for (Piece piece : pieces) {
            if (piece.side == currentPlayer && !getValidMovesPositions(piece).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public void switchPlayer() {
        currentPlayer = (currentPlayer == Side.BLACK) ? Side.RED : Side.BLACK;
    }
}
