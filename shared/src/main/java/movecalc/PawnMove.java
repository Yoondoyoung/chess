package movecalc;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMove extends ChessMoveCalc{

    private int[][] newPositionsDisplacement;
    private int startRow;
    private int[] startDisplacement;
    private ChessPiece.PieceType[] promoTypes = new ChessPiece.PieceType[] {ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK};

    public PawnMove(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        super(pieceColor, type);

        if(pieceColor == ChessGame.TeamColor.WHITE){
            this.newPositionsDisplacement = new int[][] {{1, 0}, {1, -1}, {1, 1}};
            this.startRow = 2;
            this.startDisplacement = new int[] {2, 0};
        }else{
            this.newPositionsDisplacement = new int[][] {{-1, 0}, {-1, -1}, {-1, 1}};
            this.startRow = 7;
            this.startDisplacement = new int[] {-2, 0};
        }

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
                        {0,1} //Up
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
