package com.comandante.creeper.model;

import com.google.gson.GsonBuilder;
import org.mapdb.Serializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

public class PlayerMetadataSerializer implements Serializer<PlayerMetadata>, Serializable {

    @Override
    public void serialize(DataOutput out, PlayerMetadata value) throws IOException {
        out.writeUTF(new GsonBuilder().create().toJson(value, PlayerMetadata.class));
    }

    @Override
    public PlayerMetadata deserialize(DataInput in, int available) throws IOException {
        return new GsonBuilder().create().fromJson(in.readUTF(), PlayerMetadata.class);
    }

    @Override
    public int fixedSize() {
        return -1;
    }
}

