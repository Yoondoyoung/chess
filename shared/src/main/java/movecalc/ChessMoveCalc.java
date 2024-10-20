package movecalc;

import chess.*;
import movecalc.*;

import java.util.ArrayList;
import java.util.Collection;

public class ChessMoveCalc {

    ChessGame.TeamColor teamColor;
    private ChessPiece.PieceType pieceType;

    public ChessMoveCalc(ChessGame.TeamColor teamColor, ChessPiece.PieceType type) {
        this.teamColor = teamColor;
        this.pieceType = type;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        switch (this.pieceType) {
            case BISHOP:
                return new BishopMove(this.teamColor, this.pieceType).pieceMoves(board, position);
            case KING:
                return new KingMove(this.teamColor, this.pieceType).pieceMoves(board, position);
            case PAWN:
                return new PawnMove(this.teamColor, this.pieceType).pieceMoves(board, position);
            case ROOK:
                return new RookMove(this.teamColor, this.pieceType).pieceMoves(board, position);
            case KNIGHT:
                return new KnightMove(this.teamColor, this.pieceType).pieceMoves(board, position);
            case QUEEN:
                return new QueenMove(this.teamColor, this.pieceType).pieceMoves(board, position);
            default:
                throw new UnsupportedOperationException("Move calculation not implemented for this piece.");
        }
    }

    public Collection<ChessMove> checkDirection(int rowChange, int colChange, ChessPosition position, ChessBoard board) {
        Collection<ChessMove> moves = new ArrayList<>();
        int newRow = position.getRow() + rowChange;
        int newCol = position.getColumn() + colChange;

        while (checkBound(newRow, newCol, board)) {
            ChessPosition newPosition = new ChessPosition(newRow, newCol);
            ChessPiece targetPiece = board.getPiece(newPosition);

            moves.add(new ChessMove(position, newPosition, null));
            if (targetPiece != null){
                break; // 기물이 있으면 이동을 멈춤
            }

            newRow += rowChange;
            newCol += colChange;
        }
        return moves;
    }

    public boolean checkBound(int row, int col, ChessBoard board) {
        if (row < 1 || row > 8 || col < 1 || col > 8) {
            return false; // 경계를 벗어남
        }
        ChessPiece piece = board.getPiece(new ChessPosition(row, col));
        return piece == null || this.teamColor != piece.getTeamColor(); // 상대편 기물이 있는 경우만 이동 가능
    }


}
