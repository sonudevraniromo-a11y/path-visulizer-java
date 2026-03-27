package com.pathvisualizer.model;

import java.util.List;

public class GridRequest {
    private int rows;
    private int cols;
    private int[] start;   // [row, col]
    private int[] end;     // [row, col]
    private List<int[]> walls;
    private String algorithm; // BFS, DFS, DIJKSTRA, ASTAR

    public int getRows() { return rows; }
    public void setRows(int rows) { this.rows = rows; }

    public int getCols() { return cols; }
    public void setCols(int cols) { this.cols = cols; }

    public int[] getStart() { return start; }
    public void setStart(int[] start) { this.start = start; }

    public int[] getEnd() { return end; }
    public void setEnd(int[] end) { this.end = end; }

    public List<int[]> getWalls() { return walls; }
    public void setWalls(List<int[]> walls) { this.walls = walls; }

    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
}