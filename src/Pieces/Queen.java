package Pieces;

import Game.*;

public class Queen extends Piece {

    final int UP_RIGHT = 1, DOWN_RIGHT = 2,
            DOWN_LEFT = 3, UP_LEFT = 9;
    final int UP = 5, RIGHT = 4,
            DOWN = 6, LEFT = 8;

    public Queen(int x, int y, Player player, Board b) {
        super(x, y, player, b);
        this.type = PieceType.Queen;
        this.PieceImage = (player.getPlayerColor().equals(PlayerType.Black)) ? pi.BlackQueen : pi.WhiteQueen;
    }

    @Override
    public boolean isValidPath(int finalX, int finalY, Piece[][] board, boolean phantom) {
        int direction = 0;
        if (finalX == x && finalY != y) { // vertical
            direction = ((finalY - y) > 0) ? DOWN : UP;
        } else if (finalY == y && finalX != x) { // horizontal
            direction = ((finalX - x) > 0) ? RIGHT : LEFT;
        } else if ((finalY - y) < 0) { // up 
            direction = ((finalX - x) > 0) ? UP_RIGHT : UP_LEFT;
        } else if (((finalY - y) > 0)) { // down
            direction = ((finalX - x) > 0) ? DOWN_RIGHT : DOWN_LEFT;
        }
        boolean canOrNo;
        int diff_x = Math.abs(finalX - x);
        int diff_y = Math.abs(finalY - y);
        if (finalX != x && finalY != y) {
            canOrNo = (diff_x == diff_y);
        } else if ((finalX == this.x && finalY != this.y) || (finalY == this.y && finalX != this.x)) {
            canOrNo = true;
        } else {
            canOrNo = false;
        }
        if (!canOrNo) {
            return false;
        } 
        switch (direction) {
            case UP:
                for (int i = (y - 1); i >= finalY; i--) {
                    if (this.player.getPlayerColor() == PlayerType.White) {
                        if (board[i][finalX] != null) {
                            if (board[i][finalX].getPlayer().getPlayerColor() == PlayerType.White) {
                                return false;
                            } else if (board[i][finalX].getPlayer().getPlayerColor() == PlayerType.Black) {
                                return i == finalY; 

                            }
                        }
                    } else if (this.player.getPlayerColor() == PlayerType.Black) {
                        if (board[i][finalX] != null) {
                            if (board[i][finalX].getPlayer().getPlayerColor() == PlayerType.Black) {
                                return false;
                            } else if (board[i][finalX].getPlayer().getPlayerColor() == PlayerType.White) {
                                return i == finalY; 

                            }
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
                                return i == finalY; 

                            }
                        }
                    } else if (this.player.getPlayerColor() == PlayerType.Black) {
                        if (board[i][finalX] != null) {
                            if (board[i][finalX].getPlayer().getPlayerColor() == PlayerType.Black) {
                                return false;
                            } else if (board[i][finalX].getPlayer().getPlayerColor() == PlayerType.White) {
                                return i == finalY; 

                            }
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
                                return i == finalX; 

                            }
                        }
                    } else if (this.player.getPlayerColor() == PlayerType.Black) {
                        if (board[finalY][i] != null) {
                            if (board[finalY][i].getPlayer().getPlayerColor() == PlayerType.Black) {
                                return false;
                            } else if (board[finalY][i].getPlayer().getPlayerColor() == PlayerType.White) {
                                return i == finalX; 

                            }
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
                                return i == finalX; 

                            }
                        }
                    } else if (this.player.getPlayerColor() == PlayerType.Black) {
                        if (board[finalY][i] != null) {
                            if (board[finalY][i].getPlayer().getPlayerColor() == PlayerType.Black) {
                                return false;
                            } else if (board[finalY][i].getPlayer().getPlayerColor() == PlayerType.White) {
                                return i == finalX; 

                            }
                        }
                    }
                }

                break;
            case UP_RIGHT:
                for (int i = (y - 1); i >= finalY;) { 
                    for (int j = (x + 1); j <= finalX; j++, i--) { 
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
                for (int i = (y - 1); i >= finalY;) { 
                    for (int j = (x - 1); j >= finalX; j--, i--) { 
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
                for (int i = (y + 1); i <= finalY;) { 
                    for (int j = (x + 1); j <= finalX; j++, i++) { 
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
                for (int i = (y + 1); i <= finalY;) { 
                    for (int j = (x - 1); j >= finalX; j--, i++) { 
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
