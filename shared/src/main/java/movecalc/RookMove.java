package movecalc;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class RookMove extends ChessMoveCalc{

    public RookMove(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        super(pieceColor, type);
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        int[][] newPositionDisplacement = {
                {1,0}, // Left
                {-1,0}, // Right
                {0,1}, // Up
                {0,-1} // Down
        };
        for(int[] displacement : newPositionDisplacement){
            possibleMoves.addAll(checkDirection(displacement[0], displacement[1], myPosition, board));
        }

        return possibleMoves;
    }
}
