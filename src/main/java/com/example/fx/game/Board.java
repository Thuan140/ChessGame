package com.example.fx.game;

import com.example.fx.model.Piece;

public class Board {
    private Piece[][] grid;
    private int[] lastPawnDoubleMove = null;

    public Board() {
        grid = new Piece[8][8];
    }

    public Piece[][] getGrid() {
        return grid;
    }

    public Piece get(int row, int col) {
        return grid[row][col];
    }

    public void set(int row, int col, Piece piece) {
        grid[row][col] = piece;
    }

    public void move(int startRow, int startCol, int endRow, int endCol) {
        Piece movedPiece = grid[startRow][startCol];

        // Check for en passant capture
        if (movedPiece.getType().name().equals("PAWN") && grid[endRow][endCol] == null && startCol != endCol) {
            // Remove captured pawn behind
            int dir = (movedPiece.getColor().name().equals("WHITE")) ? 1 : -1;
            grid[endRow + dir][endCol] = null;
        }

        grid[endRow][endCol] = movedPiece;
        grid[startRow][startCol] = null;

        if (movedPiece.getType().name().equals("PAWN") && Math.abs(endRow - startRow) == 2) {
            lastPawnDoubleMove = new int[]{endRow, endCol};
        } else {
            lastPawnDoubleMove = null;
        }
    }

    public int[] getLastPawnDoubleMove() {
        return lastPawnDoubleMove;
    }
}
