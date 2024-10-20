package movecalc;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class KingMove extends ChessMoveCalc{

    public KingMove(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        super(pieceColor, type);
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        int[][] newPositionsDisplacement =
                {
                    {1, 1}, //Uppser Right
                    {-1, 1}, //Upper Left
                    {-1, -1}, //Under Left
                    {1, -1}, //Under Right
                    {1, 0}, //Right
                    {0, 1}, //Up
                    {-1, 0}, //Left
                    {0, -1} //Down
                };
        for (int[] displace: newPositionsDisplacement){
            int newCol = myPosition.getColumn() + displace[1];
            int newRow = myPosition.getRow() + displace[0];
            if (checkBound(newRow, newCol, board)){

                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), null));
            }
        }
        return possibleMoves;
    }

}
