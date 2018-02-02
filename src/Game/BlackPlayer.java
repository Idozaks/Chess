package Game;

import Pieces.Piece;
import java.util.ArrayList;

public class BlackPlayer extends Player {

    private ArrayList<Piece> playerPieces = new ArrayList<>();

    public BlackPlayer() {
        super(PlayerType.Black);
    }

    @Override
    public boolean addPiece(Piece p) {
        return playerPieces.add(p);
    }

}
