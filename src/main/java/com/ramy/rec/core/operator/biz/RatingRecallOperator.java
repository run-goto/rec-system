package com.ramy.rec.core.operator.biz;

import com.ramy.rec.core.ComputeContext;
import com.ramy.rec.core.DataSet;
import com.ramy.rec.core.DataSource;
import com.ramy.rec.core.Operator;
import com.ramy.rec.core.datasource.CSVDataSource;
import com.ramy.rec.core.model.Movie;
import com.ramy.rec.core.model.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


public class RatingRecallOperator implements Operator<String, Rating> {

    private CSVDataSource csvDataSource;

    public RatingRecallOperator(CSVDataSource csvDataSource) {
        this.csvDataSource = csvDataSource;
    }

    @Override
    public DataSet<Rating> run(ComputeContext computeContext, DataSource<String> datasource, DataSet<?>... inputs) {
        return csvDataSource.getGlobalRatings();
    }
}
