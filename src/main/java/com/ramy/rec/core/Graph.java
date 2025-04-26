package com.ramy.rec.core;


import java.util.List;
import java.util.Map;

public class Graph {
    private final String name;
    private final String type;
    private final Map<String, Object> configs;
    private final List<Node<?, ?>> nodes;

    public Graph(String name, String type, Map<String, Object> configs, List<Node<?, ?>> nodes) {
        this.name = name;
        this.type = type;
        this.configs = configs;
        this.nodes = nodes;
    }

    public void execute(ComputeContext computeContext) {
        for (Node<?, ?> node : nodes) {
            node.execute(computeContext);
        }
    }

    public List<Node<?, ?>> getNodes() {
        return nodes;
    }
}