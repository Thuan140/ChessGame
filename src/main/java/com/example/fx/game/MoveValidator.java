package com.example.fx.game;

import com.example.fx.model.ColorType;
import com.example.fx.model.Piece;
import com.example.fx.model.PieceType;

public class MoveValidator {
    private Piece[][] board;
    private int BOARD_SIZE;
    private Board fullBoard;

    public MoveValidator(Board board, int boardSize) {
        this.fullBoard = board;
        this.board = board.getGrid();
        this.BOARD_SIZE = boardSize;
    }

    public boolean isValidMove(Piece piece, int startRow, int startCol, int endRow, int endCol) {
        if (endRow < 0 || endRow >= BOARD_SIZE || endCol < 0 || endCol >= BOARD_SIZE)
            return false;

        if (board[endRow][endCol] != null && board[endRow][endCol].getColor() == piece.getColor())
            return false;

        boolean isValid = false;
        switch (piece.getType()) {
            case PAWN:
                isValid = isValidPawnMove(piece, startRow, startCol, endRow, endCol);
                break;
            case ROOK:
                isValid = isValidRookMove(startRow, startCol, endRow, endCol);
                break;
            case KNIGHT:
                isValid = isValidKnightMove(startRow, startCol, endRow, endCol);
                break;
            case BISHOP:
                isValid = isValidBishopMove(startRow, startCol, endRow, endCol);
                break;
            case QUEEN:
                isValid = isValidQueenMove(startRow, startCol, endRow, endCol);
                break;
            case KING:
                isValid = isValidKingMove(startRow, startCol, endRow, endCol);
                break;
        }

        if (!isValid) return false;

        Piece temp = board[endRow][endCol];
        board[endRow][endCol] = piece;
        board[startRow][startCol] = null;

        boolean isStillInCheck = isInCheck(piece.getColor());

        board[startRow][startCol] = piece;
        board[endRow][endCol] = temp;

        return !isStillInCheck;
    }

    private boolean isValidPawnMove(Piece piece, int startRow, int startCol, int endRow, int endCol) {
        int dir = (piece.getColor() == ColorType.WHITE) ? -1 : 1;
        int startRowForPawn = (piece.getColor() == ColorType.WHITE) ? 6 : 1;

        // Đi thẳng
        if (startCol == endCol && board[endRow][endCol] == null) {
            if (endRow == startRow + dir) return true;
            if (startRow == startRowForPawn && endRow == startRow + 2 * dir &&
                board[startRow + dir][startCol] == null)
                return true;
        }

        // Ăn chéo
        if (Math.abs(startCol - endCol) == 1 && endRow == startRow + dir) {
            // Ăn thường
            if (board[endRow][endCol] != null && board[endRow][endCol].getColor() != piece.getColor()) {
                return true;
            }
            // Bắt tốt qua đường
            int[] lastMove = fullBoard.getLastPawnDoubleMove();
            if (lastMove != null &&
                lastMove[0] == startRow && lastMove[1] == endCol) {
                Piece adjacentPawn = board[startRow][endCol];
                if (adjacentPawn != null && adjacentPawn.getType() == PieceType.PAWN &&
                    adjacentPawn.getColor() != piece.getColor()) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isValidRookMove(int startRow, int startCol, int endRow, int endCol) {
        if (startRow != endRow && startCol != endCol) return false;
        if (startRow == endRow) {
            int step = (endCol > startCol) ? 1 : -1;
            for (int c = startCol + step; c != endCol; c += step) {
                if (board[startRow][c] != null) return false;
            }
        } else {
            int step = (endRow > startRow) ? 1 : -1;
            for (int r = startRow + step; r != endRow; r += step) {
                if (board[r][startCol] != null) return false;
            }
        }
        return true;
    }

    private boolean isValidKnightMove(int startRow, int startCol, int endRow, int endCol) {
        int rowDiff = Math.abs(endRow - startRow);
        int colDiff = Math.abs(endCol - startCol);
        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
    }

    private boolean isValidBishopMove(int startRow, int startCol, int endRow, int endCol) {
        if (Math.abs(startRow - endRow) != Math.abs(startCol - endCol)) return false;
        int rowStep = (endRow > startRow) ? 1 : -1;
        int colStep = (endCol > startCol) ? 1 : -1;
        int len = Math.abs(endRow - startRow);
        for (int i = 1; i < len; i++) {
            if (board[startRow + i * rowStep][startCol + i * colStep] != null) return false;
        }
        return true;
    }

    private boolean isValidQueenMove(int startRow, int startCol, int endRow, int endCol) {
        return isValidRookMove(startRow, startCol, endRow, endCol) ||
               isValidBishopMove(startRow, startCol, endRow, endCol);
    }

    private boolean isValidKingMove(int startRow, int startCol, int endRow, int endCol) {
        int rowDiff = Math.abs(startRow - endRow);
        int colDiff = Math.abs(startCol - endCol);
        return (rowDiff <= 1 && colDiff <= 1);
    }

    public boolean isInCheck(ColorType color) {
        int kingRow = -1, kingCol = -1;
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[r].length; c++) {
                Piece piece = board[r][c];
                if (piece != null && piece.getColor() == color && piece.getType() == PieceType.KING) {
                    kingRow = r;
                    kingCol = c;
                    break;
                }
            }
        }
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[r].length; c++) {
                Piece piece = board[r][c];
                if (piece != null && piece.getColor() != color) {
                    if (isValidMove(piece, r, c, kingRow, kingCol)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isCheckmate(ColorType color) {
        for (int r1 = 0; r1 < board.length; r1++) {
            for (int c1 = 0; c1 < board[r1].length; c1++) {
                Piece piece = board[r1][c1];
                if (piece != null && piece.getColor() == color) {
                    for (int r2 = 0; r2 < board.length; r2++) {
                        for (int c2 = 0; c2 < board[r2].length; c2++) {
                            if (isValidMove(piece, r1, c1, r2, c2)) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}