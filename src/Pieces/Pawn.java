package Pieces;

import Game.*;

public class Pawn extends Piece {

    public Pawn(int x, int y, Player player, Board b) {
        super(x, y, player, b);
        this.type = PieceType.Pawn;
        this.PieceImage = (player.getPlayerColor().equals(PlayerType.Black)) ? pi.BlackPawn : pi.WhitePawn;
    }

    @Override
    public boolean isValidPath(int finalX, int finalY, Piece[][] board, boolean phantom,boolean IgnoreCheck) {
        if (this.player.getPlayerColor() == PlayerType.Black) {
            if ((Math.abs(finalX - x) == 1) && ((finalY - y) == 1)) {
                if (board[finalY][finalX] != null) {
                    if (board[finalY][finalX].getPlayer().getPlayerColor() == PlayerType.White) {
                        return IsMoveLegal(true, check);
                    }
                }
            }
            if (y == 1) {
                if (finalX == x) {
                    if (((finalY - y) == 1) || (finalY - y) == 2) {
                        if (board[y + 1][finalX] == null) {
                            return IsMoveLegal((board[finalY][finalX] == null), check);
                        }
                    }
                } else {
                    return false;
                }
            } else {
                if (finalX == x && (finalY - y) == 1) {
                    return IsMoveLegal((board[finalY][finalX] == null), check);
                }
            }
        } else if (this.player.getPlayerColor() == PlayerType.White) {
            if ((Math.abs(finalX - x) == 1) && ((finalY - y) == -1)) {
                if (board[finalY][finalX] != null) {
                    if (board[finalY][finalX].getPlayer().getPlayerColor() == PlayerType.Black) {
                        return IsMoveLegal(true, check);
                    }
                }
            }
            if (y == 6) {
                if (finalX == x) {
                    if (((finalY - y) == -1) || (finalY - y) == -2) {
                        if (board[y - 1][finalX] == null) {
                            return IsMoveLegal((board[finalY][finalX] == null), check);
                        }
                    }
                } else {
                    return false;
                }
            } else {
                if (finalX == x && (finalY - y) == -1) {
                    return IsMoveLegal((board[finalY][finalX] == null), check);
                }
            }
        }

        return false;
    }

}
