package com.example.fx.model;

import com.example.fx.ui.ChessGame;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Piece {
    private PieceType type;
    private ColorType color;
    private ImageView imageView;

    // Biến cho en passant
    private int lastFromRow = -1, lastFromCol = -1, lastToRow = -1, lastToCol = -1;

    public Piece(PieceType type, ColorType color, Image image) {
        this.type = type;
        this.color = color;
        this.imageView = new ImageView(image);
        this.imageView.setFitWidth(ChessGame.TILE_SIZE);
        this.imageView.setFitHeight(ChessGame.TILE_SIZE);
    }

    public PieceType getType() {
        return type;
    }

    public void setType(PieceType type) {
        this.type = type;
    }

    public ColorType getColor() {
        return color;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setLastMove(int fromRow, int fromCol, int toRow, int toCol) {
        lastFromRow = fromRow;
        lastFromCol = fromCol;
        lastToRow = toRow;
        lastToCol = toCol;
    }

    public int getLastFromRow() {
        return lastFromRow;
    }

    public int getLastFromCol() {
        return lastFromCol;
    }

    public int getLastToRow() {
        return lastToRow;
    }

    public int getLastToCol() {
        return lastToCol;
    }

    public Piece copy() {
        Piece clone = new Piece(type, color, imageView.getImage());
        clone.setLastMove(lastFromRow, lastFromCol, lastToRow, lastToCol);
        return clone;
    }
}
