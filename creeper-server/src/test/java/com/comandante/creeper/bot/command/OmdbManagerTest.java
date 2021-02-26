package com.comandante.creeper.bot.command;

import com.omertron.omdbapi.OMDBException;
import org.junit.Test;

import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.*;

public class OmdbManagerTest {

    @Test
    public void testOmdb() throws OMDBException {
        OmdbManager omdbManager = new OmdbManager("5a2b99f0");
        List<String> the_client = omdbManager.getMovieInfo("the client");
        the_client.forEach(System.out::println);
    }

}