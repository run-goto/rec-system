package com.ramy.rec.core.operator.biz;



import com.ramy.rec.core.ComputeContext;
import com.ramy.rec.core.DataSet;
import com.ramy.rec.core.DataSource;
import com.ramy.rec.core.model.Movie;
import com.ramy.rec.core.model.Rating;
import com.ramy.rec.core.Operator;
import com.ramy.rec.core.operator.base.FilterOperator;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class InterestRecallOperator implements Operator<Rating, Movie> {
    private final int targetUserId;

    public InterestRecallOperator(int targetUserId) {
        this.targetUserId = targetUserId;
    }

    @Override
    public DataSet<Movie> run(ComputeContext computeContext, DataSource<Rating> datasource, DataSet<?>... inputs) {
        if (inputs.length != 2) {
            throw new IllegalArgumentException("InterestRecallOperator requires exactly two input DataSets.");
        }

        DataSet<Movie> moviesDataSet = (DataSet<Movie>) inputs[0];
        DataSet<Rating> ratingsDataSet = (DataSet<Rating>) inputs[1];

        // Get rated movie IDs for the target user
        Set<Integer> ratedMovies = ratingsDataSet.getData().stream()
                .filter(r -> r.getUserId() == targetUserId)
                .map(Rating::getMovieId)
                .collect(Collectors.toSet());

        // Calculate genre preferences based on rated movies
        Map<String, Integer> genrePreferences = new HashMap<>();
        for (Integer movieId : ratedMovies) {
            Optional<Movie> movieOptional = moviesDataSet.getData().stream()
                    .filter(movie -> movie.getId() == movieId)
                    .findFirst();
            if (movieOptional.isPresent()) {
                Movie movie = movieOptional.get();
                for (String genre : movie.getGenres()) {
                    genrePreferences.put(genre, genrePreferences.getOrDefault(genre, 0) + 1);
                }
            }
        }

        // Sort genres by preference count in descending order
        List<String> preferredGenres = genrePreferences.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // Create a predicate to filter movies that match the preferred genres and have not been rated yet
        Predicate<Movie> moviePredicate = movie ->
                !ratedMovies.contains(movie.getId()) &&
                        movie.getGenres().stream().anyMatch(preferredGenres::contains);

        // Filter movies using FilterOperator
        FilterOperator<Movie> filterOperator = new FilterOperator<>(moviePredicate);
        DataSet<Movie> filteredMovies = filterOperator.run(computeContext, () -> moviesDataSet.getData());

        // Sort recommended movies based on their preferred genre index
        Comparator<Movie> movieComparator = Comparator.comparingInt(movie -> {
            int index = preferredGenres.indexOf(movie.getGenres().get(0));
            return index == -1 ? Integer.MAX_VALUE : index;
        });

        // Limit the number of recommended movies to 10
        List<Movie> recommendedMovies = filteredMovies.getData().stream()
                .sorted(movieComparator)
                .limit(10)
                .collect(Collectors.toList());

        return new DataSet<>(recommendedMovies);
    }
}