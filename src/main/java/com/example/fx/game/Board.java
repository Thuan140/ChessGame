package com.example.fx.game;

import com.example.fx.model.Piece;

public class Board {
    private final Piece[][] grid = new Piece[8][8];

    public Piece get(int row, int col) {
        return grid[row][col];
    }

    public void set(int row, int col, Piece piece) {
        grid[row][col] = piece;
    }

    public boolean isEmpty(int row, int col) {
        return grid[row][col] == null;
    }

    public Piece[][] getGrid() {
        return grid;
    }

    public void move(int fromRow, int fromCol, int toRow, int toCol) {
        Piece p = grid[fromRow][fromCol];
        grid[toRow][toCol] = p;
        grid[fromRow][fromCol] = null;

        // Gán nước đi cuối cho việc bắt tốt qua đường
        if (p != null) {
            p.setLastMove(fromRow, fromCol, toRow, toCol);
        }
    }

    public Board cloneBoard() {
        Board clone = new Board();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (grid[row][col] != null) {
                    clone.grid[row][col] = grid[row][col].copy();
                }
            }
        }
        return clone;
    }
}
