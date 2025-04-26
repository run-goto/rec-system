package com.ramy.rec.core;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class Node<I, O> {
    private final String name;
    private final Operator<I, O> operator;
    private final List<Node<?, ?>> dependencies;
    private final Map<String, Object> configs;
    private final ExecutorService executorService;

    public Node(String name, Operator<I, O> operator, List<Node<?, ?>> dependencies, Map<String, Object> configs) {
        this.name = name;
        this.operator = operator;
        this.dependencies = dependencies;
        this.configs = configs;
        if (dependencies != null && !dependencies.isEmpty()) {
            this.executorService = Executors.newFixedThreadPool(dependencies.size());
        }else {
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

            // 获取所有依赖节点的执行结果

            // 获取数据源，增加类型安全检查
            DataSource<I> dataSource = getDataSource(configs);

            return operator.run(computeContext, dataSource, futures.stream()
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

    // 提取数据源获取逻辑，增强类型安全和可维护性
    private DataSource<I> getDataSource(Map<String, Object> configs) {
        Object dataSourceObj = configs.getOrDefault("dataSource", Collections.emptyList());
        if (!(dataSourceObj instanceof List<?>)) {
            throw new IllegalArgumentException("Invalid data source type. Expected List, but got " + dataSourceObj.getClass().getName());
        }
        @SuppressWarnings("unchecked")
        List<I> dataSourceList = (List<I>) dataSourceObj;
        return () -> dataSourceList;
    }


    public String getName() {
        return name;
    }
}