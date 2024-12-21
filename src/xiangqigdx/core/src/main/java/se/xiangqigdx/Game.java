package se.xiangqigdx;

public class Game {
    // game in stdin stdout

    Board board;

    public Game(){
        board = new Board();
    }

    public static void printBoard(Board board){
        for (int y = 0; y <= 9; y++){
            for (int x = 0; x <= 8; x++){
                Piece p = board.getPieceAt(x, y);
                if (p != null)
                    System.out.print(""+p.side+"_"+p.type+" ");
                else System.out.print(" blank ");
            }
            System.out.print("\n");
        }
    }

    public static void main(String[] args){
        Game game = new Game();
        printBoard(game.board);
    }
}
