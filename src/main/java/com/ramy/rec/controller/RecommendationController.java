package com.ramy.rec.controller;


import com.ramy.rec.config.DAGConfigParser;
import com.ramy.rec.core.DataSet;
import com.ramy.rec.core.datasource.CSVDataSource;
import com.ramy.rec.core.Graph;
import com.ramy.rec.core.model.Movie;
import com.ramy.rec.core.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class RecommendationController {

    private final CSVDataSource csvDataSource;
    private final DAGConfigParser dagConfigParser;
    private Graph graph;

    @Autowired
    public RecommendationController(CSVDataSource csvDataSource, DAGConfigParser dagConfigParser) {
        this.csvDataSource = csvDataSource;
        this.dagConfigParser = dagConfigParser;
    }

    @PostConstruct
    public void init() {
        InputStream yamlStream = getClass().getResourceAsStream("/dag-config.yml");
        graph = dagConfigParser.parse(yamlStream);
    }

    @GetMapping("/recommend")
    public List<String> getRecommendations(@RequestParam int uid) {
        // Find the interestRecall node
        Node<?, ?> interestRecallNode = graph.getNodes().stream()
                .filter(node -> "interestRecall".equals(node.getName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("interestRecall node not found"));

        // Execute the interestRecall node with the required inputs
        DataSet<Movie> recommendedMovies = (DataSet<Movie>) interestRecallNode.execute(dagConfigParser.getComputeContext());

        return recommendedMovies.stream().map(Movie::getTitle).collect(Collectors.toList());
    }
}