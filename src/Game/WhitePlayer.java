package Game;

import Pieces.Piece;
import java.util.ArrayList;

public class WhitePlayer extends Player {

    private ArrayList<Piece> playerPieces = new ArrayList<>();

    public WhitePlayer() {
        super(PlayerType.White);
    }

    public boolean addPiece(Piece p) {
        return playerPieces.add(p);
    }
}
