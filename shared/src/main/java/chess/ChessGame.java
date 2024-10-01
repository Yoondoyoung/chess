package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard board;
    private ChessBoard cloneBoard;
    private ChessPiece takenPiece;
    private ChessMove lastMove;
    private boolean isCheck = false;

    public ChessGame() {
        this.board = new ChessBoard();
        this.teamTurn = TeamColor.WHITE;
        this.cloneBoard = new ChessBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece target = this.board.getPiece(startPosition);
        if(target != null){

            Collection<ChessMove> possibleMoves = target.pieceMoves(this.board, startPosition);
            Collection<ChessMove> validMoves = new ArrayList<>();

            ChessBoard clonedBoard = this.cloneBoard();
            for(ChessMove move : possibleMoves){
                applyMove(clonedBoard, move);
                if (!isInCheck(target.getTeamColor(), clonedBoard)) {
                    System.out.println("validMove = "+move+ "Color = "+target.getTeamColor()+ "Type = "+target.getPieceType());
                    validMoves.add(move);
                    System.out.println("Added");
                }
                undoMove(clonedBoard, move);
            }

            return validMoves;
        }else{
            return null;
        }

    }

    private ChessBoard cloneBoard() {
        ChessBoard clonedBoard = new ChessBoard();
        for (int row = 0; row < this.board.board.length; row++) {
            for (int col = 0; col < this.board.board[row].length; col++) {
                ChessPosition pos = new ChessPosition(row + 1, col + 1);
                clonedBoard.addPiece(pos, this.board.getPiece(pos));
            }
        }
        return clonedBoard;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition target = new ChessPosition(move.getStartPosition().getRow()+1, move.getStartPosition().getColumn()+1);
        System.out.println(this.board.getPiece(target));
        if(isInCheck(this.teamTurn)){
            throw new InvalidMoveException();
        }
        if(target.getPiece() == null){
            throw new InvalidMoveException();
        }
        ChessPiece movedPiece = board.getPiece(move.getStartPosition());
        takenPiece = board.getPiece(move.getEndPosition());
        board.addPiece(move.getEndPosition(), movedPiece);
        board.addPiece(move.getStartPosition(), null);
        lastMove = move;
    }

    public void applyMove(ChessBoard board, ChessMove move) {
        ChessPiece movedPiece = board.getPiece(move.getStartPosition());
        takenPiece = board.getPiece(move.getEndPosition());
        board.addPiece(move.getEndPosition(), movedPiece);
        board.addPiece(move.getStartPosition(), null);
        lastMove = move;
    }

    public void undoMove(ChessBoard board, ChessMove move) {
        ChessPiece movedPiece = board.getPiece(move.getEndPosition());
        board.addPiece(move.getEndPosition(), takenPiece);
        board.addPiece(move.getStartPosition(), movedPiece);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {

        return isInCheck(teamColor, this.board);
    }

    public boolean isInCheck(TeamColor color, ChessBoard board){
        ChessPosition kingPos = findKingPosition(color, board);
        return isThreatened(board, kingPos, color);
    }

    private boolean canMoveOutOfCheck(TeamColor teamColor) {
        for (ChessPosition position : getAllTeamPiecesPositions(teamColor)) {
            Collection<ChessMove> moves = validMoves(position);
            if (moves != null && !moves.isEmpty()) return true;
        }
        return false;
    }

    public boolean isThreatened(ChessBoard board, ChessPosition pos, ChessGame.TeamColor color){
        ChessPiece[][] pieces = board.board;

        for(int row = 0; row < pieces.length; row++){
            for(int col = 0; col < pieces[row].length; col++){
                ChessPosition target = new ChessPosition(row+1, col+1);
                ChessPiece piece = pieces[row][col];
                if(pieces[row][col] != null && board.getPiece(target).getTeamColor() != color){
                    Collection<ChessMove> moves = piece.pieceMoves(board, target);
                    System.out.println("Who is attacking? = "+pieces[row][col]);

                    if(moves.stream().anyMatch(move -> move.getEndPosition().equals(pos))){
                        System.out.println("MOVES = "+ moves);
                        System.out.println("Threatened!");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Collection<ChessPosition> getAllTeamPiecesPositions(TeamColor teamColor) {
        Collection<ChessPosition> positions = new ArrayList<>();
        ChessPiece[][] pieces = this.board.board;
        for (int row = 0; row < pieces.length; row++) {
            for (int col = 0; col < pieces[row].length; col++) {
                if (pieces[row][col] != null && pieces[row][col].getTeamColor() == teamColor) {
                    positions.add(new ChessPosition(row + 1, col + 1));
                }
            }
        }
        return positions;
    }

    public ChessPosition findKingPosition(ChessGame.TeamColor color ,ChessBoard board){
        ChessPiece[][] pieces = board.board;
        for(int i = 0; i < pieces.length; i++){
            for(int j = 0; j < pieces[i].length; j++){
                if(pieces[i][j] != null && pieces[i][j].type == ChessPiece.PieceType.KING &&  pieces[i][j].getTeamColor() == color){
                    System.out.println("King Position = " + new ChessPosition(i+1, j+1));
                    return new ChessPosition(i+1, j+1);
                }
            }
        }
        return null;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor, this.board) && !canMoveOutOfCheck(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor, this.board) && !canMoveOutOfCheck(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return getTeamTurn() == chessGame.getTeamTurn() && Objects.equals(getBoard(), chessGame.getBoard());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTeamTurn(), getBoard());
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "teamTurn=" + teamTurn +
                ", board=" + board +
                '}';
    }
}
