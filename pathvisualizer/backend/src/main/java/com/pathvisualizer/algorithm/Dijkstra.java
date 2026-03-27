package com.pathvisualizer.algorithm;

import com.pathvisualizer.model.GridRequest;
import com.pathvisualizer.model.PathResult;

import java.util.*;

public class Dijkstra {

    public static PathResult solve(GridRequest req) {
        int rows = req.getRows();
        int cols = req.getCols();
        boolean[][] walls = BFS.buildWallGrid(rows, cols, req.getWalls());

        int sr = req.getStart()[0], sc = req.getStart()[1];
        int er = req.getEnd()[0],   ec = req.getEnd()[1];

        int[] dist = new int[rows * cols];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[sr * cols + sc] = 0;

        int[][] prev = new int[rows * cols][2];
        for (int[] p : prev) Arrays.fill(p, -1);

        // PriorityQueue: [dist, row, col]
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        pq.offer(new int[]{0, sr, sc});

        boolean[] settled = new boolean[rows * cols];
        List<int[]> visitedOrder = new ArrayList<>();
        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};
        boolean found = false;

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int d = cur[0], r = cur[1], c = cur[2];
            int idx = r * cols + c;

            if (settled[idx]) continue;
            settled[idx] = true;
            visitedOrder.add(new int[]{r, c});

            if (r == er && c == ec) { found = true; break; }

            for (int[] dir : dirs) {
                int nr = r + dir[0], nc = c + dir[1];
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                if (walls[nr][nc] || settled[nr * cols + nc]) continue;

                int newDist = d + 1;
                if (newDist < dist[nr * cols + nc]) {
                    dist[nr * cols + nc] = newDist;
                    prev[nr * cols + nc] = new int[]{r, c};
                    pq.offer(new int[]{newDist, nr, nc});
                }
            }
        }

        List<int[]> path = BFS.reconstructPath(prev, cols, sr, sc, er, ec, found);
        return new PathResult(visitedOrder, path, found,
                found ? dist[er * cols + ec] : -1, "DIJKSTRA");
    }
}