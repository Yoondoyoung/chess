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
        boolean front = false;
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        for (int[] displace: newPositionsDisplacement){
            System.out.println("Round: "+num);
            System.out.println("myposition.getRow: "+myPosition.getRow());
            System.out.println("myposition.getColumn: "+myPosition.getColumn());
            int newRow = myPosition.getRow() + displace[0];
            int newCol = myPosition.getColumn() + displace[1];
            System.out.println("newRow: "+newRow);
            System.out.println("newColumn: "+newCol);
            if (checkBounds(newRow, newCol, board)){
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                if ((displace[1] != 0) && (board.getPiece(newPosition) != null) && (board.getPiece(newPosition).getTeamColor() != this.color)) {
                    possibleMoves.addAll(this.addPromo(board, myPosition, newPosition));
                }
                if ((displace[1] == 0) && (board.getPiece(newPosition) == null)) {
                    if (checkBounds(newRow, newCol, board)) {
                        front = true;
                        possibleMoves.addAll(this.addPromo(board, myPosition, newPosition));
                    }
                }
            }
            num++;
        }

        if (myPosition.getRow() == this.startRow){
            int newRow = myPosition.getRow() + this.startDisplacement[0];
            int newCol = myPosition.getColumn() + this.startDisplacement[1];
            if ((newRow < 9 && newRow > 0) && (newCol < 9 && newCol > 0) && front) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                if ((board.getPiece(newPosition) == null)) {
                    possibleMoves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }
        return possibleMoves;
    }

    public Collection<ChessMove> addPromo(ChessBoard board, ChessPosition myPosition, ChessPosition newPosition){
        Collection<ChessMove> temp = new ArrayList<>();
        if (canPromote(newPosition.getRow(), newPosition.getColumn())) {
            for (ChessPiece.PieceType promoType : this.promoTypes) {
                if ((promoType != ChessPiece.PieceType.PAWN) && (promoType != ChessPiece.PieceType.KING)) {
                    temp.add(new ChessMove(myPosition, newPosition, promoType));
                }
            }
        } else {
            temp.add(new ChessMove(myPosition, newPosition, null));
        }
        return temp;
    }

    public boolean canPromote(int row, int col){
        if ((row < 9 && row > 0) && (col < 9 && col > 0)) {
            if ((this.color == ChessGame.TeamColor.BLACK) && (row == 1)) {
                return true;
            } else return (this.color == ChessGame.TeamColor.WHITE) && (row == 8);
        }
        return false;
    }

}
