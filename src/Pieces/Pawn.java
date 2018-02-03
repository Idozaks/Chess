package Pieces;

import Game.*;

public class Pawn extends Piece {

    public Pawn(int x, int y, Player player, Board b) {
        super(x, y, player, b);
        this.type = PieceType.Pawn;
        this.PieceImage = (player.getPlayerColor().equals(PlayerType.Black)) ? pi.BlackPawn : pi.WhitePawn;
    }

    @Override
    public boolean isValidPath(int finalX, int finalY, Piece[][] board, boolean phantom) {
        if (this.player.getPlayerColor() == PlayerType.Black) {
            if ((Math.abs(finalX - x) == 1) && ((finalY - y) == 1)) { //one-space diagonall eating
                if (board[finalY][finalX] != null) {
                    if (board[finalY][finalX].getPlayer().getPlayerColor() == PlayerType.White) {
                        return true;
                    }
                }
            }
            if (y == 1) {
                if (finalX == x) {  //same column
                    if (((finalY - y) == 1) || (finalY - y) == 2) {
                        if (board[y + 1][finalX] == null) { //the pawn can only move two space vertically if it's his "first move"
                            return true;
                        }
                    }
                } else {
                    return false;
                }
            } else {
                if (finalX == x && (finalY - y) == 1) {
                    if (board[finalY][finalX] != null) {
                        if (!board[finalY][finalX].getPlayer().getPlayerColor().equals(this.player.getPlayerColor())) {
                            return false;
                        }
                    } else {
                        return true;
                    }
                }
            }
        } else if (this.player.getPlayerColor() == PlayerType.White) {
            if ((Math.abs(finalX - x) == 1) && ((finalY - y) == -1)) {
                if (board[finalY][finalX] != null) {
                    if (board[finalY][finalX].getPlayer().getPlayerColor() == PlayerType.Black) {
                        return true;
                    }
                }
            }
            if (y == 6) {
                if (finalX == x) {
                    if (((finalY - y) == -1) || (finalY - y) == -2) {
                        if (board[y - 1][finalX] == null) {
                            return true;
                        }
                    }
                } else {
                    return false;
                }
            } else {
                if (finalX == x && (finalY - y) == -1) {
                    if (board[finalY][finalX] != null) {
                        if (!board[finalY][finalX].getPlayer().getPlayerColor().equals(this.player.getPlayerColor())) {
                            return false;
                        }
                    } else {
                        return true;
                    }
                }
            }
        }

        return false;
    }

}
