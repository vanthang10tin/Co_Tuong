package se.xiangqigdx;

import java.util.ArrayList;

public class Piece {
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
                        else if (p.side != this.side)
                            movesPositions.add(new Position(new_x, new_y));
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
                        else if (p.side != this.side)
                            movesPositions.add(new Position(new_x, new_y));

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
                        else if (p.side != this.side)
                            movesPositions.add(new Position(new_x, new_y));
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
