package movecalc;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMove extends ChessMoveCalc{

    public KnightMove(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        super(pieceColor, type);
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
        int num = 0;
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        /* {1,1} = Upper Right
           {-1,1} = Upper Left
           {-1,-1} = Under Left
           {1,-1} = Under Right
        */
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
            System.out.println("Round: "+num);
            System.out.println("myposition.getRow: "+myPosition.getRow());
            System.out.println("myposition.getColumn: "+myPosition.getColumn());
            int newRow = myPosition.getRow() + displace[0];
            int newCol = myPosition.getColumn() + displace[1];
            System.out.println("newRow: "+newRow);
            System.out.println("newColumn: "+newCol+"");
            if (checkBounds(newRow, newCol, board)){
                System.out.println("Okay to move: "+checkBounds(newRow,newCol,board)+"\n");
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), null));
            }
            num++;
        }
        return possibleMoves;
    }

}
