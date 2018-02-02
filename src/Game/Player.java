package Game;

import Pieces.Piece;
/**
 * An abstract class with and empty {@code PlayerType} field.
 * @see PlayerType
 * @author Ido
 */
public abstract class Player {
    
    
    private final PlayerType playerColor;
    /**
     * 
     * @param playerTurn the color of the player
     */
    public Player(PlayerType playerTurn){
        playerColor = playerTurn;
    }

    public PlayerType getPlayerColor() {
        return playerColor;
    }
    
    /**
     *  Adds a Piece to the Player's ArrayList of Pieces
     * @param p the piece to be added
     * @return true if the piece was added
     */
    public abstract boolean addPiece(Piece p);
    
    
    
}