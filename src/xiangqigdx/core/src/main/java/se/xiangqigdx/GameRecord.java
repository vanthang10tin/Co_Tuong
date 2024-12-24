package se.xiangqigdx;

public class GameRecord {
    public int id;
    public String date;
    public GameMode gameMode;
    public Side winner;
    public String moves;
    public Side player1Side;
    public boolean isStalemate;

    @Override
    public String toString() {
        return date + " - " + 
               gameMode + " - " + 
               (isStalemate ? "Stalemate" : (winner != null ? winner + " wins" : "Ongoing"));
    }
}
