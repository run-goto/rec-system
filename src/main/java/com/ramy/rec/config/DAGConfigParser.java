package com.ramy.rec.config;



import com.ramy.rec.core.ComputeContext;
import com.ramy.rec.core.datasource.CSVDataSource;
import com.ramy.rec.core.Graph;
import com.ramy.rec.core.Node;
import com.ramy.rec.core.Operator;
import com.ramy.rec.core.operator.base.FilterOperator;
import com.ramy.rec.core.operator.biz.InterestRecallOperator;
import com.ramy.rec.core.operator.biz.MovieRecallOperator;
import com.ramy.rec.core.operator.biz.RatingRecallOperator;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;
import java.util.function.Predicate;

public class DAGConfigParser {

    private final CSVDataSource csvDataSource;
    private final ComputeContext computeContext;

    public DAGConfigParser(CSVDataSource csvDataSource) {
        this.csvDataSource = csvDataSource;
        this.computeContext = new ComputeContext(csvDataSource);
    }

    public Graph parse(InputStream yamlStream) {
        Yaml yaml = new Yaml();
        Map<String, Object> config = yaml.load(yamlStream);

        String name = (String) config.get("name");
        String type = (String) config.get("type");
        Map<String, Object> configs = (Map<String, Object>) config.get("configs");
        List<Map<String, Object>> nodesConfig = (List<Map<String, Object>>) config.get("nodes");

        Map<String, Node<?, ?>> nodeMap = new HashMap<>();

        for (Map<String, Object> nodeConfig : nodesConfig) {
            String nodeName = (String) nodeConfig.get("name");
            String opClass = (String) nodeConfig.get("op");
            List<String> dependsNames = (List<String>) nodeConfig.get("depends");
            Map<String, Object> nodeConfigs = (Map<String, Object>) nodeConfig.getOrDefault("configs", Collections.emptyMap());

            Operator<?, ?> operator = createOperator(opClass, nodeConfigs);

            List<Node<?, ?>> dependencies = new ArrayList<>();
            if (dependsNames != null) {
                for (String dependName : dependsNames) {
                    dependencies.add(nodeMap.get(dependName));
                }
            }

            Node<?, ?> node = new Node<>(nodeName, operator, dependencies, nodeConfigs);
            nodeMap.put(nodeName, node);
        }

        List<Node<?, ?>> nodes = new ArrayList<>(nodeMap.values());
        return new Graph(name, type, configs, nodes);
    }

    private Operator<?, ?> createOperator(String className, Map<String, Object> configs) {
        switch (className) {
            case "InterestRecallOperator":
                int targetUserId = (int) configs.get("targetUserId");
                return new InterestRecallOperator(targetUserId);
            case "JoinOperator":
                throw new UnsupportedOperationException("JoinOperator not implemented yet.");
            case "FilterOperator":
                Predicate<Object> predicate = (Predicate<Object>) configs.get("predicate");
                return new FilterOperator<>(predicate);
            case "movieRecallOperator":
                return new MovieRecallOperator(csvDataSource);
            case "ratingRecallOperator":
                return new RatingRecallOperator(csvDataSource);
            case "MapOperator":
                throw new UnsupportedOperationException("MapOperator not implemented yet.");
            default:
                throw new IllegalArgumentException("Unknown operator class: " + className);
        }
    }

    public ComputeContext getComputeContext() {
        return computeContext;
    }
}