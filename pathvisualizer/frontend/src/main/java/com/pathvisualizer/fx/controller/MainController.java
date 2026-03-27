package com.pathvisualizer.fx.controller;

import com.pathvisualizer.fx.model.Cell;
import com.pathvisualizer.fx.model.CellState;
import com.pathvisualizer.fx.service.ApiClient;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    // ─── FXML bindings ───────────────────────────────────────────────
    @FXML private GridPane gridPane;
    @FXML private ComboBox<String> algorithmBox;
    @FXML private ComboBox<String> modeBox;
    @FXML private Button visualizeBtn;
    @FXML private Button clearBtn;
    @FXML private Button resetBtn;
    @FXML private Label statusLabel;
    @FXML private Label statsLabel;
    @FXML private Slider speedSlider;
    @FXML private HBox legendBox;

    // ─── Grid config ─────────────────────────────────────────────────
    private static final int ROWS = 20;
    private static final int COLS = 35;

    private Cell[][] grid = new Cell[ROWS][COLS];
    private int[] startCell = null;
    private int[] endCell   = null;
    private boolean isRunning = false;

    private final ApiClient apiClient = new ApiClient();

    // ─── Init ─────────────────────────────────────────────────────────
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        algorithmBox.getItems().addAll("BFS", "DFS", "DIJKSTRA", "ASTAR");
        algorithmBox.setValue("ASTAR");

        modeBox.getItems().addAll("Draw Walls", "Set Start", "Set End", "Erase");
        modeBox.setValue("Draw Walls");

        buildGrid();
        setDefaultStartEnd();

        visualizeBtn.setOnAction(e -> runVisualization());
        clearBtn.setOnAction(e -> clearPath());
        resetBtn.setOnAction(e -> resetGrid());
    }

    // ─── Grid builder ─────────────────────────────────────────────────
    private void buildGrid() {
        gridPane.getChildren().clear();
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Cell cell = new Cell(r, c);
                grid[r][c] = cell;
                attachMouseHandlers(cell);
                gridPane.add(cell, c, r);
            }
        }
    }

    private void setDefaultStartEnd() {
        startCell = new int[]{ROWS / 2, 2};
        endCell   = new int[]{ROWS / 2, COLS - 3};
        grid[startCell[0]][startCell[1]].setState(CellState.START);
        grid[endCell[0]][endCell[1]].setState(CellState.END);
    }

    // ─── Mouse interaction ────────────────────────────────────────────
    private boolean painting = false;

    private void attachMouseHandlers(Cell cell) {
        cell.setOnMousePressed(e -> {
            if (isRunning) return;
            painting = true;
            handleCellInteraction(cell);
        });
        cell.setOnMouseDragEntered(e -> {
            if (isRunning || !painting) return;
            handleCellInteraction(cell);
        });
        cell.setOnMouseReleased(e -> painting = false);
        cell.setOnDragDetected(e -> cell.startFullDrag());
    }

    private void handleCellInteraction(Cell cell) {
        int r = cell.getRow(), c = cell.getCol();
        String mode = modeBox.getValue();

        switch (mode) {
            case "Draw Walls" -> {
                if (cell.getState() == CellState.EMPTY) {
                    cell.setState(CellState.WALL);
                }
            }
            case "Erase" -> {
                if (cell.getState() == CellState.WALL ||
                    cell.getState() == CellState.VISITED ||
                    cell.getState() == CellState.PATH) {
                    cell.setState(CellState.EMPTY);
                }
            }
            case "Set Start" -> {
                if (cell.getState() == CellState.END) return;
                if (startCell != null)
                    grid[startCell[0]][startCell[1]].setState(CellState.EMPTY);
                startCell = new int[]{r, c};
                cell.setState(CellState.START);
            }
            case "Set End" -> {
                if (cell.getState() == CellState.START) return;
                if (endCell != null)
                    grid[endCell[0]][endCell[1]].setState(CellState.EMPTY);
                endCell = new int[]{r, c};
                cell.setState(CellState.END);
            }
        }
    }

    // ─── Run visualization ────────────────────────────────────────────
    private void runVisualization() {
        if (isRunning) return;
        if (startCell == null || endCell == null) {
            statusLabel.setText("⚠ Place start and end nodes first!");
            return;
        }

        clearPath();
        isRunning = true;
        visualizeBtn.setDisable(true);
        statusLabel.setText("⏳ Running " + algorithmBox.getValue() + "...");

        // Collect walls
        List<int[]> walls = new ArrayList<>();
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++)
                if (grid[r][c].getState() == CellState.WALL)
                    walls.add(new int[]{r, c});

        String algo = algorithmBox.getValue();
        int[] start = startCell;
        int[] end   = endCell;

        // Call API on background thread
        Thread thread = new Thread(() -> {
            try {
                ApiClient.PathResponse result =
                        apiClient.solve(ROWS, COLS, start, end, walls, algo);

                Platform.runLater(() -> animateResult(result));
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    statusLabel.setText("❌ Error: " + ex.getMessage() +
                            "\n(Is the Spring Boot server running on port 8080?)");
                    isRunning = false;
                    visualizeBtn.setDisable(false);
                });
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    // ─── Animation ────────────────────────────────────────────────────
    private void animateResult(ApiClient.PathResponse result) {
        double speedMs = 101 - speedSlider.getValue(); // 1=fast, 100=slow
        List<int[]> visited = result.visitedOrder();
        List<int[]> path    = result.shortestPath();

        Timeline visitTimeline = new Timeline();
        for (int i = 0; i < visited.size(); i++) {
            int[] cell = visited.get(i);
            int idx = i;
            KeyFrame kf = new KeyFrame(Duration.millis(speedMs * idx), e -> {
                int r = cell[0], c = cell[1];
                if (grid[r][c].getState() == CellState.EMPTY) {
                    grid[r][c].setState(CellState.VISITED);
                    grid[r][c].animateTo(Cell.COLOR_VISITED);
                }
            });
            visitTimeline.getKeyFrames().add(kf);
        }

        visitTimeline.setOnFinished(e -> {
            if (!result.pathFound()) {
                statusLabel.setText("❌ No path found! Nodes visited: " + visited.size());
                isRunning = false;
                visualizeBtn.setDisable(false);
                return;
            }

            Timeline pathTimeline = new Timeline();
            for (int i = 0; i < path.size(); i++) {
                int[] cell = path.get(i);
                int idx = i;
                KeyFrame kf = new KeyFrame(Duration.millis(50 * idx), ev -> {
                    int r = cell[0], c = cell[1];
                    if (grid[r][c].getState() != CellState.START &&
                        grid[r][c].getState() != CellState.END) {
                        grid[r][c].setState(CellState.PATH);
                        grid[r][c].animateTo(Cell.COLOR_PATH);
                    }
                });
                pathTimeline.getKeyFrames().add(kf);
            }

            pathTimeline.setOnFinished(ev -> {
                statusLabel.setText("✅ " + result.algorithm() + " complete!");
                statsLabel.setText(
                        "Visited: " + visited.size() +
                        "  |  Path length: " + path.size() +
                        "  |  Cost: " + result.totalCost()
                );
                isRunning = false;
                visualizeBtn.setDisable(false);
            });
            pathTimeline.play();
        });

        visitTimeline.play();
    }

    // ─── Helpers ──────────────────────────────────────────────────────
    private void clearPath() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                CellState s = grid[r][c].getState();
                if (s == CellState.VISITED || s == CellState.PATH)
                    grid[r][c].setState(CellState.EMPTY);
            }
        }
        statsLabel.setText("");
        statusLabel.setText("Path cleared.");
    }

    private void resetGrid() {
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++)
                grid[r][c].setState(CellState.EMPTY);
        startCell = null;
        endCell = null;
        statsLabel.setText("");
        statusLabel.setText("Grid reset. Place start and end nodes.");
        setDefaultStartEnd();
    }
}