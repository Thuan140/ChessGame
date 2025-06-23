package com.example.fx;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;



public class Piece {
    private PieceType type;
    private ColorType color;
    private ImageView imageView;

    public Piece(PieceType type, ColorType color, Image image) {
        this.type = type;
        this.color = color;
        this.imageView = new ImageView(image);
        this.imageView.setFitWidth(ChessGame.TILE_SIZE);
        this.imageView.setFitHeight(ChessGame.TILE_SIZE);
    }

    public PieceType getType() { return type; }
    public ColorType getColor() { return color; }
    public ImageView getImageView() { return imageView; }
}