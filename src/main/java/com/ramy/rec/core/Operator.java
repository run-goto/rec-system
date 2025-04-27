package com.ramy.rec.core;


public interface Operator<I, O> {
    DataSet<O> run(ComputeContext computeContext,  DataSet<?>... inputs);
}