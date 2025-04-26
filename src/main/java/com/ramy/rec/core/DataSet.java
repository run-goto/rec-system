package com.ramy.rec.core;

import java.util.List;
import java.util.stream.Stream;

public class DataSet<T> {
    private final List<T> data;

    public DataSet(List<T> data) {
        this.data = data;
    }

    public Stream<T> stream() {
        return data.stream();
    }

    public void collect(List<T> target) {
        target.addAll(data);
    }

    public List<T> getData() {
        return data;
    }
}
