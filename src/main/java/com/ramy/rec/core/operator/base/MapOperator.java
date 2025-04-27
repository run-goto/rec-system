package com.ramy.rec.core.operator.base;


import com.ramy.rec.core.ComputeContext;
import com.ramy.rec.core.DataSet;
import com.ramy.rec.core.DataSource;
import com.ramy.rec.core.Operator;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MapOperator<I, O> implements Operator<I, O> {
    private final Function<I, O> mapper;

    public MapOperator(Function<I, O> mapper) {
        this.mapper = mapper;
    }

    @Override
    public DataSet<O> run(ComputeContext computeContext, DataSet<?>... inputs) {
        DataSet<I> datasource = (DataSet<I>) inputs[0];
        List<I> inputData = datasource.getData();
        List<O> mappedData = inputData.stream().map(mapper).collect(Collectors.toList());
        return new DataSet<>(mappedData);
    }
}