package Pieces;

import Game.*;

public class Rook extends Piece {

    public boolean hasMoved;

    final int UP = 1;
    final int RIGHT = 2;
    final int DOWN = 3;
    final int LEFT = 4;

    boolean upObstructing, rightObstructing,
            downObstructing, leftObstructing;

    public Rook(int x, int y, Player player, Board b) {
        super(x, y, player, b);
        this.type = PieceType.Rook;
        this.PieceImage = (player.getPlayerColor().equals(PlayerType.Black)) ? pi.BlackRook : pi.WhiteRook;
    }

    @Override
    public boolean isValidPath(int finalX, int finalY, Piece[][] board, boolean phantom, boolean IgnoreCheck) {
        int direction = 0;

        if (finalX == x && finalY != y) { // vertical
            direction = ((finalY - y) > 0) ? DOWN : UP;
        } else if (finalY == y && finalX != x) { // horizontal
            direction = ((finalX - x) > 0) ? RIGHT : LEFT;
        }
        boolean canOrNo = ((finalX == this.x && finalY != this.y) || (finalY == this.y && finalX != this.x));
        if (!canOrNo) {
            return canOrNo;
        }

        switch (direction) {

            case UP:
                for (int i = (y - 1); i >= finalY; i--) {
                    if (this.player.getPlayerColor() == PlayerType.White) {
                        if (board[i][finalX] != null) {
                            if (board[i][finalX].getPlayer().getPlayerColor() == PlayerType.White) {
                                return false;
                            } else if (board[i][finalX].getPlayer().getPlayerColor() == PlayerType.Black) {
                                MoveIsValid = (i == finalY);
                            }
                        } else {
                            MoveIsValid = true;
                        }
                    } else if (this.player.getPlayerColor() == PlayerType.Black) {
                        if (board[i][finalX] != null) {
                            if (board[i][finalX].getPlayer().getPlayerColor() == PlayerType.Black) {
                                return false;
                            } else if (board[i][finalX].getPlayer().getPlayerColor() == PlayerType.White) {
                                MoveIsValid = (i == finalY);
                            }
                        } else {
                            MoveIsValid = true;
                        }
                    }
                }
                break;

            case DOWN:
                for (int i = (y + 1); i <= finalY; i++) {
                    if (this.player.getPlayerColor() == PlayerType.White) {
                        if (board[i][finalX] != null) {
                            if (board[i][finalX].getPlayer().getPlayerColor() == PlayerType.White) {
                                return false;
                            } else if (board[i][finalX].getPlayer().getPlayerColor() == PlayerType.Black) {
                                MoveIsValid = (i == finalY);
                            }
                        } else {
                            MoveIsValid = true;
                        }
                    } else if (this.player.getPlayerColor() == PlayerType.Black) {
                        if (board[i][finalX] != null) {
                            if (board[i][finalX].getPlayer().getPlayerColor() == PlayerType.Black) {
                                return false;
                            } else if (board[i][finalX].getPlayer().getPlayerColor() == PlayerType.White) {
                                MoveIsValid = (i == finalY);
                            }
                        } else {
                            MoveIsValid = true;
                        }
                    }

                }
                break;

            case RIGHT:
                for (int i = (x + 1); i <= finalX; i++) {
                    if (this.player.getPlayerColor() == PlayerType.White) {
                        if (board[finalY][i] != null) {
                            if (board[finalY][i].getPlayer().getPlayerColor() == PlayerType.White) {
                                return false;
                            } else if (board[finalY][i].getPlayer().getPlayerColor() == PlayerType.Black) {
                                MoveIsValid = (i == finalX);
                            }
                        } else {
                            MoveIsValid = true;
                        }
                    } else if (this.player.getPlayerColor() == PlayerType.Black) {
                        if (board[finalY][i] != null) {
                            if (board[finalY][i].getPlayer().getPlayerColor() == PlayerType.Black) {
                                return false;
                            } else if (board[finalY][i].getPlayer().getPlayerColor() == PlayerType.White) {
                                MoveIsValid = (i == finalX);
                            }
                        } else {
                            MoveIsValid = true;
                        }
                    }
                }
                break;

            case LEFT:
                for (int i = (x - 1); i >= finalX; i--) {
                    if (this.player.getPlayerColor() == PlayerType.White) {
                        if (board[finalY][i] != null) {
                            if (board[finalY][i].getPlayer().getPlayerColor() == PlayerType.White) {
                                return false;
                            } else if (board[finalY][i].getPlayer().getPlayerColor() == PlayerType.Black) {
                                MoveIsValid = (i == finalX);
                            }
                        } else {
                            MoveIsValid = true;
                        }
                    } else if (this.player.getPlayerColor() == PlayerType.Black) {
                        if (board[finalY][i] != null) {
                            if (board[finalY][i].getPlayer().getPlayerColor() == PlayerType.Black) {
                                return false;
                            } else if (board[finalY][i].getPlayer().getPlayerColor() == PlayerType.White) {
                                MoveIsValid = (i == finalX);
                            }
                        } else {
                            MoveIsValid = true;
                        }
                    }
                }
                break;
        }
        if (!IgnoreCheck) {
            if (MoveIsValid) {
                return IsMoveLegal(true, check);
            } else {
                return MoveIsValid;
            }
        }
        return MoveIsValid;
    }

}
