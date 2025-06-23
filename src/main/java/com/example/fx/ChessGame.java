package com.example.fx;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class ChessGame extends Application {
    static final int TILE_SIZE = 80;
    static final int BOARD_SIZE = 8;
    private Piece[][] board = new Piece[BOARD_SIZE][BOARD_SIZE];

private Piece selectedPiece;
private int startRow, startCol;
private boolean isWhiteTurn = true; // Bắt đầu với lượt của trắng

    @Override
    public void start(Stage stage) {
        GridPane grid = new GridPane();

        // Tạo ô bàn cờ
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Rectangle tile = new Rectangle(TILE_SIZE, TILE_SIZE);
                tile.setFill((row + col) % 2 == 0 ? Color.WHITE : Color.DARKGRAY);
                grid.add(tile, col, row);
            }
        }

        // Khởi tạo quân cờ (giả sử có hình ảnh)
        initializeBoard(grid);

        Scene scene = new Scene(grid, TILE_SIZE * BOARD_SIZE, TILE_SIZE * BOARD_SIZE);
        stage.setTitle("Chess Game");
        stage.setScene(scene);
        stage.show();
    }

    private void initializeBoard(GridPane grid) {
         // Quân Trắng (hàng 7: các quân chính, hàng 6: Tốt)
         String[] whiteImages = {
            // "white_rook.png", "white_knight.png", "white_bishop.png", "white_queen.png",
            // "white_king.png", "white_bishop.png", "white_knight.png", "white_rook.png"
               "white_rook.png", "white_knight.png", "white_bishop.png", "white_queen.png",
            "white_king.png", "white_bishop.png", "white_knight.png", "white_rook.png"
        };
        PieceType[] pieceTypes = {
            PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN,
            PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK
        };

        for (int col = 0; col < BOARD_SIZE; col++) {
            // Hàng 7: Các quân chính Trắng
            Image image = new Image("file:src/main/resources/images/" + whiteImages[col]);
            Piece piece = new Piece(pieceTypes[col], ColorType.WHITE, image);
            board[7][col] = piece;
            grid.add(piece.getImageView(), col, 7);
            addDragEvents(piece.getImageView());

            // Hàng 6: Tốt Trắng
            image = new Image("file:src/main/resources/images/white_pawn.png");
            piece = new Piece(PieceType.PAWN, ColorType.WHITE, image);
            board[6][col] = piece;
            grid.add(piece.getImageView(), col, 6);
            addDragEvents(piece.getImageView());
        }

        // Quân Đen (hàng 0: các quân chính, hàng 1: Tốt)
        String[] blackImages = {
             "black_rook.png", "black_knight.png", "black_bishop.png", "black_queen.png",
            "black_king.png", "black_bishop.png", "black_knight.png", "black_rook.png"
        };

        for (int col = 0; col < BOARD_SIZE; col++) {
            // Hàng 0: Các quân chính Đen
            Image image = new Image("file:src/main/resources/images/" + blackImages[col]);
            Piece piece = new Piece(pieceTypes[col], ColorType.BLACK, image);
            board[0][col] = piece;
            grid.add(piece.getImageView(), col, 0);
            addDragEvents(piece.getImageView());

            // Hàng 1: Tốt Đen
            image = new Image("file:src/main/resources/images/black_pawn.png");
            piece = new Piece(PieceType.PAWN, ColorType.BLACK, image);
            board[1][col] = piece;
            grid.add(piece.getImageView(), col, 1);
            addDragEvents(piece.getImageView());
        }
    }
    
    private void addDragEvents(ImageView imageView) {
        imageView.setOnMousePressed(event -> {
            for (int row = 0; row < BOARD_SIZE; row++) {
                for (int col = 0; col < BOARD_SIZE; col++) {
                    if (board[row][col] != null && board[row][col].getImageView() == imageView) {
                        if ((isWhiteTurn && board[row][col].getColor() == ColorType.WHITE) ||
                            (!isWhiteTurn && board[row][col].getColor() == ColorType.BLACK)) {
                            selectedPiece = board[row][col];
                            startRow = row;
                            startCol = col;
                        } else {
                            selectedPiece = null;
                        }
                        break;
                    }
                }
            }
        });

        imageView.setOnMouseReleased(event -> {
            if (selectedPiece == null) return;

            int endRow = (int) (event.getSceneY() / TILE_SIZE);
            int endCol = (int) (event.getSceneX() / TILE_SIZE);

            if (isValidMove(selectedPiece, startRow, startCol, endRow, endCol)) {
                board[startRow][startCol] = null;
                board[endRow][endCol] = selectedPiece;

                GridPane grid = (GridPane) selectedPiece.getImageView().getParent();
                grid.getChildren().remove(selectedPiece.getImageView());
                grid.add(selectedPiece.getImageView(), endCol, endRow);

                isWhiteTurn = !isWhiteTurn;
            }

            selectedPiece = null;
        });
    }

    private boolean isValidMove(Piece piece, int startRow, int startCol, int endRow, int endCol) {
        // Kiểm tra vị trí đích có nằm trong bàn cờ không
        if (endRow < 0 || endRow >= BOARD_SIZE || endCol < 0 || endCol >= BOARD_SIZE) {
            return false;
        }
    
        // Không cho phép di chuyển đến ô có quân cùng màu
        if (board[endRow][endCol] != null && board[endRow][endCol].getColor() == piece.getColor()) {
            return false;
        }
    
        // Kiểm tra luật di chuyển cho từng loại quân cờ
        switch (piece.getType()) {
            case PAWN:
                return isValidPawnMove(piece, startRow, startCol, endRow, endCol);
            case ROOK:
                return isValidRookMove(startRow, startCol, endRow, endCol);
            case KNIGHT:
                return isValidKnightMove(startRow, startCol, endRow, endCol);
            case BISHOP:
                return isValidBishopMove(startRow, startCol, endRow, endCol);
            case QUEEN:
                return isValidQueenMove(startRow, startCol, endRow, endCol);
            case KING:
                return isValidKingMove(startRow, startCol, endRow, endCol);
            default:
                return false;
        }
    }

    private boolean isValidPawnMove(Piece piece, int startRow, int startCol, int endRow, int endCol) {
        int direction = (piece.getColor() == ColorType.WHITE) ? -1 : 1; // Trắng đi lên, Đen đi xuống
        int startRowForPawn = (piece.getColor() == ColorType.WHITE) ? 6 : 1; // Hàng bắt đầu của Tốt
    
        // Di chuyển thẳng 1 ô
        if (startCol == endCol && endRow == startRow + direction && board[endRow][endCol] == null) {
            return true;
        }
    
        // Di chuyển thẳng 2 ô từ vị trí ban đầu
        if (startCol == endCol && startRow == startRowForPawn && endRow == startRow + 2 * direction &&
            board[endRow][endCol] == null && board[startRow + direction][startCol] == null) {
            return true;
        }
    
        // Ăn chéo
        if (Math.abs(startCol - endCol) == 1 && endRow == startRow + direction &&
            board[endRow][endCol] != null && board[endRow][endCol].getColor() != piece.getColor()) {
            return true;
        }
    
        return false;
    }
    
    private boolean isValidRookMove(int startRow, int startCol, int endRow, int endCol) {
        // Xe di chuyển ngang hoặc dọc
        if (startRow != endRow && startCol != endCol) {
            return false;
        }
    
        // Kiểm tra đường đi không bị cản
        if (startRow == endRow) { // Di chuyển ngang
            int step = (endCol > startCol) ? 1 : -1;
            for (int col = startCol + step; col != endCol; col += step) {
                if (board[startRow][col] != null) {
                    return false; // Có quân cờ cản đường
                }
            }
        } else { // Di chuyển dọc
            int step = (endRow > startRow) ? 1 : -1;
            for (int row = startRow + step; row != endRow; row += step) {
                if (board[row][startCol] != null) {
                    return false; // Có quân cờ cản đường
                }
            }
        }
        return true;
    }
    
    private boolean isValidKnightMove(int startRow, int startCol, int endRow, int endCol) {
        // Mã di chuyển hình chữ L: 2 ô ngang + 1 ô dọc hoặc 2 ô dọc + 1 ô ngang
        int rowDiff = Math.abs(endRow - startRow);
        int colDiff = Math.abs(endCol - startCol);
        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
    }
    
    // TODO: Thêm các phương thức cho Tượng, Hậu, Vua tương tự
    private boolean isValidBishopMove(int startRow, int startCol, int endRow, int endCol) {
        // Tượng di chuyển chéo
        if (Math.abs(endRow - startRow) != Math.abs(endCol - startCol)) {
            return false;
        }
        // Kiểm tra đường đi không bị cản
        int rowStep = (endRow > startRow) ? 1 : -1;
        int colStep = (endCol > startCol) ? 1 : -1;
        for (int i = 1; i < Math.abs(endRow - startRow); i++) {
            if (board[startRow + i * rowStep][startCol + i * colStep] != null) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isValidQueenMove(int startRow, int startCol, int endRow, int endCol) {
        // Hậu = Xe + Tượng
        return isValidRookMove(startRow, startCol, endRow, endCol) || isValidBishopMove(startRow, startCol, endRow, endCol);
    }
    
    private boolean isValidKingMove(int startRow, int startCol, int endRow, int endCol) {
        // Vua di chuyển 1 ô bất kỳ hướng nào
        int rowDiff = Math.abs(endRow - startRow);
        int colDiff = Math.abs(endCol - startCol);
        return rowDiff <= 1 && colDiff <= 1 && (rowDiff != 0 || colDiff != 0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}