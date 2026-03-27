package com.pathvisualizer.fx.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080/api/path";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public record PathResponse(
            List<int[]> visitedOrder,
            List<int[]> shortestPath,
            boolean pathFound,
            int totalCost,
            String algorithm
    ) {}

    public PathResponse solve(int rows, int cols,
                               int[] start, int[] end,
                               List<int[]> walls,
                               String algorithm) throws Exception {
        ObjectNode body = mapper.createObjectNode();
        body.put("rows", rows);
        body.put("cols", cols);

        ArrayNode startArr = body.putArray("start");
        startArr.add(start[0]); startArr.add(start[1]);

        ArrayNode endArr = body.putArray("end");
        endArr.add(end[0]); endArr.add(end[1]);

        ArrayNode wallsArr = body.putArray("walls");
        for (int[] w : walls) {
            ArrayNode wArr = wallsArr.addArray();
            wArr.add(w[0]); wArr.add(w[1]);
        }

        body.put("algorithm", algorithm);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/solve"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200)
            throw new RuntimeException("API error: " + response.body());

        JsonNode json = mapper.readTree(response.body());
        return parseResponse(json);
    }

    private PathResponse parseResponse(JsonNode json) {
        List<int[]> visited = parseList(json.get("visitedOrder"));
        List<int[]> path    = parseList(json.get("shortestPath"));
        boolean found       = json.get("pathFound").asBoolean();
        int cost            = json.get("totalCost").asInt();
        String algo         = json.get("algorithm").asText();
        return new PathResponse(visited, path, found, cost, algo);
    }

    private List<int[]> parseList(JsonNode node) {
        List<int[]> list = new ArrayList<>();
        if (node == null || !node.isArray()) return list;
        for (JsonNode item : node) {
            list.add(new int[]{item.get(0).asInt(), item.get(1).asInt()});
        }
        return list;
    }
}