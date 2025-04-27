package com.ramy.rec.core.operator.base;


import com.ramy.rec.core.ComputeContext;
import com.ramy.rec.core.DataSet;
import com.ramy.rec.core.DataSource;
import com.ramy.rec.core.Operator;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FilterOperator<T> implements Operator<T, T> {
    private final Predicate<T> predicate;

    public FilterOperator(Predicate<T> predicate) {
        this.predicate = predicate;
    }

    @Override
    public DataSet<T> run(ComputeContext computeContext, DataSet<?>... inputs) {
        DataSet<?> dataSet = inputs[0];
        List<T> inputData = (List<T>) dataSet.getData();
        List<T> filteredData = inputData.stream().filter(predicate).collect(Collectors.toList());
        return new DataSet<>(filteredData);
    }
}