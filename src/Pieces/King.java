package Pieces;

import Game.*;

public class King extends Piece {

    public boolean hasMoved;

    public King(int x, int y, Player player, Board b) {
        super(x, y, player, b);
        this.type = PieceType.King;
        this.PieceImage = (player.getPlayerColor().equals(PlayerType.Black)) ? pi.BlackKing : pi.WhiteKing;
    }

    @Override
    public boolean isValidPath(int finalX, int finalY, Piece[][] board, boolean phantom) {

        Piece[][] movedKingBoard = Board.copyValueOfGameboard(board);

        if (!phantom) {
            if (this.isValidPath(finalX, finalY, board, true)) {
                movedKingBoard[finalY][finalX] = null;
                movedKingBoard[finalY][finalX] = this;
                movedKingBoard[y][x] = null;
            }
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (movedKingBoard[i][j] != null) {
                        if (!movedKingBoard[i][j].getPieceType().equals(PieceType.King) && movedKingBoard[i][j] != null) {
                            boolean pathForEach = movedKingBoard[i][j].isValidPath(finalX, finalY, movedKingBoard, true);
                            if (!movedKingBoard[i][j].getPlayer().getPlayerColor().equals(this.player.getPlayerColor()) && pathForEach) {
                                //the statement means that if a piece, of the opposite color, can go
                                 //to the tile where the king is planning to go - that possibility is false.
                                
                                return false;
                            }
                        }
                    }
                }
            }
        }

        if (Math.abs(finalX - this.x) == 1 && Math.abs(finalY - this.y) == 1) {
            if (board[finalY][finalX] != null) {
                if (this.player.getPlayerColor() == PlayerType.Black) {
                    return board[finalY][finalX].getPlayer().getPlayerColor() == PlayerType.White;
                } else if (this.player.getPlayerColor() == PlayerType.White) {
                    return board[finalY][finalX].getPlayer().getPlayerColor() == PlayerType.Black;
                }
            }
            return true;
        } else if (Math.abs(finalX - this.x) == 1 && (finalY == this.y)) {
            if (board[finalY][finalX] != null) {
                if (this.player.getPlayerColor() == PlayerType.Black) {
                    return board[finalY][finalX].getPlayer().getPlayerColor() == PlayerType.White;
                } else if (this.player.getPlayerColor() == PlayerType.White) {
                    return board[finalY][finalX].getPlayer().getPlayerColor() == PlayerType.Black;
                }

            }
            return true;
        } else if (Math.abs(finalY - this.y) == 1 && (finalX == this.x)) {
            if (board[finalY][finalX] != null) {
                if (this.player.getPlayerColor() == PlayerType.Black) {
                    return board[finalY][finalX].getPlayer().getPlayerColor() == PlayerType.White;
                } else if (this.player.getPlayerColor() == PlayerType.White) {
                    return board[finalY][finalX].getPlayer().getPlayerColor() == PlayerType.Black;
                }
            }
            return true;
        }
        return false;
    }

}
