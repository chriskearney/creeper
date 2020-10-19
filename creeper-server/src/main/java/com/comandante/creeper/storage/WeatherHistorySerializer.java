package com.comandante.creeper.storage;

import com.comandante.creeper.player.PlayerMetadata;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;

import java.io.IOException;
import java.io.Serializable;

public class WeatherHistorySerializer implements Serializer<String>, Serializable {
    private final static Gson GSON = new GsonBuilder().create();

    @Override
    public void serialize(@NotNull DataOutput2 dataOutput2, @NotNull String s) throws IOException {

    }

    @Override
    public String deserialize(@NotNull DataInput2 dataInput2, int i) throws IOException {
        return null;
    }
}
