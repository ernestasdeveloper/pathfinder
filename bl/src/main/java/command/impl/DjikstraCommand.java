package command.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import command.Command;
import pathfinder.model.graph.Edge;
import pathfinder.model.graph.Graph;
import pathfinder.model.graph.Vertex;

/**
 * Created by TT on 2016-11-18.
 */
public class DjikstraCommand implements Command<Vertex> {

    private static class DistanceValue {

        private int distanceNodeBetweenTarget;
        private Edge edgeToBelong;

        public DistanceValue(int distanceNodeBetweenTarget, Edge edgeToBelong) {
            this.distanceNodeBetweenTarget = distanceNodeBetweenTarget;
            this.edgeToBelong = edgeToBelong;
        }

        public int getDistanceNodeBetweenTarget() {
            return distanceNodeBetweenTarget;
        }

        public Edge getEdgeToBelong() {
            return edgeToBelong;
        }
    }

    private final Set<Vertex> nodes;
    private final Set<Edge> edges;
    private Set<Vertex> settledNodes;
    private Set<Vertex> unSettledNodes;
    private Map<Vertex, Vertex> predecessors;
    private Map<Vertex, Integer> distance;

    public DjikstraCommand(Graph graph) {
        // create a copy of the array so that we can operate on this array
        this.nodes = new LinkedHashSet<>(graph.getVertices());
        this.edges = new LinkedHashSet<>(graph.getEdges());
    }

    public static Set<Vertex> getShortestPath(Vertex rootNode, Vertex currentNode, Graph graph) {
        DjikstraCommand command = new DjikstraCommand(graph);
        command.execute(rootNode);
        return command.getPath(currentNode);
    }

    public void execute(Vertex source) {
        settledNodes = new LinkedHashSet<>();
        unSettledNodes = new LinkedHashSet<>();
        distance = new HashMap<Vertex, Integer>();
        predecessors = new HashMap<Vertex, Vertex>();
        distance.put(source, 0);
        unSettledNodes.add(source);
        while (unSettledNodes.size() > 0) {
            Vertex node = getMinimum(unSettledNodes);
            settledNodes.add(node);
            unSettledNodes.remove(node);
            findMinimalDistances(node);
        }
    }

    private void findMinimalDistances(Vertex node) {
        List<Vertex> adjacentNodes = getNeighbors(node);
        for (Vertex target : adjacentNodes) {
            int currentShortestDist = getShortestDistance(target);
            int nodeDist = getShortestDistance(node);
            int distNodeBetweenTarget = getDistance(node, target);
            if (currentShortestDist > (nodeDist + distNodeBetweenTarget)) {
                distance.put(target, (nodeDist + distNodeBetweenTarget));
                predecessors.put(target, node);
                unSettledNodes.add(target);
            }
        }

    }

    private int getDistance(Vertex node, Vertex target) {
        for (Edge edge : edges) {
            if (edge.getSourceNode().equals(node) && edge.getDestNode().equals(target)) {
                return edge.getDistance();
            }
        }
        throw new RuntimeException("Should not happen");
    }

    private List<Vertex> getNeighbors(Vertex node) {
        List<Vertex> neighbors = new ArrayList<Vertex>();
        for (Edge edge : edges) {
            if (edge.getSourceNode().equals(node)
                    && !isSettled(edge.getDestNode())) {
                neighbors.add(edge.getDestNode());
            }
        }
        return neighbors;
    }

    private Vertex getMinimum(Set<Vertex> vertexes) {
        Vertex minimum = null;
        for (Vertex vertex : vertexes) {
            if (minimum == null) {
                minimum = vertex;
            } else {
                if (getShortestDistance(vertex) < getShortestDistance(minimum)) {
                    minimum = vertex;
                }
            }
        }
        return minimum;
    }

    private boolean isSettled(Vertex vertex) {
        return settledNodes.contains(vertex);
    }

    private int getShortestDistance(Vertex destination) {
        Integer d = distance.get(destination);
        if (d == null) {
            return Integer.MAX_VALUE;
        } else {
            return d;
        }
    }

    /*
     * This method returns the path from the source to the selected target and
     * NULL if no path exists
     */
    public Set<Vertex> getPath(Vertex target) {
        LinkedList<Vertex> path = new LinkedList<Vertex>();
        Vertex step = target;
        // check if a path exists
        if (predecessors.get(step) == null) {
            return null;
        }
        path.add(step);
        while (predecessors.get(step) != null) {
            step = predecessors.get(step);
            path.add(step);
        }
        // Put it into the correct order
        Collections.reverse(path);
        return new LinkedHashSet(path);
    }

    public Set<Edge> getEdges(Vertex endNode) {
        return getEdges(getPath(endNode));
    }

    public Set<Edge> getEdges(Set<Vertex> vertices) {
        List<Vertex> verticesList = new ArrayList<>(vertices);
        Set<Edge> result = new LinkedHashSet<>(edges.size());
        for (int i = 0; i < verticesList.size()-1; i++) {
            Vertex src = verticesList.get(i);
            Vertex dst = verticesList.get(i+1);
            for (Edge edge : edges) {
                if (edge.getSourceNode().equals(src) && edge.getDestNode().equals(dst)) {
                    result.add(edge);
                }
            }
        }
        return result;
    }
}
