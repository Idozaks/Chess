package Pieces;

import Game.*;

public class Bishop extends Piece {

    final int UP_RIGHT = 1, DOWN_RIGHT = 2,
            DOWN_LEFT = 3, UP_LEFT = 4;

    public Bishop(int x, int y, Player player, Board b) {
        super(x, y, player, b);
        this.type = PieceType.Bishop;
        this.PieceImage = (player.getPlayerColor().equals(PlayerType.Black)) ? pi.BlackBishop : pi.WhiteBishop;
    }

    @Override
    public boolean isValidPath(int finalX, int finalY, Piece[][] board, boolean phantom) {
        int direction = 0;

        if ((finalY - y) < 0) { // up 
            direction = ((finalX - x) > 0) ? UP_RIGHT : UP_LEFT;
        } else if (((finalY - y) > 0)) { // down
            direction = ((finalX - x) > 0) ? DOWN_RIGHT : DOWN_LEFT;
        }

        boolean canOrNo;
        int diff_x = Math.abs(finalX - x);
        int diff_y = Math.abs(finalY - y);
        if (finalX != x && finalY != y) {
            canOrNo = (diff_x == diff_y);
        } else {
            canOrNo = false;
        }
        if (!canOrNo) {
            return canOrNo;
        }
        switch (direction) {
            case UP_RIGHT:
                for (int i = (y - 1); i >= finalY;) { //UP part
                    for (int j = (x + 1); j <= finalX; j++, i--) { //RIGHT part
                        if (this.player.getPlayerColor() == PlayerType.White) {
                            if (board[i][j] != null) {
                                if (board[i][j].getPlayer().getPlayerColor() == PlayerType.White) {
                                    return false;
                                } else if (board[i][j].getPlayer().getPlayerColor() == PlayerType.Black) {
                                    return (i == finalY && j == finalX);
                                }
                            }
                        } else if (this.player.getPlayerColor() == PlayerType.Black) {
                            if (board[i][j] != null) {
                                if (board[i][j].getPlayer().getPlayerColor() == PlayerType.Black) {
                                    return false;
                                } else if (board[i][j].getPlayer().getPlayerColor() == PlayerType.White) {
                                    return (i == finalY && j == finalX);
                                }
                            }
                        }

                    }
                }
                break;

            case UP_LEFT:
                for (int i = (y - 1); i >= finalY;) { //UP part
                    for (int j = (x - 1); j >= finalX; j--, i--) { //LEFT part
                        if (this.player.getPlayerColor() == PlayerType.White) {
                            if (board[i][j] != null) {
                                if (board[i][j].getPlayer().getPlayerColor() == PlayerType.White) {
                                    return false;
                                } else if (board[i][j].getPlayer().getPlayerColor() == PlayerType.Black) {
                                    return (i == finalY && j == finalX);
                                }
                            }
                        } else if (this.player.getPlayerColor() == PlayerType.Black) {
                            if (board[i][j] != null) {
                                if (board[i][j].getPlayer().getPlayerColor() == PlayerType.Black) {
                                    return false;
                                } else if (board[i][j].getPlayer().getPlayerColor() == PlayerType.White) {
                                    return (i == finalY && j == finalX);
                                }
                            }
                        }

                    }
                }

                break;

            case DOWN_RIGHT:
                for (int i = (y + 1); i <= finalY;) { //DOWN part
                    for (int j = (x + 1); j <= finalX; j++, i++) { //RIGHT part
                        if (this.player.getPlayerColor() == PlayerType.White) {
                            if (board[i][j] != null) {
                                if (board[i][j].getPlayer().getPlayerColor() == PlayerType.White) {
                                    return false;
                                } else if (board[i][j].getPlayer().getPlayerColor() == PlayerType.Black) {
                                    return (i == finalY && j == finalX);

                                }
                            }
                        } else if (this.player.getPlayerColor() == PlayerType.Black) {
                            if (board[i][j] != null) {
                                if (board[i][j].getPlayer().getPlayerColor() == PlayerType.Black) {
                                    return false;
                                } else if (board[i][j].getPlayer().getPlayerColor() == PlayerType.White) {
                                    return (i == finalY && j == finalX);

                                }
                            }
                        }
                    }
                }
                break;

            case DOWN_LEFT:
                for (int i = (y + 1); i <= finalY;) { //DOWN part
                    for (int j = (x - 1); j >= finalX; j--, i++) { //LEFT part
                        if (this.player.getPlayerColor() == PlayerType.White) {
                            if (board[i][j] != null) {
                                if (board[i][j].getPlayer().getPlayerColor() == PlayerType.White) {
                                    return false;
                                } else if (board[i][j].getPlayer().getPlayerColor() == PlayerType.Black) {
                                    return (i == finalY && j == finalX);

                                }
                            }
                        } else if (this.player.getPlayerColor() == PlayerType.Black) {
                            if (board[i][j] != null) {
                                if (board[i][j].getPlayer().getPlayerColor() == PlayerType.Black) {
                                    return false;
                                } else if (board[i][j].getPlayer().getPlayerColor() == PlayerType.White) {
                                    return (i == finalY && j == finalX);
                                }
                            } 
                        }
                        
                    }
                }
                break;

        }
        return true;
    }

}
