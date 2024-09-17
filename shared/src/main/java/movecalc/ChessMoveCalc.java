package movecalc;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessMove;
import chess.ChessBoard;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class ChessMoveCalc {
    public ChessGame.TeamColor color;
    public ChessPiece.PieceType type;

    public ChessMoveCalc(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        if(this.type == ChessPiece.PieceType.BISHOP){
            BishopMove bi = new BishopMove(this.color, this.type);
            return bi.pieceMoves(board, position);
        }else{
            throw new RuntimeException("Not implemented");
        }
    }
//Check Direction
    public Collection<ChessMove> checkDirection(int changeRow, int changeCol, ChessPosition myPosition, ChessBoard board){
        Collection<ChessMove> tempMoves = new ArrayList<>();
        int newRow = myPosition.getRow() + changeRow;
        int newCol = myPosition.getColumn() + changeCol;
        while (checkBounds(newRow, newCol, board)) {
            ChessPosition newPosition = new ChessPosition(newRow, newCol);
            // if next position is null, keep going
            if (board.getPiece(newPosition) == null) {
                tempMoves.add(new ChessMove(myPosition, newPosition, null));
            } else {
            // else get the piece and stop
                tempMoves.add(new ChessMove(myPosition, newPosition, null));
                break;
            }
            newRow = newRow + changeRow;
            newCol = newCol + changeCol;
        }
        return tempMoves;
    }

    public boolean checkBounds(int newRow, int newCol, ChessBoard board){
        //Check valid range of move
        if ((newRow < 9 && newRow > 0) && (newCol < 9 && newCol > 0)) {
            ChessPosition newPosition = new ChessPosition(newRow, newCol);
            //If the position is not empty
            if (board.getPiece(newPosition) != null) {
                //Get the piece
                return this.color != board.getPiece(newPosition).pieceColor;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }


}
