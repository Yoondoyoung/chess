package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Random;

import static ui.EscapeSequences.*;

public class Board {

    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_CHARS = 1;
    private static final int LINE_WIDTH_IN_CHARS = 1;
    private static final String[] boarderLetters = {"a", "b", "c", "d", "e", "f", "g", "h"};
    private static final String[] boarderSpacing = {"   ", "    ", "    ", "    ", "   ", "    ", "   ", "    "};
    private static Random rand = new Random();
    private static ChessGame chessGame = new ChessGame();
    private final PrintStream out;

    public Board(){
        ChessBoard board = chessGame.getBoard();
        board.resetBoard();
        chessGame.setBoard(board);
        out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    }

    public void drawWhitePlayer(Collection<ChessMove> moves){
        out.print(ERASE_SCREEN);

        System.out.println();
        drawHorizontalLineBackwards(out);
        drawBoard(out, moves);
        drawHorizontalLineBackwards(out);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    public void drawBlackPlayer(Collection<ChessMove> moves){
        out.print(ERASE_SCREEN);

        System.out.println();
        drawHorizontalLine(out);
        drawBoardBackwards(out, moves);
        drawHorizontalLine(out);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    public void setGame(ChessGame game){
        chessGame = game;
    }

    public ChessGame getGame() {return chessGame;}

    private static void drawHorizontalLine(PrintStream out) {

        setBlack(out);

        int spacingCol = 0;
        for (int boardCol = BOARD_SIZE_IN_SQUARES-1; boardCol >= 0; --boardCol) {
            if (spacingCol < BOARD_SIZE_IN_SQUARES) {
                out.print(boarderSpacing[spacingCol]);
            }
            printHeaderText(out, boarderLetters[boardCol]);
            spacingCol++;
        }

        out.println();
    }

    private static void drawBoard(PrintStream out, Collection<ChessMove> moves) {
        for (int boardRow = BOARD_SIZE_IN_SQUARES-1; boardRow >= 0; --boardRow) {
            drawVerticalLine(out, "" + (boardRow+1));
            drawRow(out, boardRow, moves);
            drawVerticalLine(out, "" + (boardRow+1));
            out.println();
        }
    }

    private static void drawRowBackwards(PrintStream out, int boardRow, Collection<ChessMove> moves) {

        ChessBoard board = chessGame.getBoard();

        for (int boardCol = BOARD_SIZE_IN_SQUARES-1; boardCol >= 0; --boardCol) {
            drawSpace(out, boardRow, moves, board, boardCol);
        }
    }

    private static void drawSpace(PrintStream out, int boardRow, Collection<ChessMove> moves, ChessBoard board, int boardCol) {
        boolean checkHighlight;
        ChessPosition position = new ChessPosition(boardRow+1, boardCol+1);
        ChessPiece piece = board.getPiece(position);
        checkHighlight = isMoveSpace(moves, position);
        if (piece == null) {
            if (!checkHighlight) {
                drawBoardSpace(out, EMPTY);
            } else {
                drawHighlightedSpace(out, EMPTY);
            }
        } else {
            if (!checkHighlight) {
                drawBoardSpace(out, getChessPieceCharacter(piece));
            } else {
                drawHighlightedSpace(out, getChessPieceCharacter(piece));
            }
        }
    }

    private static boolean isMoveSpace(Collection<ChessMove> moves, ChessPosition position){
        for (ChessMove move: moves) {
            if (move.endPos.equals(position)){
                return true;
            }
        }
        return false;
    }

    private static void drawHorizontalLineBackwards(PrintStream out) {
        setBlack(out);

        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            out.print(boarderSpacing[boardCol]);
            printHeaderText(out, boarderLetters[boardCol]);
        }

        out.println();
    }

    private static void drawBoardBackwards(PrintStream out, Collection<ChessMove> moves) {

        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
            drawVerticalLine(out, "" + (boardRow+1));
            drawRowBackwards(out, boardRow, moves);
            drawVerticalLine(out, "" + (boardRow+1));
            out.println();
        }
    }

    private static void drawRow(PrintStream out, int boardRow, Collection<ChessMove> moves) {

        ChessBoard board = chessGame.getBoard();

        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawSpace(out, boardRow, moves, board, boardCol);
        }
    }

    private static void printHeaderText(PrintStream out, String player) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
        out.print(player);
        setBlack(out);
    }
    private static void printPieceText(PrintStream out, String piece) {
        out.print(SET_TEXT_COLOR_WHITE);
        out.print(piece);
        setBlack(out);
    }

    private static void drawBoardSpace(PrintStream out, String piece) {
        setGrey(out);
        printPieceText(out, piece);
        out.print(" ");
    }

    private static void drawHighlightedSpace(PrintStream out, String piece) {
        setGreen(out);
        printPieceText(out, piece);
        out.print(" ");
        out.print(RESET_TEXT_BLINKING);
    }

    private static void drawVerticalLine(PrintStream out, String number) {
        setBlack(out);
        printPieceText(out, number);
        out.print(" ");
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setGrey(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_LIGHT_GREY);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setGreen(PrintStream out) {
        out.print(SET_BG_COLOR_GREEN);
        out.print(SET_TEXT_BLINKING);
    }

    private static String getChessPieceCharacter(ChessPiece piece) {
        ChessPiece.PieceType type = piece.getPieceType();
        ChessGame.TeamColor color = piece.getTeamColor();
        if (type == ChessPiece.PieceType.KING) {
            if (color == ChessGame.TeamColor.WHITE) {
                return WHITE_KING;
            } else {
                return BLACK_KING;
            }
        } else if (type == ChessPiece.PieceType.QUEEN) {
            if (color == ChessGame.TeamColor.WHITE) {
                return WHITE_QUEEN;
            } else {
                return BLACK_QUEEN;
            }
        } else if (type == ChessPiece.PieceType.KNIGHT) {
            if (color == ChessGame.TeamColor.WHITE) {
                return WHITE_KNIGHT;
            } else {
                return BLACK_KNIGHT;
            }
        } else if (type == ChessPiece.PieceType.BISHOP) {
            if (color == ChessGame.TeamColor.WHITE) {
                return WHITE_BISHOP;
            } else {
                return BLACK_BISHOP;
            }
        } else if (type == ChessPiece.PieceType.ROOK) {
            if (color == ChessGame.TeamColor.WHITE) {
                return WHITE_ROOK;
            } else {
                return BLACK_ROOK;
            }
        } else if (type == ChessPiece.PieceType.PAWN) {
            if (color == ChessGame.TeamColor.WHITE) {
                return WHITE_PAWN;
            } else {
                return BLACK_PAWN;
            }
        } else {
            return EMPTY;
        }
    }
}