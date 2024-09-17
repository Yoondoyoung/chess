package movecalc;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMove extends ChessMoveCalc{
    public BishopMove (ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        super(pieceColor, type);
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        /* {1,1} = Upper Right
           {-1,1} = Upper Left
           {-1,-1} = Under Left
           {1,-1} = Under Right
        */
        int[][] newPositionsDisplacement = {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}};
        for (int[] displace: newPositionsDisplacement){
            possibleMoves.addAll(checkDirection(displace[0], displace[1], myPosition, board));
        }
        return possibleMoves;
    }
}
