package com.pathvisualizer.algorithm;

import com.pathvisualizer.model.GridRequest;
import com.pathvisualizer.model.PathResult;

import java.util.*;

public class BFS {

    public static PathResult solve(GridRequest req) {
        int rows = req.getRows();
        int cols = req.getCols();
        boolean[][] walls = buildWallGrid(rows, cols, req.getWalls());

        int sr = req.getStart()[0], sc = req.getStart()[1];
        int er = req.getEnd()[0],   ec = req.getEnd()[1];

        boolean[][] visited = new boolean[rows][cols];
        int[][] prev = new int[rows * cols][2];
        for (int[] p : prev) Arrays.fill(p, -1);

        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};
        Queue<int[]> queue = new LinkedList<>();
        List<int[]> visitedOrder = new ArrayList<>();

        queue.add(new int[]{sr, sc});
        visited[sr][sc] = true;

        boolean found = false;
        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int r = cur[0], c = cur[1];
            visitedOrder.add(new int[]{r, c});

            if (r == er && c == ec) { found = true; break; }

            for (int[] d : dirs) {
                int nr = r + d[0], nc = c + d[1];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                        && !visited[nr][nc] && !walls[nr][nc]) {
                    visited[nr][nc] = true;
                    prev[nr * cols + nc] = new int[]{r, c};
                    queue.add(new int[]{nr, nc});
                }
            }
        }

        List<int[]> path = reconstructPath(prev, cols, sr, sc, er, ec, found);
        return new PathResult(visitedOrder, path, found, path.size(), "BFS");
    }

    static boolean[][] buildWallGrid(int rows, int cols, List<int[]> wallList) {
        boolean[][] walls = new boolean[rows][cols];
        if (wallList != null)
            for (int[] w : wallList) walls[w[0]][w[1]] = true;
        return walls;
    }

    static List<int[]> reconstructPath(int[][] prev, int cols,
                                        int sr, int sc, int er, int ec, boolean found) {
        List<int[]> path = new ArrayList<>();
        if (!found) return path;
        int[] cur = {er, ec};
        while (!(cur[0] == sr && cur[1] == sc)) {
            path.add(0, cur);
            cur = prev[cur[0] * cols + cur[1]];
        }
        path.add(0, new int[]{sr, sc});
        return path;
    }
}