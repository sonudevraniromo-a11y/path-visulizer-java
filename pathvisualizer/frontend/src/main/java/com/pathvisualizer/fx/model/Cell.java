package com.pathvisualizer.fx.model;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Cell extends StackPane {

    private final int row;
    private final int col;
    private CellState state = CellState.EMPTY;

    private final Rectangle rect;

    private static final double SIZE = 30;

    // Color palette
    public static final Color COLOR_EMPTY   = Color.web("#1e1e2e");
    public static final Color COLOR_WALL    = Color.web("#313244");
    public static final Color COLOR_START   = Color.web("#a6e3a1");
    public static final Color COLOR_END     = Color.web("#f38ba8");
    public static final Color COLOR_VISITED = Color.web("#89b4fa");
    public static final Color COLOR_PATH    = Color.web("#f9e2af");
    public static final Color COLOR_BORDER  = Color.web("#45475a");

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;

        rect = new Rectangle(SIZE, SIZE);
        rect.setFill(COLOR_EMPTY);
        rect.setStroke(COLOR_BORDER);
        rect.setStrokeWidth(0.5);
        rect.setArcWidth(3);
        rect.setArcHeight(3);

        getChildren().add(rect);
        setWidth(SIZE);
        setHeight(SIZE);
        updateStyle();
    }

    public void setState(CellState state) {
        this.state = state;
        updateStyle();
    }

    public CellState getState() { return state; }
    public int getRow() { return row; }
    public int getCol() { return col; }

    private void updateStyle() {
        Color fill = switch (state) {
            case EMPTY   -> COLOR_EMPTY;
            case WALL    -> COLOR_WALL;
            case START   -> COLOR_START;
            case END     -> COLOR_END;
            case VISITED -> COLOR_VISITED;
            case PATH    -> COLOR_PATH;
        };
        rect.setFill(fill);
    }

    public void animateTo(Color color) {
        rect.setFill(color);
    }

    public static double getCellSize() { return SIZE; }
}