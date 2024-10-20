package movecalc;

import chess.*;
import java.util.ArrayList;
import java.util.Collection;

public class PawnMove extends ChessMoveCalc {

    private int startRow;
    private int[][] moveDirections;
    private int[] doubleStepMove;
    public ChessPiece.PieceType[] promotionOptions = {
            ChessPiece.PieceType.QUEEN,
            ChessPiece.PieceType.BISHOP,
            ChessPiece.PieceType.KNIGHT,
            ChessPiece.PieceType.ROOK};

    public PawnMove(ChessGame.TeamColor teamColor, ChessPiece.PieceType type) {
        super(teamColor, type);

        int forwardDirection = (teamColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        this.moveDirections = new int[][]{{forwardDirection, 1}, {forwardDirection, 0}, {forwardDirection, -1}};
        this.startRow = (teamColor == ChessGame.TeamColor.WHITE) ? 2 : 7;
        this.doubleStepMove = new int[]{2 * forwardDirection, 0};
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition currentPos) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        boolean canMoveForward = false;

        for (int[] direction : moveDirections) {
            int newRow = currentPos.getRow() + direction[0];
            int newCol = currentPos.getColumn() + direction[1];
            ChessPosition newPos = new ChessPosition(newRow, newCol);

            if (checkBound(newRow, newCol, board)) {
                ChessPiece targetPiece = board.getPiece(newPos);


                if (direction[1] != 0 && targetPiece != null && targetPiece.getTeamColor() != this.teamColor) {
                    possibleMoves.addAll(addPromotionMoves(board, currentPos, newPos));
                }

                else if (direction[1] == 0 && targetPiece == null) {
                    canMoveForward = true;
                    possibleMoves.addAll(addPromotionMoves(board, currentPos, newPos));
                }
            }
        }

        // 첫 이동 시 두 칸 전진 가능
        if (currentPos.getRow() == startRow && canMoveForward) {
            int newRow = currentPos.getRow() + doubleStepMove[0];
            int newCol = currentPos.getColumn() + doubleStepMove[1];
            ChessPosition newPos = new ChessPosition(newRow, newCol);

            if (checkBound(newRow, newCol, board) && board.getPiece(newPos) == null) {
                possibleMoves.add(new ChessMove(currentPos, newPos, null));
            }
        }

        return possibleMoves;
    }

    public Collection<ChessMove> addPromotionMoves(ChessBoard board, ChessPosition currentPos, ChessPosition newPos) {
        Collection<ChessMove> promotionMoves = new ArrayList<>();
        if (isPromotionRow(newPos.getRow())) {
            for (ChessPiece.PieceType promoType : promotionOptions) {
                promotionMoves.add(new ChessMove(currentPos, newPos, promoType));
            }
        } else {
            promotionMoves.add(new ChessMove(currentPos, newPos, null));
        }
        return promotionMoves;
    }

    public boolean isPromotionRow(int row) {
        return (this.teamColor == ChessGame.TeamColor.WHITE && row == 8) ||
                (this.teamColor == ChessGame.TeamColor.BLACK && row == 1);
    }
}
