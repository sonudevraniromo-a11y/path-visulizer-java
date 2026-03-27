package com.pathvisualizer.algorithm;

import com.pathvisualizer.model.GridRequest;
import com.pathvisualizer.model.PathResult;

import java.util.*;

public class DFS {

    public static PathResult solve(GridRequest req) {
        int rows = req.getRows();
        int cols = req.getCols();
        boolean[][] walls = BFS.buildWallGrid(rows, cols, req.getWalls());

        int sr = req.getStart()[0], sc = req.getStart()[1];
        int er = req.getEnd()[0],   ec = req.getEnd()[1];

        boolean[][] visited = new boolean[rows][cols];
        int[][] prev = new int[rows * cols][2];
        for (int[] p : prev) Arrays.fill(p, -1);

        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};
        Deque<int[]> stack = new ArrayDeque<>();
        List<int[]> visitedOrder = new ArrayList<>();

        stack.push(new int[]{sr, sc});
        visited[sr][sc] = true;

        boolean found = false;
        while (!stack.isEmpty()) {
            int[] cur = stack.pop();
            int r = cur[0], c = cur[1];
            visitedOrder.add(new int[]{r, c});

            if (r == er && c == ec) { found = true; break; }

            for (int[] d : dirs) {
                int nr = r + d[0], nc = c + d[1];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                        && !visited[nr][nc] && !walls[nr][nc]) {
                    visited[nr][nc] = true;
                    prev[nr * cols + nc] = new int[]{r, c};
                    stack.push(new int[]{nr, nc});
                }
            }
        }

        List<int[]> path = BFS.reconstructPath(prev, cols, sr, sc, er, ec, found);
        return new PathResult(visitedOrder, path, found, path.size(), "DFS");
    }
}