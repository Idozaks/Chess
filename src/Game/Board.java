/*********************************************************************************************************************\
|#       /  |      /                          /               _  |                       |   |                   |   #|
|#      (   | ___ (  ___  ___  _ _  ___      (___  ___      (    |___  ___  ___  ___     |___| ___  ___  ___  ___|   #|
|#      | / )|___)| |    |   )| | )|___)     |    |   )     |   )|   )|___)|___ |___     |   )|   )|   )|   )|   )   #|
|#      |/|/ |__  | |__  |__/ |  / |__       |__  |__/      |__/ |  / |__   __/  __/     |__/ |__/ |__/||    |__/    #|
|#                                                                                    •                              #|
\*********************************************************************************************************************/

/**
FOR CLASS DOCUMENTATION EXPLAINED BY CHATGPT:
                                              https://pastebin.com/GW8DEHRk
**/

package Game;

import Pieces.*;
import java.awt.Component;

/**
 * Stores {@code Pieces}, populates itself with the appropriate pieces for a
 * starting state board.
 *
 * @author Ido
 * @see Piece
 */
public class Board {

    public Piece[][] gameBoard;

    public Board(BlackPlayer bp, WhitePlayer wp) {
        gameBoard = new Piece[8][8];
        PopulateGameBoard(this, bp, wp);
    }

    /**
     * Copies a {@code Piece[][]}
     *
     * @param board the board you want to copy from
     * @return a copy of the inputted board
     */
    public static Piece[][] copyValueOfGameboard(Piece[][] board) {
        Piece[][] newBoard = new Piece[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                newBoard[i][j] = board[i][j];
            }
        }
        return newBoard;
    }

    /**
     * A method to determine whether a piece has no possible moves. primarily
     * used for check-mate checking.
     *
     * @param KingY the King's y location on the board
     * @param KingX the King's x location on the board
     * @return true if the piece has no possible moves. otherwise returns false.
     */
    public boolean PieceCannotMove(int KingY, int KingX) {
        boolean[][] possibles = gameBoard[KingY][KingX].drawPath(gameBoard);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (possibles[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * checks whether no piece on the board can protect the threatened king.
     *
     * @param KingY the King's y location on the board
     * @param KingX the King's x location on the board
     * @return true if there is a piece that can defend the king.
     */
    public boolean SomePieceCanDefend(int KingY, int KingX) {
        //run through the board
        //set temp Piece
        //set temp boolean[][] of Piece's move possibilites "possibles"
        /*check if any places that the piece would be in "possibles" would 
         eliminate the checkMate bool*/
        //if NO check returns true - return the method as true

        Piece[][] tempBoard;    // = Board.copyValueOfGameboard(gameBoard);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                // int i & j represent the location of the "defending" piece
                if (gameBoard[i][j] != null) {
                    Piece defendingPiece = gameBoard[i][j];
                    boolean[][] possibles = gameBoard[i][j].drawPath(gameBoard);
                    for (int y = 0; y < 8; y++) {
                        for (int x = 0; x < 8; x++) { // y & x are for checking available moves.
                            if (possibles[y][x]) {
                                tempBoard = Board.copyValueOfGameboard(gameBoard);

                                tempBoard[y][x] = tempBoard[i][j];
                                tempBoard[i][j] = null;
                                if (!JustCheckForCheck(tempBoard)) { //meaning some piece has prevented the check
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Used to test if a gameBoard contains a check condition.
     *
     * @param testedBoard the board which you want to test for check.
     * @return true if testBoard contains a check in it.
     */
    public static boolean JustCheckForCheck(Piece[][] testedBoard) {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (testedBoard[y][x] != null && testedBoard[y][x].getPieceType().equals(PieceType.King)) {
                    for (int i = 0; i < 8; i++) {
                        for (int j = 0; j < 8; j++) {
                            if (testedBoard[i][j] != null && testedBoard[i][j].isValidPath(x, y, testedBoard, false)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Converts a one dimensional array of Components to a matrix and returns
     * the matrix.
     *
     * @param components one dimensional array of components
     * @return a matrix of Components based on the input only if components has
     * a size of 64
     * @see Component
     */
    public static Component[][] ArrayToMatrix(Component[] components) {
        if (components.length == 64) {
            Component[][] componentsesMatrix = new Component[8][8];
            for (int i = 0; i < componentsesMatrix.length; i++) {
                for (int j = 0; j < componentsesMatrix[i].length; j++) {
                    componentsesMatrix[i][j] = components[(i * componentsesMatrix[i].length) + j];
                }
            }
            return componentsesMatrix;
        } else {
            return null;
        }
    }

    /**
     * Used by the constructor to set the pieces for the starting position
     *
     * @param board the board you want to populate 
     * @param bp the black player object
     * @param wp the white player object
     * @return the populated gameBoard
     */
    public static Piece[][] PopulateGameBoard(Board board, BlackPlayer bp, WhitePlayer wp) {
        board.gameBoard[0][0] = new Rook(0, 0, bp, board);
        board.gameBoard[0][1] = new Knight(1, 0, bp, board);
        board.gameBoard[0][2] = new Bishop(2, 0, bp, board);
        board.gameBoard[0][3] = new Queen(3, 0, bp, board);
        board.gameBoard[0][4] = new King(4, 0, bp, board);
        board.gameBoard[0][5] = new Bishop(5, 0, bp, board);
        board.gameBoard[0][6] = new Knight(6, 0, bp, board);
        board.gameBoard[0][7] = new Rook(7, 0, bp, board);
        for (int j = 0; j < board.gameBoard[1].length; j++) {
            board.gameBoard[1][j] = new Pawn(j, 1, bp, board);
        }

        board.gameBoard[7][0] = new Rook(0, 7, wp, board);
        board.gameBoard[7][1] = new Knight(1, 7, wp, board);
        board.gameBoard[7][2] = new Bishop(2, 7, wp, board);
        board.gameBoard[7][3] = new Queen(3, 7, wp, board);
        board.gameBoard[7][4] = new King(4, 7, wp, board);
        board.gameBoard[7][5] = new Bishop(5, 7, wp, board);
        board.gameBoard[7][6] = new Knight(6, 7, wp, board);
        board.gameBoard[7][7] = new Rook(7, 7, wp, board);

        for (int j = 0; j < board.gameBoard[1].length; j++) {
            board.gameBoard[6][j] = new Pawn(j, 6, wp, board);
        }
        return board.gameBoard;
    }

}
