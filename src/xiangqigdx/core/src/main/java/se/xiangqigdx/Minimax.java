package se.xiangqigdx;

import java.util.ArrayList;
import java.util.EnumMap;

class MinimaxResult {
    Move move;
    int score;

    public MinimaxResult(Move move, int score) {
        this.move = move;
        this.score = score;
    }
}

class ValueEvaluation {
    // for Red side
    static int[][] chariotPositionValues = {
        {14, 14, 12, 18, 16, 18, 12, 14, 14},
        {16, 20, 18, 24, 26, 24, 18, 20, 26},
        {12, 12, 12, 18, 18, 18, 12, 12, 12},
        {12, 18, 16, 22, 22, 22, 16, 18, 12},
        {12, 14, 12, 18, 18, 18, 12, 14, 12},
        {12, 16, 14, 20, 20, 20, 14, 16, 12},
        {6, 10, 8, 14, 14, 14, 8, 10, 6},
        {4, 8, 6, 14, 12, 14, 6, 8, 4},
        {8, 4, 8, 16, 8, 16, 8, 4, 8},
        {-2, 10, 6, 14, 12, 14, 6, 10, -2}
    };
    static int[][] horsePositionValues = {
        {4, 8, 16, 12, 4, 12, 16, 8, 4},
        {4, 10, 28, 16, 8, 16, 28, 10, 4},
        {12, 14, 16, 20, 18, 20, 16, 14, 12},
        {8, 24, 18, 24, 20, 24, 18, 24, 8},
        {6, 16, 14, 18, 16, 18, 14, 16, 6},
        {4, 12, 16, 14, 12, 14, 16, 12, 4},
        {2, 6, 8, 6, 10, 6, 8, 6, 2},
        {4, 2, 8, 8, 4, 8, 8, 2, 4},
        {0, 2, 4, 4, -2, 4, 4, 2, 0},
        {0, -4, 0, 0, 0, 0, 0, -4, 0}
    };
    static int[][] cannonPositionValues = {
        {6, 4, 0, -10, -12, -10, 0, 4, 6},
        {2, 2, 0, -4, -14, -4, 0, 2, 2},
        {2, 2, 0, -10, -8, -10, 0, 2, 2},
        {0, 0, -2, 4, 10, 4, -2, 0, 0},
        {0, 0, 0, 2, 8, 2, 0, 0, 0},
        {-2, 0, 4, 2, 6, 2, 4, 0, -2},
        {0, 0, 0, 2, 4, 2, 0, 0, 0},
        {4, 0, 8, 6, 10, 6, 8, 0, 4},
        {0, 2, 4, 6, 6, 6, 4, 2, 0},
        {0, 0, 2, 6, 6, 6, 2, 0, 0}
    };
    static int[][] soldierPositionValues = {
        {0, 3, 6, 9, 12, 9, 6, 3, 0},
        {18, 36, 56, 80, 120, 80, 56, 36, 18},
        {14, 26, 42, 60, 80, 60, 42, 26, 14},
        {10, 20, 30, 34, 40, 34, 30, 20, 10},
        {6, 12, 18, 18, 20, 18, 18, 12, 6},
        {2, 0, 8, 0, 8, 0, 8, 0, 2},
        {0, 0, -2, 0, 4, 0, -2, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0}
    };

    static EnumMap<Type, Integer> initPieceValue(){
        EnumMap<Type, Integer> values = new EnumMap<>(Type.class);
        values.put(Type.GENERAL, 6000);
        values.put(Type.ADVISOR, 120);
        values.put(Type.ELEPHANT, 120);
        values.put(Type.CHARIOT, 600);
        values.put(Type.HORSE, 270);
        values.put(Type.CANNON, 285);
        values.put(Type.SOLDIER, 30);
        return values;
    }
    static final EnumMap<Type, Integer> pieceValue = initPieceValue();

}

public class Minimax {
    static int DEPTH_LIMIT = 3;

    public static int evaluateSide(Board board, Side side){
        int score = 0;
        for (var piece : board.pieces){
            if (piece.side == side){
                score += ValueEvaluation.pieceValue.get(piece.type);
                int x = piece.pos.x, y = piece.pos.y;
                if (side == Side.BLACK){
                    x = 8 - x; y = 9 - y;
                }
                switch(piece.type){
                    case CHARIOT:
                        score += ValueEvaluation.chariotPositionValues[y][x];
                        break;
                    case HORSE:
                        score += ValueEvaluation.horsePositionValues[y][x];
                        break;
                    case CANNON:
                        score += ValueEvaluation.cannonPositionValues[y][x];
                        break;
                    case SOLDIER:
                        score += ValueEvaluation.soldierPositionValues[y][x];
                }
            }
        }

        return score;
    }

    public static int evaluateBoard(Board board, Side maximizingSide) {
        return evaluateSide(board, maximizingSide) - evaluateSide(board, maximizingSide == Side.RED ? Side.BLACK : Side.RED);
    }

    public static MinimaxResult minimax(Board board, int depth, int alpha, int beta, Side maximizingSide, boolean isMaximizingSide) {
        if (depth == DEPTH_LIMIT || board.isGameOver()) {
            int score = evaluateBoard(board, maximizingSide);
            return new MinimaxResult(null, score);
        }

        // Create a copy of pieces to avoid concurrent modification
        ArrayList<Piece> piecesToConsider = new ArrayList<>(board.pieces);

        if (isMaximizingSide) {
            int maxScore = Integer.MIN_VALUE;
            Move bestMove = null;
            outerMax:
            for (Piece piece : piecesToConsider) {
                if (piece.side == maximizingSide) {
                    ArrayList<Position> moves = new ArrayList<>(board.getValidMovesPositions(piece));
                    for (Position move : moves) {
                        // Create a copy of the board state
                        Position originalPos = new Position(piece.pos.x, piece.pos.y);
                        Piece capturedPiece = board.getPieceAt(move);
                        
                        // Make move
                        if (capturedPiece != null) {
                            board.pieces.remove(capturedPiece);
                        }
                        piece.pos = move;
                        
                        int score = minimax(board, depth + 1, alpha, beta, maximizingSide, false).score;
                        
                        // Restore board state
                        piece.pos = originalPos;
                        if (capturedPiece != null) {
                            board.pieces.add(capturedPiece);
                        }
                        
                        if (score > maxScore) {
                            maxScore = score;
                            bestMove = new Move(piece, move);
                        }
                        alpha = Math.max(alpha, score);
                        if (beta <= alpha) break outerMax;
                    }
                }
            }

            if (depth == 0) {
                if (bestMove == null) {
                    return new MinimaxResult(null, Integer.MIN_VALUE);
                }
                return new MinimaxResult(bestMove, maxScore);
            }
            return new MinimaxResult(null, maxScore);
        } else {
            int minScore = Integer.MAX_VALUE;
            outerMin:
            for (Piece piece : piecesToConsider) {
                if (piece.side != maximizingSide) {
                    ArrayList<Position> moves = new ArrayList<>(board.getValidMovesPositions(piece));
                    for (Position move : moves) {
                        // Create a copy of the board state
                        Position originalPos = new Position(piece.pos.x, piece.pos.y);
                        Piece capturedPiece = board.getPieceAt(move);
                        
                        // Make move
                        if (capturedPiece != null) {
                            board.pieces.remove(capturedPiece);
                        }
                        piece.pos = move;
                        
                        int score = minimax(board, depth + 1, alpha, beta, maximizingSide, true).score;
                        
                        // Restore board state
                        piece.pos = originalPos;
                        if (capturedPiece != null) {
                            board.pieces.add(capturedPiece);
                        }
                        
                        minScore = Math.min(minScore, score);
                        beta = Math.min(beta, score);
                        if (beta <= alpha) break outerMin;
                    }
                }
            }
            return new MinimaxResult(null, minScore);
        }
    }

}