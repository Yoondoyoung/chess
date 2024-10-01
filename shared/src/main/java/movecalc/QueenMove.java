package movecalc;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMove extends ChessMoveCalc{

    public QueenMove(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        super(pieceColor, type);
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
        int num = 0;
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
            possibleMoves.addAll(checkDirection(displace[0], displace[1], myPosition, board));
            num++;
        }
        return possibleMoves;
    }
}
