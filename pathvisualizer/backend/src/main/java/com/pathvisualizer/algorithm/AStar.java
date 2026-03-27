package com.pathvisualizer.algorithm;

import com.pathvisualizer.model.GridRequest;
import com.pathvisualizer.model.PathResult;

import java.util.*;

public class AStar {

    private static int heuristic(int r, int c, int er, int ec) {
        return Math.abs(r - er) + Math.abs(c - ec); // Manhattan distance
    }

    public static PathResult solve(GridRequest req) {
        int rows = req.getRows();
        int cols = req.getCols();
        boolean[][] walls = BFS.buildWallGrid(rows, cols, req.getWalls());

        int sr = req.getStart()[0], sc = req.getStart()[1];
        int er = req.getEnd()[0],   ec = req.getEnd()[1];

        int[] gScore = new int[rows * cols];
        int[] fScore = new int[rows * cols];
        Arrays.fill(gScore, Integer.MAX_VALUE);
        Arrays.fill(fScore, Integer.MAX_VALUE);
        gScore[sr * cols + sc] = 0;
        fScore[sr * cols + sc] = heuristic(sr, sc, er, ec);

        int[][] prev = new int[rows * cols][2];
        for (int[] p : prev) Arrays.fill(p, -1);

        PriorityQueue<int[]> openSet = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        openSet.offer(new int[]{fScore[sr * cols + sc], sr, sc});

        boolean[] closed = new boolean[rows * cols];
        List<int[]> visitedOrder = new ArrayList<>();
        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};
        boolean found = false;

        while (!openSet.isEmpty()) {
            int[] cur = openSet.poll();
            int r = cur[1], c = cur[2];
            int idx = r * cols + c;

            if (closed[idx]) continue;
            closed[idx] = true;
            visitedOrder.add(new int[]{r, c});

            if (r == er && c == ec) { found = true; break; }

            for (int[] dir : dirs) {
                int nr = r + dir[0], nc = c + dir[1];
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                if (walls[nr][nc] || closed[nr * cols + nc]) continue;

                int tentativeG = gScore[idx] + 1;
                int nIdx = nr * cols + nc;
                if (tentativeG < gScore[nIdx]) {
                    gScore[nIdx] = tentativeG;
                    fScore[nIdx] = tentativeG + heuristic(nr, nc, er, ec);
                    prev[nIdx] = new int[]{r, c};
                    openSet.offer(new int[]{fScore[nIdx], nr, nc});
                }
            }
        }

        List<int[]> path = BFS.reconstructPath(prev, cols, sr, sc, er, ec, found);
        return new PathResult(visitedOrder, path, found,
                found ? gScore[er * cols + ec] : -1, "ASTAR");
    }
}