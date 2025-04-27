package com.ramy.rec.core;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Node<I, O> {
    private final String name;
    private final Operator<I, O> operator;
    private final List<Node<?, ?>> dependencies;
    private final ExecutorService executorService;

    public Node(String name, Operator<I, O> operator, List<Node<?, ?>> dependencies, Map<String, Object> configs) {
        this.name = name;
        this.operator = operator;
        this.dependencies = dependencies;
        if (dependencies != null && !dependencies.isEmpty()) {
            this.executorService = Executors.newFixedThreadPool(dependencies.size());
        } else {
            this.executorService = Executors.newSingleThreadExecutor();
        }
    }

    public DataSet<O> execute(ComputeContext computeContext) {
        try {
            // 使用 ExecutorService 并行执行依赖节点的 execute 方法
            List<Future<DataSet<?>>> futures = new ArrayList<>();
            for (Node<?, ?> node : dependencies) {
                Future<? extends DataSet<?>> submit = executorService.submit(() -> node.execute(computeContext));
                futures.add((Future<DataSet<?>>) submit);
            }

            return operator.run(computeContext, futures.stream()
                    .map(future -> {
                        try {
                            return future.get();
                        } catch (InterruptedException | ExecutionException e) {
                            throw new RuntimeException("Error executing dependency", e);
                        }
                    }).toArray(DataSet<?>[]::new));
        } catch (Exception e) {
            throw new RuntimeException("Error during execution", e);
        } finally {
            // 关闭线程池
            executorService.shutdown();
        }
    }


    public String getName() {
        return name;
    }
}