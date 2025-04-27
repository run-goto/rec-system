package com.ramy.rec.core.operator.base;


import com.ramy.rec.core.ComputeContext;
import com.ramy.rec.core.DataSet;
import com.ramy.rec.core.Operator;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JoinOperator<L, R, O> implements Operator<L, O> {
    private final Function<L, ?> keyExtractorL;
    private final Function<R, ?> keyExtractorR;
    private final BiFunction<L, R, O> resultMapper;

    public JoinOperator(Function<L, ?> keyExtractorL, Function<R, ?> keyExtractorR, BiFunction<L, R, O> resultMapper) {
        this.keyExtractorL = keyExtractorL;
        this.keyExtractorR = keyExtractorR;
        this.resultMapper = resultMapper;
    }

    @Override
    public DataSet<O> run(ComputeContext computeContext, DataSet<?>... inputs) {
        if (inputs.length != 1) {
            throw new IllegalArgumentException("JoinOperator requires exactly one input DataSet.");
        }

        DataSet<R> rightDataSet = (DataSet<R>) inputs[0];
        DataSet<L> leftDataSet = (DataSet<L>) inputs[1];
        List<L> leftData = leftDataSet.getData();
        List<R> rightData = rightDataSet.getData();

        List<O> joinedData = leftData.stream()
                .flatMap(leftItem -> rightData.stream()
                        .filter(rightItem -> keyExtractorL.apply(leftItem).equals(keyExtractorR.apply(rightItem)))
                        .map(rightItem -> resultMapper.apply(leftItem, rightItem)))
                .collect(Collectors.toList());

        return new DataSet<>(joinedData);
    }
}
