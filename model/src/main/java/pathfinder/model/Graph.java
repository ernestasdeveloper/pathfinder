package pathfinder.model;

import java.util.List;

/**
 * Created by TT on 2016-11-18.
 */
public class Graph {

    private List<Vertex> vertices;
    private List<Edge> edges;

    public Graph(List<Vertex> vertices, List<Edge> edges) {
        this.vertices = vertices;
        this.edges = edges;
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public List<Edge> getEdges() {
        return edges;
    }
}