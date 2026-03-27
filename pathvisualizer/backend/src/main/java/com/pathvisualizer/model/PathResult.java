package com.pathvisualizer.model;

import java.util.List;

public class PathResult {
    private List<int[]> visitedOrder;  // cells in order they were visited
    private List<int[]> shortestPath;  // final path from start to end
    private boolean pathFound;
    private int totalCost;
    private String algorithm;

    public PathResult(List<int[]> visitedOrder, List<int[]> shortestPath,
                      boolean pathFound, int totalCost, String algorithm) {
        this.visitedOrder = visitedOrder;
        this.shortestPath = shortestPath;
        this.pathFound = pathFound;
        this.totalCost = totalCost;
        this.algorithm = algorithm;
    }

    public List<int[]> getVisitedOrder() { return visitedOrder; }
    public List<int[]> getShortestPath() { return shortestPath; }
    public boolean isPathFound() { return pathFound; }
    public int getTotalCost() { return totalCost; }
    public String getAlgorithm() { return algorithm; }
}