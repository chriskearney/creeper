package com.comandante.creeper.model;

import com.google.gson.GsonBuilder;
import org.mapdb.Serializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

public class ItemSerializer implements Serializer<Item>, Serializable {
    @Override
    public void serialize(DataOutput out, Item value) throws IOException {
        out.writeUTF(new GsonBuilder().create().toJson(value, Item.class));
    }

    @Override
    public Item deserialize(DataInput in, int available) throws IOException {
        return new GsonBuilder().create().fromJson(in.readUTF(), Item.class);
    }

    @Override
    public int fixedSize() {
        return -1;
    }
}
