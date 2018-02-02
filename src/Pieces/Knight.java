package Pieces;

import Game.*;

public class Knight extends Piece {

    public Knight(int x, int y, Player player, Board b) {
        super(x, y, player, b);
        this.type = PieceType.Knight;
        this.PieceImage = (player.getPlayerColor().equals(PlayerType.Black)) ? pi.BlackKnight : pi.WhiteKnight;
    }

    @Override
    public boolean isValidPath(int finalX, int finalY, Piece[][] board, boolean phantom,boolean IgnoreCheck) {
        boolean available;
        available = (((Math.abs(finalX - this.x) == 2) && (Math.abs(finalY - this.y) == 1))
                || ((Math.abs(finalX - this.x) == 1) && (Math.abs(finalY - this.y) == 2)));
        if (available) {
            if (board[finalY][finalX] != null) {
                if (this.player.getPlayerColor() == PlayerType.Black) {
                    return IsMoveLegal((board[finalY][finalX].getPlayer().getPlayerColor() == PlayerType.White), check);
                } else if (this.player.getPlayerColor() == PlayerType.White) {
                    return IsMoveLegal((board[finalY][finalX].getPlayer().getPlayerColor() == PlayerType.Black), check);
                }
            }
            return IsMoveLegal(true, check);
        }
        return false;
    }

}
