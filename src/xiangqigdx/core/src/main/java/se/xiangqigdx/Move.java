package se.xiangqigdx;

public class Move {
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
