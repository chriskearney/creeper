package com.comandante.creeper.player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;

import java.io.IOException;
import java.io.Serializable;

public class PlayerMetadataSerializer implements Serializer<PlayerMetadata>, Serializable {

    private final static Gson GSON = new GsonBuilder().create();

    @Override
    public void serialize(@NotNull DataOutput2 out, @NotNull PlayerMetadata value) throws IOException {
        out.writeUTF(GSON.toJson(value, PlayerMetadata.class));

    }

    @Override
    public PlayerMetadata deserialize(@NotNull DataInput2 input, int available) throws IOException {
        return GSON.fromJson(input.readUTF(), PlayerMetadata.class);
    }

    @Override
    public int fixedSize() {
        return -1;
    }
}

