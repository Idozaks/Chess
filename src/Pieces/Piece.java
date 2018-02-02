package Pieces;

import GUI.PiecesIcons.Icons;
import javax.swing.ImageIcon;
import Game.*;

/**
 * An Abstract class to guide inherited pieces. stores information about the
 * {@code Player} who "owns" it, its {@code PieceType}, and its image (selected
 * accordingly through {@code PieceIcon}).
 *
 * @author Ido
 * @see Player
 * @see Icons
 * @see PieceType
 */
public abstract class Piece {

    public int x, y;
    Player player;
    PieceType type;
    public Icons pi = new Icons();
    ImageIcon PieceImage;
    Board board;

    boolean MoveIsValid = false;
    public static boolean check;

//    boolean[][] possiblePaths = new boolean[8][8];
    /**
     * @param x the x location of the piece
     * @param y the y location of the piece
     * @param player the player object associated with the piece
     * @param b the piece's board
     */
    public Piece(int x, int y, Player player, Board b) {
        this.x = x;
        this.y = y;
        this.player = player;
        this.board = b;
        addThisPiece();
    }

    /**
     *
     * @return the Piece's ImageIcon
     */
    public ImageIcon getPieceImage() {
        return PieceImage;
    }

    /**
     * sets the piece's x and y parameters.
     *
     * @param x x location
     * @param y y location
     */
    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    private boolean addThisPiece() {
        return this.player.addPiece(this);
    }

    public PieceType getPieceType() {
        return type;
    }

    public Player getPlayer() {
        return player;
    }

    /**
     * returns whether the piece can move to the specified place
     *
     * @param finalX x destination
     * @param finalY y destination
     * @param board a two-dimensional array of {@code Piece}s
     * @param phantom a boolean that determines if the method calling is done by
     * another {@code Piece}, to see if that Piece is prohibited to go there, or
     * if it's done by the same Piece.
     * @param ignoreCheck
     * @return true if the path is valid
     */
    public abstract boolean isValidPath(int finalX, int finalY, Piece[][] board, boolean phantom, boolean ignoreCheck);

    /**
     * returns a boolean matrix to indicate where a piece can go
     *
     * @param board the current board matrix
     * @return a boolean[][] for possible paths
     */
    public boolean[][] drawPath(Piece[][] board) {
        boolean[][] possiblePaths = new boolean[8][8];
        for (int i = 0; i < possiblePaths.length; i++) {
            for (int j = 0; j < possiblePaths[i].length; j++) {
                if (this.isValidPath(j, i, board, false, true)) {
                    possiblePaths[i][j] = true;
                }
            }
        }
        return possiblePaths;
    }

    /**
     * Return a true or false bool depending on if the piece can move to the
     * location, and if the piece defends the king in the case of a check
     * condition.
     *
     * @param canMove the bool that states if the piece can move or not.
     * @param KingCheck the bool that state whether there is a check on the
     * board.
     * @return
     */
    public boolean IsMoveLegal(boolean canMove, boolean KingCheck) {
        if (canMove) {                  //meaning the piece can move to the "destination"
            if (KingCheck) {            // meaning - if there is some check-state on the board.
                Piece[][] tempBoard;    //temporary gameBoard used to test paths/moves.
                boolean[][] possibles = drawPath(this.board.gameBoard); //this piece's valid moves.
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        if (possibles[i][j]) {
                            tempBoard = Board.copyValueOfGameboard(board.gameBoard);

                            tempBoard[i][j] = this;
                            tempBoard[y][x] = null;
                            if (Board.JustCheckForCheck(tempBoard)) {
                                //meaning the attempted moves does not cancel the check.
                                return false;
                            }
                        }
                    }
                }
            } else {
                //if there is no check currently - the piece can move.
                return true;
            }
        }
        return false; //return false because the piece cannot move
    }

}
