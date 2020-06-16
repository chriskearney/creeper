package com.comandante.creeper.bot.command;

import com.google.api.client.util.Lists;
import com.omertron.omdbapi.OMDBException;
import com.omertron.omdbapi.OmdbApi;
import com.omertron.omdbapi.model.OmdbVideoBasic;
import com.omertron.omdbapi.model.OmdbVideoFull;
import com.omertron.omdbapi.model.SearchResults;
import com.omertron.omdbapi.tools.OmdbBuilder;

import java.util.List;

public class OmdbManager {

    private final OmdbApi omdb = new OmdbApi();

    public List<String> getMovieInfo(String movieSearchString) throws OMDBException {
        SearchResults results = omdb.search(new OmdbBuilder().setSearchTerm(movieSearchString).build());
        if (!results.isResponse()) {
            return Lists.newArrayList();
        }
        List<String> resp = Lists.newArrayList();
        OmdbVideoBasic omdbVideoBasic = results.getResults().get(0);
        String title = omdbVideoBasic.getTitle();
        String year = omdbVideoBasic.getYear();
        String type = omdbVideoBasic.getType();

        OmdbVideoFull omdbVideoFull = omdb.getInfo(new OmdbBuilder()
                .setImdbId(omdbVideoBasic.getImdbID())
                .setPlotLong()
                .setTomatoesOn()
                .build());

        String imdbRating = omdbVideoFull.getImdbRating();
        String tomatoRating = omdbVideoFull.getTomatoRating();
        resp.add(title + " | " + year + " | " + type + " | imdb rating: " + imdbRating + " | rotten tomatoes: " + tomatoRating);
        resp.add(omdbVideoFull.getPlot());
        return resp;
    }
}
