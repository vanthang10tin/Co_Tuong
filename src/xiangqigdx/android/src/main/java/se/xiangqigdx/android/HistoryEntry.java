package se.xiangqigdx.android;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import se.xiangqigdx.Move;
import se.xiangqigdx.Piece;

public class HistoryEntry implements Serializable {
    public Move move;
    public ArrayList<Piece> boardState;

    public HistoryEntry(Move move, ArrayList<Piece> boardState) {
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
