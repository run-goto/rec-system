package com.ramy.rec.core.operator.biz;

import com.ramy.rec.core.ComputeContext;
import com.ramy.rec.core.DataSet;
import com.ramy.rec.core.DataSource;
import com.ramy.rec.core.Operator;
import com.ramy.rec.core.datasource.CSVDataSource;
import com.ramy.rec.core.model.Movie;

public class MovieRecallOperator implements Operator<String, Movie> {

    private final CSVDataSource csvDataSource;

    public MovieRecallOperator(CSVDataSource csvDataSource) {
        this.csvDataSource = csvDataSource;
    }

    @Override
    public DataSet<Movie> run(ComputeContext computeContext, DataSource<String> datasource, DataSet<?>... inputs) {
        return csvDataSource.getGlobalMovies();
    }
}
