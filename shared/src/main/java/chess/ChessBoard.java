package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    public ChessPiece[][] pieces;

    public ChessBoard() {
        //Size of Board 8*8
        pieces = new ChessPiece[8][8];
    }


    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        this.pieces[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return this.pieces[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        //Reset for new game
        this.pieces = new ChessPiece[][] {

                {new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK)}, // 8
                {new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN)}, // 7
                {null, null, null, null, null, null, null, null}, // 6
                {null, null, null, null, null, null, null, null}, // 5
                {null, null, null, null, null, null, null, null}, // 4
                {null, null, null, null, null, null, null, null}, // 3
                {new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN)}, // 2
                {new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK)}// 1

        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(pieces, that.pieces);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(pieces);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "pieces=" + Arrays.deepToString(pieces) +
                '}';
    }

}
