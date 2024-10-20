package movecalc;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMove extends ChessMoveCalc{

    public KnightMove(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        super(pieceColor, type);
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        int[][] newPositionsDisplacement =
                {
                        {-2, 1}, // 1Up 2Left
                        {2, 1}, // 1Up 2Right
                        {-1, 2}, // 2Up 1Left
                        {1, 2}, // 2Up 1Right
                        {-2, -1}, // 1Down 2Left
                        {2, -1}, // 1Down 2Right
                        {-1, -2}, // 2Down 1Left
                        {1, -2} // 2Down 1Right
                };
        for (int[] displace: newPositionsDisplacement){
            int newRow = myPosition.getRow() + displace[0];
            int newCol = myPosition.getColumn() + displace[1];

            if (checkBound(newRow, newCol, board)){
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), null));
            }
        }
        return possibleMoves;
    }

}
