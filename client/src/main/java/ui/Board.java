package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Random;

import static ui.EscapeSequences.*;

public class Board {
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final String[] BOARDER_LETTER = {"a", "b", "c", "d", "e", "f", "g", "h"};
    private static final String[] BOARDER_SPACING = {"   ", "   ", "   ", "   ", "   ", "   ", "   ", "   "};
    private static Random rand = new Random();
    private static ChessGame chessGame = new ChessGame();
    private final PrintStream out;

    public Board() {
        ChessBoard board = chessGame.getBoard();
        board.resetBoard();
        chessGame.setBoard(board);
        out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    }

    public void drawWhitePlayer(Collection<ChessMove> moves) {
        out.print(ERASE_SCREEN);
        System.out.println();
        drawHorizontalLineBackwards(out);
        drawBoard(out, moves);
        drawHorizontalLineBackwards(out);
        resetColors(out);
    }

    public void drawBlackPlayer(Collection<ChessMove> moves) {
        out.print(ERASE_SCREEN);
        System.out.println();
        drawHorizontalLine(out);
        drawBoardBackwards(out, moves);
        drawHorizontalLine(out);
        resetColors(out);
    }

    public void setGame(ChessGame game) {
        chessGame = game;
    }

    public ChessGame getGame() {
        return chessGame;
    }

    private static void drawHorizontalLine(PrintStream out) {
        setBlack(out);
        int spacingCol = 0;
        for (int boardCol = BOARD_SIZE_IN_SQUARES - 1; boardCol >= 0; --boardCol) {
            if (spacingCol < BOARD_SIZE_IN_SQUARES) {
                out.print(BOARDER_SPACING[spacingCol]);
            }
            printHeaderText(out, BOARDER_LETTER[boardCol]);
            spacingCol++;
        }
        out.println();
    }

    private static void drawHorizontalLineBackwards(PrintStream out) {
        setBlack(out);
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            out.print(BOARDER_SPACING[boardCol]);
            printHeaderText(out, BOARDER_LETTER[boardCol]);
        }
        out.println();
    }

    private static void drawBoard(PrintStream out, Collection<ChessMove> moves) {
        for (int boardRow = BOARD_SIZE_IN_SQUARES - 1; boardRow >= 0; --boardRow) {
            drawVerticalLine(out, "" + (boardRow + 1));
            drawRow(out, boardRow, moves);
            drawVerticalLine(out, "" + (boardRow + 1));
            out.println();
        }
    }

    private static void drawBoardBackwards(PrintStream out, Collection<ChessMove> moves) {
        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
            drawVerticalLine(out, "" + (boardRow + 1));
            drawRowBackwards(out, boardRow, moves);
            drawVerticalLine(out, "" + (boardRow + 1));
            out.println();
        }
    }

    private static void drawRow(PrintStream out, int boardRow, Collection<ChessMove> moves) {
        ChessBoard board = chessGame.getBoard();
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            drawSquare(out, boardRow, boardCol, moves, board);
        }
    }

    private static void drawRowBackwards(PrintStream out, int boardRow, Collection<ChessMove> moves) {
        ChessBoard board = chessGame.getBoard();
        for (int boardCol = BOARD_SIZE_IN_SQUARES - 1; boardCol >= 0; --boardCol) {
            drawSquare(out, boardRow, boardCol, moves, board);
        }
    }

    private static void drawSquare(PrintStream out, int boardRow, int boardCol, Collection<ChessMove> moves, ChessBoard board) {
        ChessPosition position = new ChessPosition(boardRow + 1, boardCol + 1);
        ChessPiece piece = board.getPiece(position);
        boolean isHighlighted = isMoveSpace(moves, position);
        boolean isLightSquare = (boardRow + boardCol) % 2 == 0;

        if (isHighlighted) {
            setGreen(out);
        } else if (!isLightSquare) {
            setWhiteSquare(out);
        } else {
            setBlackSquare(out);
        }

        String pieceChar = (piece == null) ? EMPTY : getChessPieceCharacter(piece);
        printPieceText(out, pieceChar, piece, isLightSquare);
        out.print(" ");
    }

    private static boolean isMoveSpace(Collection<ChessMove> moves, ChessPosition position) {
        for (ChessMove move : moves) {
            if (move.endPos.equals(position)) {
                return true;
            }
        }
        return false;
    }

    private static void printHeaderText(PrintStream out, String text) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
        out.print(text);
        setBlack(out);
    }

    private static void printPieceText(PrintStream out, String piece, ChessPiece chessPiece, boolean isLightSquare) {
        if (chessPiece != null) {
            if (chessPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                // White pieces
                if (!isLightSquare) {
                    out.print(SET_TEXT_COLOR_BLUE); // Blue on light squares
                } else {
                    out.print(SET_TEXT_COLOR_WHITE); // White on dark squares
                }
            } else {
                // Black pieces
                if (!isLightSquare) {
                    out.print(SET_TEXT_COLOR_BLACK); // Black on light squares
                } else {
                    out.print(SET_TEXT_COLOR_RED); // Red on dark squares
                }
            }
        }
        out.print(piece);
    }

    private static void drawVerticalLine(PrintStream out, String number) {
        setBlack(out);
        printNumberText(out, number);
        out.print(" ");
    }

    private static void printNumberText(PrintStream out, String number) {
        out.print(SET_TEXT_COLOR_WHITE);
        out.print(number);
    }

    private static void setWhiteSquare(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
    }

    private static void setBlackSquare(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREY);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setGreen(PrintStream out) {
        out.print(SET_BG_COLOR_GREEN);
        out.print(SET_TEXT_BLINKING);
    }

    private static void resetColors(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static String getChessPieceCharacter(ChessPiece piece) {
        ChessPiece.PieceType type = piece.getPieceType();
        ChessGame.TeamColor color = piece.getTeamColor();

        return switch (type) {
            case KING -> (color == ChessGame.TeamColor.WHITE) ? WHITE_KING : BLACK_KING;
            case QUEEN -> (color == ChessGame.TeamColor.WHITE) ? WHITE_QUEEN : BLACK_QUEEN;
            case KNIGHT -> (color == ChessGame.TeamColor.WHITE) ? WHITE_KNIGHT : BLACK_KNIGHT;
            case BISHOP -> (color == ChessGame.TeamColor.WHITE) ? WHITE_BISHOP : BLACK_BISHOP;
            case ROOK -> (color == ChessGame.TeamColor.WHITE) ? WHITE_ROOK : BLACK_ROOK;
            case PAWN -> (color == ChessGame.TeamColor.WHITE) ? WHITE_PAWN : BLACK_PAWN;
        };
    }
}