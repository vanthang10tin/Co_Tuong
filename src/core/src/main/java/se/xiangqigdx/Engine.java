package se.xiangqigdx;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Engine {
    Side side;

    public Engine(Side side) {
        if (!side.equals(Side.BLACK) && !side.equals(Side.RED)) {
            throw new IllegalArgumentException("Invalid color");
        }
        this.side = side;
    }

    public ArrayList<Move> findMoves(Board board) {
        if (board.gameOver) {
            return null;
        }
        if (!board.currentPlayer.equals(this.side)) {
            return null;
        }

        ArrayList<Piece> aiPieces = new ArrayList<>();
        for (Piece piece : board.pieces) {
            if (piece.side.equals(this.side)) {
                aiPieces.add(piece);
            }
        }

        ArrayList<Move> aiMoves = new ArrayList<>();
        for (Piece piece : aiPieces) {
            List<Position> validMoves = board.getValidMovesPositions(piece);
            for (Position move : validMoves) {
                aiMoves.add(new Move(piece, move));
            }
        }

        return aiMoves;
    }
}

class RandomEngine extends Engine {
    private Random random;

    public RandomEngine(Side side) {
        super(side);
        this.random = new Random();
    }

    public void move(Board board) {
        ArrayList<Move> aiMoves = findMoves(board);
        if (aiMoves == null || aiMoves.isEmpty()) {
            board.setGameOver(true);
            board.setWinner(this.side.equals(Side.BLACK) ? Side.RED : Side.BLACK);
            return;
        }

        Move selectedMove = aiMoves.get(random.nextInt(aiMoves.size()));
        Piece selectedPiece = selectedMove.getPieceToMove();
        Position selectedPosition = selectedMove.getTo();

        board.setLastMove(selectedMove);
        board.movePiece(selectedPiece, selectedPosition);
        board.switchPlayer();

        // Check for endgame conditions
        if (board.isCheckmate()) {
            board.setGameOver(true);
            board.setWinner(this.side);
        } else if (board.checkStalemate()) {
            board.setGameOver(true);
            board.isStalemate = true;
        }
    }
}

class MinimaxEngine extends Engine {
    public MinimaxEngine(Side side) {
        super(side);
        System.out.println("Initializing strong AI");
    }

    public void move(Board board) {
        System.out.println("Starting to find a move using Minimax");
        Move bestMove = findBestMove(board);

        if (bestMove == null) {
            System.out.println("No valid moves found");
            board.setGameOver(true);
            board.setWinner(this.side.equals(Side.BLACK) ? Side.RED : Side.BLACK);
            return;
        }

        Piece selectedPiece = bestMove.getPieceToMove();
        Position selectedPosition = bestMove.getTo();

        board.setLastMove(bestMove);
        board.movePiece(selectedPiece, selectedPosition);
        board.switchPlayer();

        // Check for endgame conditions
        if (board.isCheckmate()) {
            board.setGameOver(true);
            board.setWinner(this.side);
        } else if (board.isStalemate()) {
            board.setGameOver(true);
            board.setStalemate(true);
        }
    }

    private Move findBestMove(Board board) {
        if (board.isGameOver() || !board.getCurrentPlayer().equals(this.side)) {
            return null;
        }
        System.out.println("Calculating Minimax");
        return Minimax.minimax(board, 0, -10000, 10000, Side.BLACK, true).move;
    }
}
