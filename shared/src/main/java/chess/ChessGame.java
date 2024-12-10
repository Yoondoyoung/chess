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
    public boolean isResigned;


    public ChessGame() {
        System.out.println(
                "Called Chess Game!"
        );
        this.board = new ChessBoard();
        this.teamTurn = TeamColor.WHITE;
        this.cloneBoard = new ChessBoard();
        this.board.resetBoard();
        this.isResigned = false;
    }

    public ChessGame(ChessBoard board, TeamColor color, ChessBoard cloneBoard, boolean isResigned) {
        this.board = board;
        this.teamTurn = color;
        this.cloneBoard = cloneBoard;
        this.isResigned = isResigned;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.teamTurn;
    }

    public ChessBoard getCloneBoard() {
        return this.cloneBoard;
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
        BLACK,
        OBSERVER
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
                    validMoves.add(move);
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
        for (int row = 0; row < this.board.pieces.length; row++) {
            for (int col = 0; col < this.board.pieces[row].length; col++) {
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
        ChessPosition target = new ChessPosition(move.getStartPosition().getRow(), move.getStartPosition().getColumn());
        if(isInCheck(this.teamTurn) ||
                this.board.getPiece(target) == null ||
                !validMoves(move.getStartPosition()).stream().anyMatch(moving -> moving.getEndPosition().equals(move.getEndPosition())) ||
                this.board.getPiece(target).getTeamColor() != this.teamTurn)
        {
            throw new InvalidMoveException();
        }


        ChessPiece movedPiece = board.getPiece(move.getStartPosition());

        if(movedPiece.getPieceType() == ChessPiece.PieceType.PAWN){
            if(move.getPromotionPiece() != null){
                movedPiece.type = move.getPromotionPiece();
            }
        }
        takenPiece = board.getPiece(move.getEndPosition());
        board.addPiece(move.getEndPosition(), movedPiece);
        board.addPiece(move.getStartPosition(), null);
        lastMove = move;
        System.out.println("Turn changed from : " + this.teamTurn);
        this.teamTurn = (this.teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        System.out.println("Turn changed to : " + this.teamTurn);
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
            if (moves != null && !moves.isEmpty()){
                return true;
            }
        }
        return false;
    }

    public boolean isThreatened(ChessBoard board, ChessPosition pos, ChessGame.TeamColor color){
        ChessPiece[][] pieces = board.pieces;

        for(int row = 0; row < pieces.length; row++){
            for(int col = 0; col < pieces[row].length; col++){
                ChessPosition target = new ChessPosition(row+1, col+1);
                ChessPiece piece = pieces[row][col];
                if(pieces[row][col] != null && board.getPiece(target).getTeamColor() != color){
                    Collection<ChessMove> moves = piece.pieceMoves(board, target);

                    if(moves.stream().anyMatch(move -> move.getEndPosition().equals(pos))){
                        System.out.println(pieces[row][col]+" can attack your king!");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Collection<ChessPosition> getAllTeamPiecesPositions(TeamColor teamColor) {
        Collection<ChessPosition> positions = new ArrayList<>();
        ChessPiece[][] pieces = this.board.pieces;
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
        ChessPiece[][] pieces = board.pieces;
        for(int i = 0; i < pieces.length; i++){
            for(int j = 0; j < pieces[i].length; j++){
                if(pieces[i][j] != null && pieces[i][j].type == ChessPiece.PieceType.KING &&  pieces[i][j].getTeamColor() == color){
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

        if(!isInCheck(teamColor, this.board) && canMoveOutOfCheck(teamColor)){
            System.out.println(teamColor + " is in stalemate!");
        }
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

    public boolean isResigned() {
        return isResigned;
    }

    public void setResigned(boolean resigned) {
        isResigned = resigned;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
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
