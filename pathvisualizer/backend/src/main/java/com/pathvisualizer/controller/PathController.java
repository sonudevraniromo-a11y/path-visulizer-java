package com.pathvisualizer.controller;

import com.pathvisualizer.model.GridRequest;
import com.pathvisualizer.model.PathResult;
import com.pathvisualizer.service.PathService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/path")
@CrossOrigin(origins = "*")
public class PathController {

    private final PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    /**
     * POST /api/path/solve
     * Body: GridRequest JSON
     * Returns: PathResult with visitedOrder + shortestPath
     */
    @PostMapping("/solve")
    public ResponseEntity<PathResult> solve(@RequestBody GridRequest request) {
        PathResult result = pathService.solve(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Path Visualizer API is running!");
    }
}