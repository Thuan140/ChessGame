package com.example.fx.ui;

import com.example.fx.game.Board;
import com.example.fx.game.MoveValidator;
import com.example.fx.model.ColorType;
import com.example.fx.model.Piece;
import com.example.fx.model.PieceType;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.File;
import java.util.*;

public class ChessGame extends Application {
    public static final int TILE_SIZE = 80;
    public static final int BOARD_SIZE = 8;

    private final Board board = new Board();
    private boolean isWhiteTurn = true;
    private Piece selectedPiece;
    private MoveValidator moveValidator;
    private int startRow, startCol;

    private Media moveSound;
    private Media captureSound;

    private Rectangle[][] tiles = new Rectangle[BOARD_SIZE][BOARD_SIZE];
    private List<Rectangle> highlightedTiles = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        GridPane grid = new GridPane();
        moveValidator = new MoveValidator(board, BOARD_SIZE);

        loadSounds();

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Rectangle tile = new Rectangle(TILE_SIZE, TILE_SIZE);
                tile.setFill((row + col) % 2 == 0 ? Color.WHITE : Color.DARKGRAY);
                tiles[row][col] = tile;
                grid.add(tile, col, row);
            }
        }

        setupPieces(grid);

        Scene scene = new Scene(grid, TILE_SIZE * BOARD_SIZE, TILE_SIZE * BOARD_SIZE);
        stage.setTitle("Chess Game");
        stage.setScene(scene);
        stage.show();
    }

    private void loadSounds() {
        try {
            moveSound = new Media(new File("src/main/resources/sounds/move.mp3").toURI().toString());
            captureSound = new Media(new File("src/main/resources/sounds/capture.mp3").toURI().toString());
        } catch (Exception e) {
            System.err.println("Không thể tải âm thanh: " + e.getMessage());
        }
    }

    private void setupPieces(GridPane grid) {
        PieceType[] layout = {
                PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN,
                PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK
        };

        for (int col = 0; col < BOARD_SIZE; col++) {
            createPiece(grid, 0, col, layout[col], ColorType.BLACK);
            createPiece(grid, 1, col, PieceType.PAWN, ColorType.BLACK);
            createPiece(grid, 6, col, PieceType.PAWN, ColorType.WHITE);
            createPiece(grid, 7, col, layout[col], ColorType.WHITE);
        }
    }

    private void createPiece(GridPane grid, int row, int col, PieceType type, ColorType color) {
        String imageName = color.name().toLowerCase() + "_" + type.name().toLowerCase() + ".png";
        Image image = new Image("file:src/main/resources/images/" + imageName);
        Piece piece = new Piece(type, color, image);
        board.set(row, col, piece);

        ImageView view = piece.getImageView();
        grid.add(view, col, row);
        addDragHandlers(view, grid);
    }

    private void highlightValidMoves(Piece piece, int row, int col) {
        clearHighlights();
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                if (moveValidator.isValidMove(piece, row, col, r, c)) {
                    tiles[r][c].setFill(Color.LIGHTGREEN);
                    highlightedTiles.add(tiles[r][c]);
                }
            }
        }
    }

    private void clearHighlights() {
        for (Rectangle tile : highlightedTiles) {
            int row = GridPane.getRowIndex(tile);
            int col = GridPane.getColumnIndex(tile);
            tile.setFill((row + col) % 2 == 0 ? Color.WHITE : Color.DARKGRAY);
        }
        highlightedTiles.clear();
    }

    private void addDragHandlers(ImageView view, GridPane grid) {
        view.setOnMousePressed(e -> {
            for (int r = 0; r < BOARD_SIZE; r++) {
                for (int c = 0; c < BOARD_SIZE; c++) {
                    Piece p = board.get(r, c);
                    if (p != null && p.getImageView() == view) {
                        if ((isWhiteTurn && p.getColor() == ColorType.WHITE) ||
                                (!isWhiteTurn && p.getColor() == ColorType.BLACK)) {
                            selectedPiece = p;
                            startRow = r;
                            startCol = c;
                            highlightValidMoves(selectedPiece, startRow, startCol);
                        }
                        return;
                    }
                }
            }
        });

        view.setOnMouseReleased(e -> {
            if (selectedPiece == null) return;

            int endRow = (int) (e.getSceneY() / TILE_SIZE);
            int endCol = (int) (e.getSceneX() / TILE_SIZE);

            clearHighlights();

            if (moveValidator.isValidMove(selectedPiece, startRow, startCol, endRow, endCol)) {
                Piece captured = board.get(endRow, endCol);

                if (captured == null && selectedPiece.getType() == PieceType.PAWN && startCol != endCol) {
                    int dir = selectedPiece.getColor() == ColorType.WHITE ? 1 : -1;
                    Piece enPassantTarget = board.get(endRow + dir, endCol);
                    if (enPassantTarget != null && enPassantTarget.getType() == PieceType.PAWN) {
                        grid.getChildren().remove(enPassantTarget.getImageView());
                        board.set(endRow + dir, endCol, null);
                        new MediaPlayer(captureSound).play();
                    }
                } else if (captured != null) {
                    grid.getChildren().remove(captured.getImageView());
                    new MediaPlayer(captureSound).play();
                } else {
                    new MediaPlayer(moveSound).play();
                }

                board.move(startRow, startCol, endRow, endCol);
                grid.getChildren().remove(selectedPiece.getImageView());
                grid.add(selectedPiece.getImageView(), endCol, endRow);

                if (selectedPiece.getType() == PieceType.PAWN && (endRow == 0 || endRow == BOARD_SIZE - 1)) {
                    promotePawn(endRow, endCol, selectedPiece.getColor(), grid);
                }

                if (moveValidator.isCheckmate(isWhiteTurn ? ColorType.BLACK : ColorType.WHITE)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Checkmate");
                    alert.setHeaderText("Chiếu hết!");
                    alert.setContentText((isWhiteTurn ? "Trắng" : "Đen") + " thắng!");
                    alert.showAndWait();
                }

                isWhiteTurn = !isWhiteTurn;
            }

            selectedPiece = null;
        });
    }

    private void promotePawn(int row, int col, ColorType color, GridPane grid) {
        List<PieceType> options = Arrays.asList(PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT);
        ChoiceDialog<PieceType> dialog = new ChoiceDialog<>(PieceType.QUEEN, options);
        dialog.setTitle("Phong cấp tốt");
        dialog.setHeaderText("Tốt đã đến cuối bàn!");
        dialog.setContentText("Chọn quân để phong cấp:");

        Optional<PieceType> result = dialog.showAndWait();
        result.ifPresent(selectedType -> {
            String imageName = color.name().toLowerCase() + "_" + selectedType.name().toLowerCase() + ".png";
            Image image = new Image("file:src/main/resources/images/" + imageName);
            Piece newPiece = new Piece(selectedType, color, image);
            board.set(row, col, newPiece);

            grid.getChildren().removeIf(node ->
                    GridPane.getRowIndex(node) == row &&
                            GridPane.getColumnIndex(node) == col &&
                            node instanceof ImageView
            );

            grid.add(newPiece.getImageView(), col, row);
            addDragHandlers(newPiece.getImageView(), grid);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}