package se.xiangqigdx.android;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import se.xiangqigdx.Board;

public class HistoryEntry implements Serializable {
    public Board.Move move;
    public ArrayList<Board.Piece> boardState;

    public HistoryEntry(Board.Move move, ArrayList<Board.Piece> boardState) {
        this.move = move;
        this.boardState = boardState;
    }
    public byte[] toByteArray() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
