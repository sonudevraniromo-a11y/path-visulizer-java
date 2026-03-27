package com.pathvisualizer.service;

import com.pathvisualizer.algorithm.AStar;
import com.pathvisualizer.algorithm.BFS;
import com.pathvisualizer.algorithm.DFS;
import com.pathvisualizer.algorithm.Dijkstra;
import com.pathvisualizer.model.GridRequest;
import com.pathvisualizer.model.PathResult;
import org.springframework.stereotype.Service;

@Service
public class PathService {

    public PathResult solve(GridRequest request) {
        return switch (request.getAlgorithm().toUpperCase()) {
            case "BFS"      -> BFS.solve(request);
            case "DFS"      -> DFS.solve(request);
            case "DIJKSTRA" -> Dijkstra.solve(request);
            case "ASTAR"    -> AStar.solve(request);
            default -> throw new IllegalArgumentException(
                    "Unknown algorithm: " + request.getAlgorithm());
        };
    }
}