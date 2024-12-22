package se.xiangqigdx;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Stack;

enum GameMode {
    PVP,
    PVE
}

class Position {
    public int x, y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}

class Piece {
    public Side side;
    public Type type;
    public Position pos;

    public Piece(Side side, Type type, Position pos) {
        this.side = side;
        this.type = type;
        this.pos = pos;
    }

    public Piece(Side side, Type type, int posX, int posY) {
        this.side = side;
        this.type = type;
        this.pos = new Position(posX, posY);
    }

    @Override
    public String toString() {
        return " " + side + " " + type;
    }

    public ArrayList<Position> getPseudoValidMovesPositions(Board board) {
        ArrayList<Position> movesPositions = new ArrayList<>();
        int x = pos.x, y = pos.y;
        switch (type) {
            case CHARIOT: {
                int[] dx = {1, -1, 0, 0}, dy = {0, 0, 1, -1};
                for (int i = 0; i < 4; i++) {
                    for (int j = 1; j < 10; j++) {
                        int new_x = x + dx[i] * j;
                        int new_y = y + dy[i] * j;
                        if (!(0 <= new_x && new_x <= 8 && 0 <= new_y && new_y <= 9)) {
                            break;
                        }
                        Piece p = board.getPieceAt(new_x, new_y);
                        if (p == null) {
                            movesPositions.add(new Position(new_x, new_y));
                        } else if (p.side != this.side) {
                            movesPositions.add(new Position(new_x, new_y));
                            break;  // Stop after capturing
                        } else {
                            break;  // Stop at own piece
                        }
                    }
                }
                break;
            }
            case HORSE: {
                int[] dx = {1, 2, 2, 1, -1, -2, -2, -1}, dy = {2, 1, -1, -2, -2, -1, 1, 2};
                for (int i = 0; i < 8; i++) {
                    int new_x = x + dx[i], new_y = y + dy[i];
                    if (0 <= new_x && new_x <= 8 && 0 <= new_y && new_y <= 9) {
                        // Check blocks
                        Piece p = board.getPieceAt(new Position(x + dx[i] / 2, y + dy[i] / 2));
                        if (p != null) continue;

                        p = board.getPieceAt(new Position(new_x, new_y));
                        if (p == null) movesPositions.add(new Position(new_x, new_y));
                        else if (p.side != this.side) movesPositions.add(new Position(new_x, new_y));
                    }

                }
                break;
            }
            case ELEPHANT: {
                int[] dx = {2, -2, -2, 2}, dy = {2, 2, -2, -2};
                for (int i = 0; i < 4; i++) {
                    int new_x = x + dx[i], new_y = y + dy[i];
                    if (0 <= new_x && new_x <= 8 && ((side == Side.BLACK && 0 <= new_y && new_y <= 4) || (side == Side.RED && 5 <= new_y && new_y <= 9))) {
                        // Check blocks
                        Piece p = board.getPieceAt(x + dx[i] / 2, y + dy[i] / 2);
                        if (p != null) continue;

                        p = board.getPieceAt(new_x, new_y);
                        if (p == null) movesPositions.add(new Position(new_x, new_y));
                        else if (p.side != this.side) movesPositions.add(new Position(new_x, new_y));

                    }

                }
                break;
            }
            case ADVISOR: {
                int[] dx = {1, 1, -1, -1}, dy = {1, -1, 1, -1};
                for (int i = 0; i < 4; i++) {
                    int new_x = x + dx[i], new_y = y + dy[i];
                    if (3 <= new_x && new_x <= 5 && ((side == Side.RED && 7 <= new_y && new_y <= 9) ||
                        (side == Side.BLACK && 0 <= new_y && new_y <= 2))) {
                        Piece p = board.getPieceAt(new_x, new_y);
                        if (p == null || p.side != this.side) {
                            movesPositions.add(new Position(new_x, new_y));
                        }
                    }
                }
                break;
            }
            case GENERAL: {
                int[] dx = {1, -1, 0, 0}, dy = {0, 0, 1, -1};
                for (int i = 0; i < 4; i++) {
                    int new_x = x + dx[i], new_y = y + dy[i];
                    if (3 <= new_x && new_x <= 5 && ((side == Side.RED && 7 <= new_y && new_y <= 9) || (side == Side.BLACK && 0 <= new_y && new_y <= 2))) {
                        Piece p = board.getPieceAt(new_x, new_y);
                        if (p == null) movesPositions.add(new Position(new_x, new_y));
                        else if (p.side != this.side) movesPositions.add(new Position(new_x, new_y));
                    }
                }

                // Check General
                int tmp_y = y;
                int dir = (side == Side.BLACK) ? 1 : -1;
                while (0 <= tmp_y && tmp_y <= 9) {
                    tmp_y += dir;
                    if (board.getPieceAt(x, tmp_y) != null) {
                        if (board.getPieceAt(x, tmp_y).type == Type.GENERAL)
                            movesPositions.add(new Position(x, tmp_y));
                        else break;
                    }
                }
                break;
            }
            case CANNON: {
                int[] dx = {1, -1, 0, 0}, dy = {0, 0, 1, -1};
                for (int i = 0; i < 4; i++) {
                    boolean jump = false;
                    for (int j = 1; j <= 10; j++) {
                        int new_x = x + dx[i] * j, new_y = y + dy[i] * j;
                        if (!(0 <= new_x && new_x <= 8 && 0 <= new_y && new_y <= 9)) break;
                        Piece p = board.getPieceAt(new_x, new_y);
                        if (p == null) {
                            if (!jump) movesPositions.add(new Position(new_x, new_y));
                        } else if (!jump) jump = true;
                        else if (p.side != this.side) {
                            movesPositions.add(new Position(new_x, new_y));
                            break;
                        } else break;
                    }
                }
                break;
            }
            case SOLDIER: {
                int dir = (side == Side.BLACK) ? 1 : -1;
                boolean crossedRiver = (side == Side.BLACK) ? y > 4 : y < 5;

                if (!crossedRiver) {
                    // Before crossing river - can only move forward
                    int new_y = y + dir;
                    if (0 <= new_y && new_y <= 9) {
                        Piece p = board.getPieceAt(x, new_y);
                        if (p == null || p.side != this.side) {
                            movesPositions.add(new Position(x, new_y));
                        }
                    }
                } else {
                    // After crossing river - can move forward and sideways
                    for (int[] move : new int[][]{{0, dir}, {-1, 0}, {1, 0}}) {
                        int new_x = x + move[0];
                        int new_y = y + move[1];
                        if (0 <= new_x && new_x <= 8 && 0 <= new_y && new_y <= 9) {
                            Piece p = board.getPieceAt(new_x, new_y);
                            if (p == null || p.side != this.side) {
                                movesPositions.add(new Position(new_x, new_y));
                            }
                        }
                    }
                }
                break;
            }
        }
        return movesPositions;
    }
}

class Move {
    public Piece pieceToMove;
    public Position from, to;
    public Piece capturedPiece;

    public Move(Piece pieceToMove, Position to){
        this.pieceToMove = pieceToMove; this.from = pieceToMove.pos;
        this.to = to;
    }

    public Move(Piece pieceToMove, Position to, Piece capturedPiece){
        this.pieceToMove = pieceToMove; this.from = pieceToMove.pos;
        this.to = to; this.capturedPiece = capturedPiece;
    }

    public Move(Piece pieceToMove, Position from, Position to, Piece capturedPiece){
        this.pieceToMove = pieceToMove; this.from = from; this.to = to; this.capturedPiece = capturedPiece;
    }

    public Move(Position from, Position to, Piece capturedPiece) {
        this.from = from;
        this.to = to;
        this.capturedPiece = capturedPiece;
    }

    public Piece getPieceToMove() {
        return pieceToMove;
    }

    public void setPieceToMove(Piece pieceToMove) {
        this.pieceToMove = pieceToMove;
    }

    public Position getFrom() {
        return from;
    }

    public void setFrom(Position from) {
        this.from = from;
    }

    public Position getTo() {
        return to;
    }

    public void setTo(Position to) {
        this.to = to;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    public void setCapturedPiece(Piece capturedPiece) {
        this.capturedPiece = capturedPiece;
    }
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
        
        // Then check game status
        checkGameStatus();

        // If it's PVE mode and it's AI's turn, make AI move
        // Add a flag to prevent recursive AI moves
        if (gameMode == GameMode.PVE && currentPlayer == player2Side && !gameOver && !isProcessingAIMove) {
            makeAIMove();
        }
    }

    private void makeAIMove() {
        isProcessingAIMove = true;
        try {
            MinimaxResult result = Minimax.minimax(this, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, currentPlayer, true);
            if (result.move != null) {
                movePiece(result.move.pieceToMove, result.move.to);
            }
        } finally {
            isProcessingAIMove = false;
        }
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
        // Find the general
        Piece general = null;
        for (var piece : pieces) {
            if (piece.type == Type.GENERAL && piece.side == side) {
                general = piece;
                break;
            }
        }

        if (general == null) return false;

        // Debug log general position
        Gdx.app.log("DEBUG", "Checking if " + side + " General at (" +
            general.pos.x + "," + general.pos.y + ") is in check");

        // Check each opposing piece
        for (var piece : pieces) {
            if (piece.side != side) {
                ArrayList<Position> moves = piece.getPseudoValidMovesPositions(this);
                boolean canAttackGeneral = moves.contains(general.pos);

                // Debug log attacking pieces
                if (canAttackGeneral) {
                    Gdx.app.log("DEBUG", piece.side + " " + piece.type +
                        " at (" + piece.pos.x + "," + piece.pos.y +
                        ") can attack general");
                }

                if (canAttackGeneral) return true;
            }
        }

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
                Gdx.app.log("DEBUG", "Checkmate! Winner: " + winner);
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
