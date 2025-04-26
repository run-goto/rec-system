package com.ramy.rec.core.datasource;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.ramy.rec.core.DataSet;
import com.ramy.rec.core.DataSource;
import com.ramy.rec.core.model.Movie;
import com.ramy.rec.core.model.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Objects;

@Service
public class CSVDataSource implements DataSource<Rating> {

    private final CsvMapper csvMapper;
    private final CsvSchema movieSchema;
    private final CsvSchema ratingSchema;

    @Autowired
    private ApplicationContext applicationContext;

    public CSVDataSource() {
        this.csvMapper = new CsvMapper();
        this.movieSchema = csvMapper.schemaFor(Movie.class).withHeader();
        this.ratingSchema = csvMapper.schemaFor(Rating.class).withHeader();
    }

    @Override
    public List<Rating> getData() {
        try {
            URL url = ClassLoader.getSystemResource("./ml-lastest-small/ratings.csv");
            InputStream inputStream =  new FileInputStream(url.getPath());
            MappingIterator<Rating> readValues = csvMapper.readerWithTypedSchemaFor(Rating.class).readValues(inputStream);
            return readValues.readAll();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read movies.csv", e);
        }
    }

    public DataSet<Movie> getGlobalMovies() {

        try  {
            URL url = ClassLoader.getSystemResource("./ml-lastest-small/movies.csv");
            InputStream inputStream =  new FileInputStream(url.getPath());
            MappingIterator<Movie> readValues = csvMapper.readerWithTypedSchemaFor(Movie.class).readValues(inputStream);
            List<Movie> movies = readValues.readAll();
            return new DataSet<>(movies);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read movies.csv", e);
        }
    }

    public DataSet<Rating> getGlobalRatings() {
        return new DataSet<>(getData());
    }
}